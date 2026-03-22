package com.evodart.glyphdial.data.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.data.model.PhoneNumber
import com.evodart.glyphdial.data.model.PhoneNumberType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing device contacts
 */
@Singleton
class ContactRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver
    
    /**
     * Get all contacts with phone numbers
     */
    fun getAllContacts(): Flow<List<Contact>> = flow {
        val contacts = mutableListOf<Contact>()
        
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1",
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val contactId = it.getLong(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)) ?: ""
                val photoUri = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                val starred = it.getInt(it.getColumnIndexOrThrow(ContactsContract.Contacts.STARRED)) == 1
                
                val phoneNumbers = getPhoneNumbers(contactId)
                if (phoneNumbers.isNotEmpty()) {
                    contacts.add(
                        Contact(
                            id = contactId,
                            name = name,
                            phoneNumbers = phoneNumbers,
                            photoUri = photoUri,
                            starred = starred
                        )
                    )
                }
            }
        }
        
        emit(contacts)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get phone numbers for a contact
     */
    private fun getPhoneNumbers(contactId: Long): List<PhoneNumber> {
        val phoneNumbers = mutableListOf<PhoneNumber>()
        
        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.LABEL
            ),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null
        )
        
        phoneCursor?.use {
            while (it.moveToNext()) {
                val number = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: continue
                val type = it.getInt(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE))
                val label = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LABEL))
                
                phoneNumbers.add(
                    PhoneNumber(
                        number = number,
                        type = mapPhoneType(type),
                        label = label
                    )
                )
            }
        }
        
        return phoneNumbers.distinctBy { it.number.filter { c -> c.isDigit() } }
    }
    
    /**
     * Search contacts by name or number
     */
    fun searchContacts(query: String): Flow<List<Contact>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }
        
        val contacts = mutableListOf<Contact>()
        val searchQuery = "%$query%"
        
        // Search by name
        val nameCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.Contacts.STARRED
            ),
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ? AND ${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1",
            arrayOf(searchQuery),
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"
        )
        
        nameCursor?.use {
            while (it.moveToNext()) {
                val contactId = it.getLong(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)) ?: ""
                val photoUri = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                val starred = it.getInt(it.getColumnIndexOrThrow(ContactsContract.Contacts.STARRED)) == 1
                
                val phoneNumbers = getPhoneNumbers(contactId)
                if (phoneNumbers.isNotEmpty()) {
                    contacts.add(
                        Contact(
                            id = contactId,
                            name = name,
                            phoneNumbers = phoneNumbers,
                            photoUri = photoUri,
                            starred = starred
                        )
                    )
                }
            }
        }
        
        emit(contacts)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get starred/favorite contacts
     */
    fun getStarredContacts(): Flow<List<Contact>> = flow {
        val contacts = mutableListOf<Contact>()
        
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.Contacts.STARRED
            ),
            "${ContactsContract.Contacts.STARRED} = 1 AND ${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1",
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val contactId = it.getLong(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)) ?: ""
                val photoUri = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                
                val phoneNumbers = getPhoneNumbers(contactId)
                if (phoneNumbers.isNotEmpty()) {
                    contacts.add(
                        Contact(
                            id = contactId,
                            name = name,
                            phoneNumbers = phoneNumbers,
                            photoUri = photoUri,
                            starred = true
                        )
                    )
                }
            }
        }
        
        emit(contacts)
    }.flowOn(Dispatchers.IO)
    
    private fun mapPhoneType(type: Int): PhoneNumberType {
        return when (type) {
            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> PhoneNumberType.MOBILE
            ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> PhoneNumberType.HOME
            ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> PhoneNumberType.WORK
            else -> PhoneNumberType.OTHER
        }
    }
    
    /**
     * Lookup a contact by phone number (for caller ID)
     * Handles country code differences by trying multiple formats
     * Returns contact info if found, null otherwise
     */
    suspend fun lookupContactByNumber(phoneNumber: String): Contact? {
        if (phoneNumber.isBlank()) return null
        
        // Normalize number - keep only digits
        val normalizedNumber = phoneNumber.filter { it.isDigit() }
        if (normalizedNumber.length < 4) return null
        
        // Try different number formats for matching
        val numbersToTry = mutableListOf<String>()
        
        // Original number
        numbersToTry.add(phoneNumber)
        
        // Just digits
        numbersToTry.add(normalizedNumber)
        
        // Last 10 digits (local number without country code)
        if (normalizedNumber.length >= 10) {
            numbersToTry.add(normalizedNumber.takeLast(10))
        }
        
        // With common country code prefixes stripped
        if (normalizedNumber.startsWith("91") && normalizedNumber.length > 10) {
            numbersToTry.add(normalizedNumber.drop(2)) // India +91
        }
        if (normalizedNumber.startsWith("1") && normalizedNumber.length > 10) {
            numbersToTry.add(normalizedNumber.drop(1)) // US/Canada +1
        }
        if (normalizedNumber.startsWith("44") && normalizedNumber.length > 10) {
            numbersToTry.add(normalizedNumber.drop(2)) // UK +44
        }
        
        // Try each number format
        for (tryNumber in numbersToTry.distinct()) {
            val contact = lookupSingleNumber(tryNumber)
            if (contact != null) return contact
        }
        
        return null
    }
    
    private fun lookupSingleNumber(phoneNumber: String): Contact? {
        // Use phone lookup URI for matching
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
}
