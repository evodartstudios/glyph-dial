package com.evodart.glyphdial

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.data.repository.ContactRepository
import com.evodart.glyphdial.service.CallState
import com.evodart.glyphdial.service.GlyphDialCallService
import com.evodart.glyphdial.ui.components.dialpad.formatPhoneNumber
import com.evodart.glyphdial.ui.screens.call.ActiveCallScreen
import com.evodart.glyphdial.ui.screens.call.IncomingCallScreen
import com.evodart.glyphdial.ui.screens.call.OutgoingCallScreen
import com.evodart.glyphdial.ui.theme.GlyphDialTheme
import com.evodart.glyphdial.ui.theme.LocalAccentColor
import com.evodart.glyphdial.data.settings.AccentColor
import com.evodart.glyphdial.data.settings.SettingsDataStore
import com.evodart.glyphdial.ui.theme.toColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity for in-call UI - shown when app is default dialer
 * Shows over lock screen and keeps screen on
 */
@AndroidEntryPoint
class InCallActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsDataStore: SettingsDataStore
    
    @Inject
    lateinit var contactRepository: ContactRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Show over lock screen
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        
        // Keep screen on during call
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Dismiss keyguard (modern replacement for FLAG_DISMISS_KEYGUARD)
        val keyguardManager = getSystemService(android.app.KeyguardManager::class.java)
        keyguardManager?.requestDismissKeyguard(this, null)
        
        enableEdgeToEdge()
        
        setContent {
            val accentColor by settingsDataStore.accentColor.collectAsState(initial = AccentColor.RED)
            
            CompositionLocalProvider(LocalAccentColor provides accentColor.toColor()) {
                GlyphDialTheme {
                    InCallContent(
                        contactRepository = contactRepository,
                        onFinish = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun InCallContent(
    contactRepository: ContactRepository,
    onFinish: () -> Unit
) {
    val callState by GlyphDialCallService.callState.collectAsState()
    val currentCall by GlyphDialCallService.currentCall.collectAsState()
    val audioState by GlyphDialCallService.audioState.collectAsState()
    val scope = rememberCoroutineScope()
    
    // Track call duration
    var callDuration by remember { mutableLongStateOf(0L) }
    
    // Caller info from contacts
    var callerContact by remember { mutableStateOf<Contact?>(null) }
    var rawNumber by remember { mutableStateOf("") }
    
    // Lookup contact when call arrives
    LaunchedEffect(currentCall) {
        val number = currentCall?.details?.handle?.schemeSpecificPart ?: ""
        rawNumber = number
        if (number.isNotBlank()) {
            scope.launch {
                callerContact = contactRepository.lookupContactByNumber(number)
            }
        }
    }
    
    // Timer for active calls only
    LaunchedEffect(callState) {
        if (callState == CallState.ACTIVE) {
            callDuration = 0
            while (true) {
                delay(1000)
                callDuration++
            }
        }
    }
    
    // Finish when call ends instantly
    LaunchedEffect(callState) {
        if (callState == CallState.DISCONNECTED || callState == CallState.IDLE) {
            onFinish()
        }
    }
    
    // Derived caller info
    val callerNumber = formatPhoneNumber(rawNumber.ifBlank { "Unknown" })
    val callerName = callerContact?.name 
        ?: currentCall?.details?.callerDisplayName?.takeIf { it.isNotBlank() }
    val callerPhotoUri = callerContact?.photoUri
    
    // Mute/speaker state
    val isMuted = audioState?.isMuted ?: false
    val isSpeaker = audioState?.route == android.telecom.CallAudioState.ROUTE_SPEAKER
    val isOnHold = callState == CallState.HOLDING
    
    when (callState) {
        // Incoming call - show answer/decline
        CallState.RINGING -> {
            IncomingCallScreen(
                callerName = callerName,
                callerNumber = callerNumber,
                onAnswer = { GlyphDialCallService.answerCall() },
                onDecline = { GlyphDialCallService.rejectCall() },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Outgoing call - show calling screen with animations
        CallState.DIALING, CallState.CONNECTING, CallState.NEW -> {
            OutgoingCallScreen(
                callerName = callerName,
                callerNumber = callerNumber,
                callState = callState,
                photoUri = callerPhotoUri,
                isMuted = isMuted,
                isSpeaker = isSpeaker,
                onMuteToggle = { GlyphDialCallService.mute(!isMuted) },
                onKeypadClick = { /* TODO */ },
                onSpeakerToggle = { GlyphDialCallService.setSpeaker(!isSpeaker) },
                onEndCall = { GlyphDialCallService.endCall() },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Active call - show connected screen with timer
        CallState.ACTIVE, CallState.HOLDING -> {
            ActiveCallScreen(
                callerName = callerName,
                callerNumber = callerNumber,
                callDuration = callDuration,
                isMuted = isMuted,
                isSpeaker = isSpeaker,
                isOnHold = isOnHold,
                onMuteToggle = { GlyphDialCallService.mute(!isMuted) },
                onSpeakerToggle = { GlyphDialCallService.setSpeaker(!isSpeaker) },
                onHoldToggle = {
                    if (isOnHold) GlyphDialCallService.unholdCall()
                    else GlyphDialCallService.holdCall()
                },
                onKeypadClick = { /* TODO */ },
                onEndCall = { GlyphDialCallService.endCall() },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Default/transitional
        else -> {
            OutgoingCallScreen(
                callerName = callerName,
                callerNumber = callerNumber,
                callState = callState,
                photoUri = callerPhotoUri,
                isMuted = false,
                isSpeaker = false,
                onMuteToggle = { },
                onKeypadClick = { },
                onSpeakerToggle = { },
                onEndCall = { GlyphDialCallService.endCall() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
