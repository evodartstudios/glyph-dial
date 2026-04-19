package com.evodart.glyphdial.data.repository

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.data.model.PhoneNumber
import com.evodart.glyphdial.data.model.PhoneNumberType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ContactRepository"

/**
 * Repository for accessing device contacts.
 * Uses single-query approach against Phone.CONTENT_URI to avoid N+1 queries.
 */
@Singleton
class ContactRepository @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver

    // Shared projection for Phone.CONTENT_URI queries
    private val phoneProjection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
        ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
        ContactsContract.CommonDataKinds.Phone.STARRED,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.TYPE,
        ContactsContract.CommonDataKinds.Phone.LABEL
    )

    /**
     * Get all contacts reactively — re-emits whenever the contacts database changes.
     */
    fun getAllContacts(): Flow<List<Contact>> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(fetchAllContacts())
            }
        }
        contentResolver.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI, true, observer
        )
        // Initial load
        trySend(fetchAllContacts())
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }.flowOn(Dispatchers.IO)

    private fun fetchAllContacts(): List<Contact> {
        return try {
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                phoneProjection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"
            )
            buildContactsFromPhoneCursor(cursor)
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied reading contacts", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading contacts", e)
            emptyList()
        }
    }

    /**
     * Search contacts by name or number using a single query.
     */
    fun searchContacts(query: String): Flow<List<Contact>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }

        try {
            val searchQuery = "%$query%"
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                phoneProjection,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} LIKE ? OR ${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?",
                arrayOf(searchQuery, searchQuery),
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"
            )

            val contacts = buildContactsFromPhoneCursor(cursor)
            emit(contacts)
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied searching contacts", e)
            emit(emptyList())
        } catch (e: Exception) {
            Log.e(TAG, "Error searching contacts: query='$query'", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get starred/favorite contacts reactively — re-emits on any contacts change.
     */
    fun getStarredContacts(): Flow<List<Contact>> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(fetchStarredContacts())
            }
        }
        contentResolver.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI, true, observer
        )
        trySend(fetchStarredContacts())
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }.flowOn(Dispatchers.IO)

    private fun fetchStarredContacts(): List<Contact> {
        return try {
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                phoneProjection,
                "${ContactsContract.CommonDataKinds.Phone.STARRED} = 1",
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"
            )
            buildContactsFromPhoneCursor(cursor)
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied reading starred contacts", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading starred contacts", e)
            emptyList()
        }
    }

    /**
     * Build Contact list from a Phone.CONTENT_URI cursor.
     * Groups rows by CONTACT_ID and deduplicates phone numbers.
     */
    private fun buildContactsFromPhoneCursor(cursor: android.database.Cursor?): List<Contact> {
        if (cursor == null) return emptyList()

        // Collect all phone rows grouped by contact ID
        val contactMap = linkedMapOf<Long, MutableList<PhoneRow>>()
        var firstName = mutableMapOf<Long, String>()
        var photoUris = mutableMapOf<Long, String?>()
        var starredMap = mutableMapOf<Long, Boolean>()

        cursor.use {
            val idCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
            val photoCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            val starredCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.STARRED)
            val numberCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val typeCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)
            val labelCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LABEL)

            while (it.moveToNext()) {
                val contactId = it.getLong(idCol)
                val number = it.getString(numberCol) ?: continue

                contactMap.getOrPut(contactId) { mutableListOf() }.add(
                    PhoneRow(
                        number = number,
                        type = it.getInt(typeCol),
                        label = it.getString(labelCol)
                    )
                )

                // Store contact-level info (same for all rows with same ID)
                if (contactId !in firstName) {
                    firstName[contactId] = it.getString(nameCol) ?: ""
                    photoUris[contactId] = it.getString(photoCol)
                    starredMap[contactId] = it.getInt(starredCol) == 1
                }
            }
        }

        // Build Contact objects
        return contactMap.map { (contactId, phoneRows) ->
            val phoneNumbers = phoneRows
                .map { row ->
                    PhoneNumber(
                        number = row.number,
                        type = mapPhoneType(row.type),
                        label = row.label
                    )
                }
                .distinctBy { it.number.filter { c -> c.isDigit() } }

            Contact(
                id = contactId,
                name = firstName[contactId] ?: "",
                phoneNumbers = phoneNumbers,
                photoUri = photoUris[contactId],
                starred = starredMap[contactId] ?: false
            )
        }
    }

    /**
     * Lookup a contact by phone number (for caller ID).
     * Handles country code differences by trying multiple formats.
     */
    suspend fun lookupContactByNumber(phoneNumber: String): Contact? {
        if (phoneNumber.isBlank()) return null

        val normalizedNumber = phoneNumber.filter { it.isDigit() }
        if (normalizedNumber.length < 4) return null

        val numbersToTry = mutableListOf<String>()
        numbersToTry.add(phoneNumber)
        numbersToTry.add(normalizedNumber)

        if (normalizedNumber.length >= 10) {
            numbersToTry.add(normalizedNumber.takeLast(10))
        }

        // Common country code prefixes
        if (normalizedNumber.startsWith("91") && normalizedNumber.length > 10) {
            numbersToTry.add(normalizedNumber.drop(2)) // India +91
        }
        if (normalizedNumber.startsWith("1") && normalizedNumber.length > 10) {
            numbersToTry.add(normalizedNumber.drop(1)) // US/Canada +1
        }
        if (normalizedNumber.startsWith("44") && normalizedNumber.length > 10) {
            numbersToTry.add(normalizedNumber.drop(2)) // UK +44
        }

        for (tryNumber in numbersToTry.distinct()) {
            try {
                val contact = lookupSingleNumber(tryNumber)
                if (contact != null) return contact
            } catch (e: SecurityException) {
                Log.e(TAG, "Permission denied during caller ID lookup", e)
                return null
            } catch (e: Exception) {
                Log.e(TAG, "Error in caller ID lookup for $tryNumber", e)
            }
        }

        return null
    }

    private fun lookupSingleNumber(phoneNumber: String): Contact? {
        val uri = android.net.Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            android.net.Uri.encode(phoneNumber)
        )

        val projection = arrayOf(
            ContactsContract.PhoneLookup._ID,
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup.PHOTO_URI,
            ContactsContract.PhoneLookup.STARRED
        )

        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val contactId = it.getLong(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)) ?: ""
                val photoUri = it.getString(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI))
                val starred = it.getInt(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.STARRED)) == 1

                return Contact(
                    id = contactId,
                    name = name,
                    phoneNumbers = listOf(PhoneNumber(phoneNumber, PhoneNumberType.MOBILE, null)),
                    photoUri = photoUri,
                    starred = starred
                )
            }
        }

        return null
    }

    private fun mapPhoneType(type: Int): PhoneNumberType {
        return when (type) {
            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> PhoneNumberType.MOBILE
            ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> PhoneNumberType.HOME
            ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> PhoneNumberType.WORK
            else -> PhoneNumberType.OTHER
        }
    }

    /** Internal helper for phone cursor rows */
    private data class PhoneRow(
        val number: String,
        val type: Int,
        val label: String?
    )
}
