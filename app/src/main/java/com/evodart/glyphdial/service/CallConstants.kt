package com.evodart.glyphdial.service

/**
 * Centralized constants for the Call Service and Notification Actions.
 */
object CallConstants {
    const val ACTION_ANSWER    = "com.evodart.glyphdial.ACTION_ANSWER"
    const val ACTION_DECLINE   = "com.evodart.glyphdial.ACTION_DECLINE"
    const val ACTION_HANG_UP   = "com.evodart.glyphdial.ACTION_HANG_UP"
    const val ACTION_MUTE      = "com.evodart.glyphdial.ACTION_MUTE"
    const val ACTION_SPEAKER   = "com.evodart.glyphdial.ACTION_SPEAKER"
    const val ACTION_MERGE     = "com.evodart.glyphdial.ACTION_MERGE"

    // Notification Channels
    const val CHANNEL_INCOMING = "glyph_dial_incoming"
    const val CHANNEL_ACTIVE   = "glyph_dial_active"
    const val CHANNEL_MISSED   = "glyph_dial_missed"

    // Notification IDs
    const val NOTIFICATION_ID        = 1001
    const val NOTIFICATION_MISSED_ID = 1002
}

