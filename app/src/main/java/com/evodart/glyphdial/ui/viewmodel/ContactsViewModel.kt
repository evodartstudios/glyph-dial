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
class ContactsViewModel @Inject constructor(
    application: Application,
    private val contactRepository: ContactRepository
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkPermission()
    }

    fun checkPermission() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        
        _hasPermission.value = granted
        if (granted) {
            loadContacts()
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            contactRepository.getAllContacts()
                .catch { /* handled in repo */ }
                .collect { list ->
                    _contacts.value = list
                    _isLoading.value = false
                }
        }
    }

    fun getStarredContacts(): List<Contact> {
        return _contacts.value.filter { it.starred }
    }
}
