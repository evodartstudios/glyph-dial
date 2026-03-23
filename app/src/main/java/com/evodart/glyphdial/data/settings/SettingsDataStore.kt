package com.evodart.glyphdial.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * App settings using DataStore
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object Keys {
        val DEFAULT_START_PAGE = stringPreferencesKey("default_start_page")
        val SCROLLBAR_POSITION = stringPreferencesKey("scrollbar_position")
        val SHOW_RECOMMENDATIONS = booleanPreferencesKey("show_recommendations")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
    }
    
    // Default start page
    val defaultStartPage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_START_PAGE] ?: "dial"
    }
    
    suspend fun setDefaultStartPage(page: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_START_PAGE] = page
        }
    }
    
    // Scrollbar position (left or right)
    val scrollbarPosition: Flow<ScrollbarPosition> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.SCROLLBAR_POSITION]) {
            "left" -> ScrollbarPosition.LEFT
            "right" -> ScrollbarPosition.RIGHT
            else -> ScrollbarPosition.RIGHT // default
        }
    }
    
    suspend fun setScrollbarPosition(position: ScrollbarPosition) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SCROLLBAR_POSITION] = position.name.lowercase()
        }
    }
    
    // Show recommendations when no results
    val showRecommendations: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_RECOMMENDATIONS] ?: true
    }
    
    suspend fun setShowRecommendations(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_RECOMMENDATIONS] = show
        }
    }
    
    // Accent color
    val accentColor: Flow<AccentColor> = context.dataStore.data.map { prefs ->
        AccentColor.fromString(prefs[Keys.ACCENT_COLOR])
    }
    
    suspend fun setAccentColor(color: AccentColor) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCENT_COLOR] = color.name.lowercase()
        }
    }
}

enum class ScrollbarPosition {
    LEFT, RIGHT
}

enum class AccentColor(val hex: Long) {
    RED(0xFFD71921),
    BLUE(0xFF2196F3),
    GREEN(0xFF4CAF50),
    ORANGE(0xFFFF9800),
    PURPLE(0xFF9C27B0),
    CYAN(0xFF00BCD4),
    PINK(0xFFE91E63),
    WHITE(0xFFFFFFFF);
    
    companion object {
        fun fromString(value: String?): AccentColor {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: RED
        }
    }
}

