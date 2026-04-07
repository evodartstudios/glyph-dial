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

    private val t9Trie = com.evodart.glyphdial.utils.T9Trie()

    // Cache contacts for fast T9 search
    private var cachedContacts = emptyList<Contact>()

    init {
        checkPermission()
        
        // Setup cache
        viewModelScope.launch {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                contactRepository.getAllContacts().collect { list ->
                    cachedContacts = list
                    // Build T9 index asynchronously
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                        t9Trie.build(list)
                        // Update suggestions if a query is active
                        if (_searchQuery.value.isNotEmpty()) {
                            _t9Suggestions.value = t9Trie.search(_searchQuery.value).take(5)
                        }
                    }
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
                val results = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                    t9Trie.search(query).take(5)
                }
                _t9Suggestions.value = results
            }
        }
    }
}
