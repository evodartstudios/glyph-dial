package com.evodart.glyphdial.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evodart.glyphdial.data.settings.AccentColor
import com.evodart.glyphdial.data.settings.ScrollbarPosition
import com.evodart.glyphdial.data.settings.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val defaultStartPage: StateFlow<String> = settingsDataStore.defaultStartPage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "dial"
        )

    val scrollbarPosition: StateFlow<ScrollbarPosition> = settingsDataStore.scrollbarPosition
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScrollbarPosition.RIGHT
        )

    val showRecommendations: StateFlow<Boolean> = settingsDataStore.showRecommendations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val accentColor: StateFlow<AccentColor> = settingsDataStore.accentColor
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccentColor.RED
        )

    fun setDefaultStartPage(page: String) {
        viewModelScope.launch { settingsDataStore.setDefaultStartPage(page) }
    }

    fun setScrollbarPosition(pos: ScrollbarPosition) {
        viewModelScope.launch { settingsDataStore.setScrollbarPosition(pos) }
    }

    fun setShowRecommendations(show: Boolean) {
        viewModelScope.launch { settingsDataStore.setShowRecommendations(show) }
    }

    fun setAccentColor(color: AccentColor) {
        viewModelScope.launch { settingsDataStore.setAccentColor(color) }
    }
}
