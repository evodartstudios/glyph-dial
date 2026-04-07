package com.evodart.glyphdial.utils

/**
 * Known emergency numbers across global regions.
 * Used to bypass custom dialer routing and hand off to the system.
 */
object EmergencyNumbers {

    private val EMERGENCY_NUMBERS = setOf(
        // International
        "112", "911",
        // UK / Australia / NZ
        "999", "000",
        // Europe
        "110", "115", "117", "118",
        // Middle East / Asia
        "119", "120", "122", "123",
        // India
        "100", "101", "102", "108",
        // Mountain rescue / specialized
        "911",
    )

    // Also covers short patterns like "0911" entered with leading zero on some ROMs
    private val EMERGENCY_PATTERN = Regex(
        "^(112|911|999|000|110|115|117|118|119|120|122|123|100|101|102|108)$"
    )

    fun isEmergencyNumber(number: String): Boolean {
        val stripped = number.trimStart('0', '+').trim()
        return EMERGENCY_NUMBERS.contains(stripped) ||
                EMERGENCY_NUMBERS.contains(number.trim()) ||
                EMERGENCY_PATTERN.matches(stripped)
    }
}
