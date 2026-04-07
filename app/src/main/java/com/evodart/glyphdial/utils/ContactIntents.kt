package com.evodart.glyphdial.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import com.evodart.glyphdial.data.model.Contact

object ContactIntents {

    /**
     * Launch the system intent to create a new contact, optionally pre-filling a phone number.
     */
    fun createContact(context: Context, phoneNumber: String? = null) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            if (!phoneNumber.isNullOrBlank()) {
                putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
            }
        }
        context.startActivity(intent)
    }

    /**
     * Open the system contact detail view for the given contact.
     */
    fun openContact(context: Context, contact: Contact) {
        val uri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_URI, contact.id.toString()
        )
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    /**
     * Launch the system intent to edit a contact by its ID.
     */
    fun editContact(context: Context, contact: Contact) {
        val uri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_URI, contact.id.toString()
        )
        val intent = Intent(Intent.ACTION_EDIT).apply {
            setDataAndType(uri, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
            putExtra("finishActivityOnSaveCompleted", true)
        }
        context.startActivity(intent)
    }

    /**
     * Launch the system content chooser to share a contact vCard.
     */
    fun shareContact(context: Context, contact: Contact) {
        val lookupUri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_URI, contact.id.toString()
        )
        // Resolve to a lookup key for a stable share URI
        context.contentResolver.query(
            lookupUri,
            arrayOf(ContactsContract.Contacts.LOOKUP_KEY),
            null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val lookupKey = cursor.getString(0)
                val vCardUri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = ContactsContract.Contacts.CONTENT_VCARD_TYPE
                    putExtra(Intent.EXTRA_STREAM, vCardUri)
                }
                context.startActivity(Intent.createChooser(intent, "Share contact via"))
            }
        }
    }

    // ── Legacy overloads kept for backward-compat ─────────────────────────────

    /** Edit contact by raw URI (kept for compatibility). */
    fun editContact(context: Context, contactUri: Uri) {
        val intent = Intent(Intent.ACTION_EDIT).apply {
            setDataAndType(contactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
            putExtra("finishActivityOnSaveCompleted", true)
        }
        context.startActivity(intent)
    }

    /** Share contact by raw lookup key (kept for compatibility). */
    fun shareContact(context: Context, lookupKey: String) {
        val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = ContactsContract.Contacts.CONTENT_VCARD_TYPE
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        context.startActivity(Intent.createChooser(intent, "Share contact via"))
    }
}
