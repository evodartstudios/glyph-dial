package com.evodart.glyphdial.ui.navigation

/**
 * Navigation routes for the app
 */
object Routes {
    // Main screens (bottom nav)
    const val DIAL = "dial"
    const val RECENTS = "recents"
    const val CONTACTS = "contacts"
    const val FAVORITES = "favorites"
    
    // Settings
    const val SETTINGS = "settings"
    const val SETTINGS_BLOCKED = "settings/blocked"
    const val SETTINGS_SPEED_DIAL = "settings/speed-dial"
    const val SETTINGS_DISPLAY = "settings/display"
    const val SETTINGS_GLYPH = "settings/glyph"
    
    // Contact details
    const val CONTACT_DETAIL = "contact/{contactId}"
    const val CONTACT_EDIT = "contact/{contactId}/edit"
    const val CONTACT_NEW = "contact/new"
    
    // Call screens
    const val CALL_INCOMING = "call/incoming"
    const val CALL_OUTGOING = "call/outgoing"
    const val CALL_ACTIVE = "call/active"
    const val CALL_ENDED = "call/ended"
    
    // Utility screens
    const val SEARCH = "search"
    const val RECORDINGS = "recordings"
    const val STATS = "stats"
    
    // Helper functions for navigation with arguments
    fun contactDetail(contactId: String) = "contact/$contactId"
    fun contactEdit(contactId: String) = "contact/$contactId/edit"
}

/**
 * Navigation argument keys
 */
object NavArgs {
    const val CONTACT_ID = "contactId"
    const val PHONE_NUMBER = "phoneNumber"
    const val CALL_ID = "callId"
}
