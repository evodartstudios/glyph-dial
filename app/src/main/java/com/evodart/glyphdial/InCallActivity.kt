package com.evodart.glyphdial

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.PowerManager
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
 * Activity for in-call UI - shown over lock screen.
 * Manages proximity sensor WakeLock to turn screen off during ear calls.
 */
@AndroidEntryPoint
class InCallActivity : ComponentActivity() {

    @Inject lateinit var settingsDataStore: SettingsDataStore
    @Inject lateinit var contactRepository: ContactRepository

    // Proximity sensor for screen-off-near-ear
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var proximityWakeLock: PowerManager.WakeLock? = null

    private val proximitySensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val isNearEar = event.values[0] < (proximitySensor?.maximumRange ?: 5f)
            if (isNearEar) {
                if (proximityWakeLock?.isHeld == false) proximityWakeLock?.acquire(10 * 60 * 1000L)
            } else {
                if (proximityWakeLock?.isHeld == true) proximityWakeLock?.release()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show over lock screen
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        // Keep screen on during call
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Dismiss keyguard
        val keyguardManager = getSystemService(android.app.KeyguardManager::class.java)
        keyguardManager?.requestDismissKeyguard(this, null)

        // Proximity sensor setup (turns screen off when held to ear)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        proximityWakeLock = powerManager.newWakeLock(
            PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
            "GlyphDial:ProximityWakeLock"
        )

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

