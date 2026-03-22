package com.evodart.glyphdial.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evodart.glyphdial.data.model.CallLogEntry
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.data.repository.CallLogRepository
import com.evodart.glyphdial.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main ViewModel for dialer app
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val contactRepository: ContactRepository,
    private val callLogRepository: CallLogRepository
) : AndroidViewModel(application) {
    
    private val context = application.applicationContext
    
    // Permission states
    private val _hasContactsPermission = MutableStateFlow(false)
    val hasContactsPermission: StateFlow<Boolean> = _hasContactsPermission.asStateFlow()
    
    private val _hasCallLogPermission = MutableStateFlow(false)
    val hasCallLogPermission: StateFlow<Boolean> = _hasCallLogPermission.asStateFlow()
    
    private val _hasPhonePermission = MutableStateFlow(false)
    val hasPhonePermission: StateFlow<Boolean> = _hasPhonePermission.asStateFlow()
    
    // Contacts
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()
    
    private val _contactsLoading = MutableStateFlow(false)
    val contactsLoading: StateFlow<Boolean> = _contactsLoading.asStateFlow()
    
    // Call log
    private val _recentCalls = MutableStateFlow<List<CallLogEntry>>(emptyList())
    val recentCalls: StateFlow<List<CallLogEntry>> = _recentCalls.asStateFlow()
    
    private val _callLogLoading = MutableStateFlow(false)
    val callLogLoading: StateFlow<Boolean> = _callLogLoading.asStateFlow()
    
    // T9 search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _t9Suggestions = MutableStateFlow<List<Contact>>(emptyList())
    val t9Suggestions: StateFlow<List<Contact>> = _t9Suggestions.asStateFlow()
    
    init {
        checkPermissions()
    }
    
    fun checkPermissions() {
        _hasContactsPermission.value = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        
        _hasCallLogPermission.value = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
        
        _hasPhonePermission.value = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun onPermissionsGranted() {
        checkPermissions()
        if (_hasContactsPermission.value) loadContacts()
        if (_hasCallLogPermission.value) loadRecentCalls()
    }
    
    fun loadContacts() {
        if (!_hasContactsPermission.value) return
        
        viewModelScope.launch {
            _contactsLoading.value = true
            contactRepository.getAllContacts()
                .catch { e -> 
                    // Handle error
                }
                .collect { list ->
                    _contacts.value = list
                    _contactsLoading.value = false
                }
        }
    }
    
    fun loadRecentCalls() {
        if (!_hasCallLogPermission.value) return
        
        viewModelScope.launch {
            _callLogLoading.value = true
            callLogRepository.getRecentCalls()
                .catch { e ->
                    // Handle error
                }
                .collect { list ->
                    _recentCalls.value = list
                    _callLogLoading.value = false
                }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        
        if (query.isEmpty()) {
            _t9Suggestions.value = emptyList()
            return
        }
        
        // T9 search in contacts - works from 1 character
        viewModelScope.launch {
            if (_hasContactsPermission.value && query.isNotEmpty()) {
                // Simple number-based search for now
                val filtered = _contacts.value.filter { contact ->
                    contact.phoneNumbers.any { phone ->
                        phone.number.filter { it.isDigit() }.contains(query.filter { it.isDigit() })
                    } || matchesT9(contact.name, query)
                }.take(5)
                _t9Suggestions.value = filtered
            }
        }
    }
    
    /**
     * Simple T9 matching - checks if name matches T9 pattern
     */
    private fun matchesT9(name: String, digits: String): Boolean {
        val t9Map = mapOf(
            '2' to "abc", '3' to "def", '4' to "ghi", '5' to "jkl",
            '6' to "mno", '7' to "pqrs", '8' to "tuv", '9' to "wxyz"
        )
        
        val nameLower = name.lowercase().filter { it.isLetter() }
        if (nameLower.length < digits.length) return false
        
        for (i in digits.indices) {
            val digit = digits[i]
            val letters = t9Map[digit] ?: return false
            if (i >= nameLower.length || !letters.contains(nameLower[i])) {
                return false
            }
        }
        return true
    }
    
    fun getStarredContacts(): List<Contact> {
        return _contacts.value.filter { it.starred }
    }
}
