package com.evodart.glyphdial.data.model

import java.time.Instant

/**
 * Call history entry
 */
data class CallLogEntry(
    val id: Long,
    val number: String,
    val name: String?,
    val type: CallType,
    val timestamp: Instant,
    val duration: Long, // seconds
    val photoUri: String? = null,
    val isRead: Boolean = true
) {
    val displayName: String get() = if (name.isNullOrBlank()) number else name
    val formattedDuration: String get() {
        if (duration == 0L) return ""
        val minutes = duration / 60
        val seconds = duration % 60
        return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
    }
}

/**
 * Call types
 */
enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED,
    REJECTED,
    BLOCKED,
    VOICEMAIL
}