    override fun onResume() {
        super.onResume()
        proximitySensor?.let {
            sensorManager.registerListener(
                proximitySensorListener, it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(proximitySensorListener)
        if (proximityWakeLock?.isHeld == true) proximityWakeLock?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (proximityWakeLock?.isHeld == true) proximityWakeLock?.release()
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun InCallContent(
    contactRepository: ContactRepository,
    onFinish: () -> Unit
) {
    val callState      by GlyphDialCallService.callState.collectAsState()
    val currentCall    by GlyphDialCallService.currentCall.collectAsState()
    val audioState     by GlyphDialCallService.audioState.collectAsState()
    val waitingCall    by GlyphDialCallService.waitingCall.collectAsState()
    val waitingState   by GlyphDialCallService.waitingCallState.collectAsState()
    // Epoch timestamp set by service when call becomes ACTIVE (never reset on hold/unhold)
    val callStartTime  by GlyphDialCallService.callStartTime.collectAsState()
    val scope          = rememberCoroutineScope()

    // ── Epoch-based duration: ticks every second, computed from service start time ──
    // This never resets on HOLDING → ACTIVE transitions because callStartTime stays constant.
    var callDuration by remember { mutableLongStateOf(0L) }
    LaunchedEffect(callState, callStartTime) {
        if ((callState == CallState.ACTIVE || callState == CallState.HOLDING) && callStartTime > 0L) {
            while (true) {
                callDuration = (System.currentTimeMillis() - callStartTime) / 1000L
                delay(500L) // Update twice per second for accuracy
            }
        }
    }

    // Caller info
    var callerContact  by remember { mutableStateOf<Contact?>(null) }
    var rawNumber      by remember { mutableStateOf("") }

    // Waiting caller info
    var waitingNumber  by remember { mutableStateOf("") }

    LaunchedEffect(currentCall) {
        val number = currentCall?.details?.handle?.schemeSpecificPart ?: ""
        rawNumber = number
        if (number.isNotBlank()) {
            scope.launch {
                callerContact = contactRepository.lookupContactByNumber(number)
            }
        }
    }

    LaunchedEffect(waitingCall) {
        waitingNumber = waitingCall?.details?.handle?.schemeSpecificPart ?: ""
    }

    // Finish on disconnect
    LaunchedEffect(callState) {
        if (callState == CallState.DISCONNECTED || callState == CallState.IDLE) {
            onFinish()
        }
    }

    val callerNumber   = formatPhoneNumber(rawNumber.ifBlank { "Unknown" })
    val callerName     = callerContact?.name
        ?: currentCall?.details?.callerDisplayName?.takeIf { it.isNotBlank() }
    val callerPhotoUri = callerContact?.photoUri

    val isMuted      = audioState?.isMuted ?: false
    val isSpeaker    = audioState?.route == android.telecom.CallAudioState.ROUTE_SPEAKER
    val isOnHold     = callState == CallState.HOLDING
    val isConference = GlyphDialCallService.isConference.collectAsState().value

    // Whether the current call supports merging (into conference)
    val canMerge = remember(currentCall) {
        currentCall?.details?.can(android.telecom.Call.Details.CAPABILITY_MERGE_CONFERENCE) == true
    }

    // Audio routes for the bottom sheet
    val availableRoutes = remember(audioState) { GlyphDialCallService.getAvailableRoutes() }

    // UI state
    var showKeypad   by remember { mutableStateOf(false) }
    var showMore     by remember { mutableStateOf(false) }

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
        when (callState) {
            CallState.RINGING -> {
                IncomingCallScreen(
                    callerName     = callerName,
                    callerNumber   = callerNumber,
                    photoUri       = callerPhotoUri,
                    onAnswer       = { GlyphDialCallService.answerCall() },
                    onDecline      = { GlyphDialCallService.rejectCall() },
                    onRejectWithSms = { message -> GlyphDialCallService.rejectWithMessage(message) },
                    modifier       = Modifier.fillMaxSize()
                )
            }

            CallState.DIALING, CallState.CONNECTING, CallState.NEW -> {
                OutgoingCallScreen(
                    callerName      = callerName,
                    callerNumber    = callerNumber,
                    callState       = callState,
                    photoUri        = callerPhotoUri,
                    isMuted         = isMuted,
                    isSpeaker       = isSpeaker,
                    onMuteToggle    = { GlyphDialCallService.mute(!isMuted) },
                    onKeypadClick   = { showKeypad = !showKeypad },
                    onSpeakerToggle = { GlyphDialCallService.setSpeaker(!isSpeaker) },
                    onEndCall       = { GlyphDialCallService.endCall() },
                    modifier        = Modifier.fillMaxSize()
                )
            }

            CallState.ACTIVE, CallState.HOLDING -> {
                ActiveCallScreen(
                    callerName       = callerName,
                    callerNumber     = callerNumber,
                    callDuration     = callDuration,
                    isMuted          = isMuted,
                    isSpeaker        = isSpeaker,
                    isOnHold         = isOnHold,
                    isConference     = isConference,
                    canMerge         = canMerge,
                    availableRoutes  = availableRoutes,
                    onMuteToggle     = { GlyphDialCallService.mute(!isMuted) },
                    onSpeakerToggle  = { GlyphDialCallService.setSpeaker(!isSpeaker) },
                    onBluetoothClick = { GlyphDialCallService.setAudioRouteBluetooth() },
                    onMergeClick     = { GlyphDialCallService.mergeCall() },
                    onHoldToggle     = {
                        if (isOnHold) GlyphDialCallService.unholdCall()
                        else GlyphDialCallService.holdCall()
                    },
                    onKeypadClick    = { showKeypad = !showKeypad },
                    onMoreClick      = { showMore = true },
                    onEndCall        = { GlyphDialCallService.endCall() },
                    modifier         = Modifier.fillMaxSize()
                )
            }

            CallState.DISCONNECTING -> {
                // Greyed-out "Ending…" — show active screen but non-interactive
                ActiveCallScreen(
                    callerName       = callerName,
                    callerNumber     = callerNumber,
                    callDuration     = callDuration,
                    isMuted          = isMuted,
                    isSpeaker        = isSpeaker,
                    isOnHold         = false,
                    isEnding         = true,
                    availableRoutes  = emptyList(),
                    onMuteToggle     = {},
                    onSpeakerToggle  = {},
                    onBluetoothClick = {},
                    onHoldToggle     = {},
                    onKeypadClick    = {},
                    onMoreClick      = {},
                    onEndCall        = {},
                    modifier         = Modifier.fillMaxSize()
                )
            }

            CallState.SELECT_PHONE_ACCOUNT -> {
                OutgoingCallScreen(
                    callerName      = callerName,
                    callerNumber    = callerNumber,
                    callState       = CallState.DIALING,
                    photoUri        = callerPhotoUri,
                    isMuted         = false,
                    isSpeaker       = false,
                    onMuteToggle    = {},
                    onKeypadClick   = {},
                    onSpeakerToggle = {},
                    onEndCall       = { GlyphDialCallService.endCall() },
                    modifier        = Modifier.fillMaxSize()
                )
            }

            else -> {
                OutgoingCallScreen(
                    callerName      = callerName,
                    callerNumber    = callerNumber,
                    callState       = callState,
                    photoUri        = callerPhotoUri,
                    isMuted         = false,
                    isSpeaker       = false,
                    onMuteToggle    = {},
                    onKeypadClick   = { showKeypad = true },
                    onSpeakerToggle = {},
                    onEndCall       = { GlyphDialCallService.endCall() },
                    modifier        = Modifier.fillMaxSize()
                )
            }
        }

        // ── Call Waiting Banner ───────────────────────────────────────────────
        if (waitingCall != null) {
            val waitingName = formatPhoneNumber(waitingNumber.ifBlank { "Unknown" })
            com.evodart.glyphdial.ui.screens.call.CallWaitingBanner(
                callerDisplay      = waitingName,
                onReject           = { GlyphDialCallService.rejectWaiting() },
                onHoldAndAnswer    = { GlyphDialCallService.holdAndAnswerWaiting() },
                onEndAndAnswer     = { GlyphDialCallService.endAndAnswerWaiting() },
                modifier           = androidx.compose.ui.Modifier
                    .align(androidx.compose.ui.Alignment.TopCenter)
            )
        }

        // ── DTMF Keypad Overlay ───────────────────────────────────────────────
        androidx.compose.animation.AnimatedVisibility(
            visible  = showKeypad,
            enter    = androidx.compose.animation.slideInVertically(initialOffsetY = { it }),
            exit     = androidx.compose.animation.slideOutVertically(targetOffsetY = { it }),
            modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
        ) {
            com.evodart.glyphdial.ui.screens.call.InCallDialpad(
                onDigitDown = { digit -> GlyphDialCallService.playDtmfTone(digit) },
                onDigitUp   = { GlyphDialCallService.stopDtmfTone() },
                onClose     = { showKeypad = false }
            )
        }
    }

    // ── More Options Sheet ────────────────────────────────────────────────────
    if (showMore) {
        com.evodart.glyphdial.ui.screens.call.MoreOptionsSheet(
            canMerge     = canMerge,
            isConference = isConference,
            callerName   = callerName ?: callerNumber,
            onMerge      = { GlyphDialCallService.mergeCall(); showMore = false },
            onDismiss    = { showMore = false }
        )
    }
}
