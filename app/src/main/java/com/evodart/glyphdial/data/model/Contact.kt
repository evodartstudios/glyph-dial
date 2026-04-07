package com.evodart.glyphdial.data.model

/**
 * Contact model representing a device contact
 */
data class Contact(
    val id: Long,
    val name: String,
    val phoneNumbers: List<PhoneNumber>,
    val photoUri: String? = null,
    val starred: Boolean = false
) {
    val displayName: String get() = name.ifEmpty { phoneNumbers.firstOrNull()?.number ?: "Unknown" }
    val primaryNumber: String? get() = phoneNumbers.firstOrNull()?.number
    val initials: String get() {
        val parts = name.split(" ").filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "#"
        }
    }
}

/**
 * Phone number with type
 */
data class PhoneNumber(
    val number: String,
    val type: PhoneNumberType = PhoneNumberType.MOBILE,
    val label: String? = null
)

/**
 * Phone number types
 */
enum class PhoneNumberType {
    MOBILE,
    HOME,
    WORK,
    OTHER
}
