package com.evodart.glyphdial.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialerViewModel @Inject constructor(
    application: Application,
    private val contactRepository: ContactRepository
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _hasPhonePermission = MutableStateFlow(false)
    val hasPhonePermission: StateFlow<Boolean> = _hasPhonePermission.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _t9Suggestions = MutableStateFlow<List<Contact>>(emptyList())
    val t9Suggestions: StateFlow<List<Contact>> = _t9Suggestions.asStateFlow()

    // Cache contacts for fast T9 search
    private var cachedContacts = emptyList<Contact>()

    init {
        checkPermission()
        
        // Setup cache
        viewModelScope.launch {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                contactRepository.getAllContacts().collect { list ->
                    cachedContacts = list
                }
            }
        }
    }

    fun checkPermission() {
        _hasPhonePermission.value = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        if (query.isEmpty()) {
            _t9Suggestions.value = emptyList()
            return
        }

        viewModelScope.launch {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                val filtered = cachedContacts.filter { contact ->
                    contact.phoneNumbers.any { phone ->
                        phone.number.filter { it.isDigit() }.contains(query.filter { it.isDigit() })
                    } || matchesT9(contact.name, query)
                }.take(5)
                _t9Suggestions.value = filtered
            }
        }
    }

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
}
