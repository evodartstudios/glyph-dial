package com.evodart.glyphdial.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evodart.glyphdial.data.model.CallLogEntry
import com.evodart.glyphdial.data.repository.CallLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallLogViewModel @Inject constructor(
    application: Application,
    private val callLogRepository: CallLogRepository
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    private val _recentCalls = MutableStateFlow<List<CallLogEntry>>(emptyList())
    val recentCalls: StateFlow<List<CallLogEntry>> = _recentCalls.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkPermission()
    }

    fun checkPermission() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
        
        _hasPermission.value = granted
        if (granted) {
            loadRecentCalls()
        }
    }

    private fun loadRecentCalls() {
        viewModelScope.launch {
            _isLoading.value = true
            callLogRepository.getRecentCalls()
                .catch { /* handled in repo */ }
                .collect { list ->
                    _recentCalls.value = list
                    _isLoading.value = false
                }
        }
    }
}
