package com.evodart.glyphdial.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.evodart.glyphdial.InCallActivity
import com.evodart.glyphdial.R
import com.evodart.glyphdial.service.CallConstants.ACTION_ANSWER
import com.evodart.glyphdial.service.CallConstants.ACTION_DECLINE
import com.evodart.glyphdial.service.CallConstants.ACTION_HANG_UP
import com.evodart.glyphdial.service.CallConstants.ACTION_MERGE
import com.evodart.glyphdial.service.CallConstants.ACTION_MUTE
import com.evodart.glyphdial.service.CallConstants.ACTION_SPEAKER
import com.evodart.glyphdial.service.CallConstants.CHANNEL_ACTIVE
import com.evodart.glyphdial.service.CallConstants.CHANNEL_INCOMING
import com.evodart.glyphdial.service.CallConstants.CHANNEL_MISSED
import com.evodart.glyphdial.service.CallConstants.NOTIFICATION_ID
import com.evodart.glyphdial.service.CallConstants.NOTIFICATION_MISSED_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * InCallService implementation for handling phone calls.
 * Tracks primary + secondary (waiting) calls, shows rich notifications,
 * and exposes control functions to the UI layer.
 */
class GlyphDialCallService : InCallService() {

    companion object {
        // Primary call state
        private val _currentCall = MutableStateFlow<Call?>(null)
        val currentCall: StateFlow<Call?> = _currentCall.asStateFlow()

        private val _callState = MutableStateFlow(CallState.IDLE)
        val callState: StateFlow<CallState> = _callState.asStateFlow()

        private val _audioState = MutableStateFlow<CallAudioState?>(null)
        val audioState: StateFlow<CallAudioState?> = _audioState.asStateFlow()

        // Second waiting call (call-waiting scenario)
        private val _waitingCall = MutableStateFlow<Call?>(null)
        val waitingCall: StateFlow<Call?> = _waitingCall.asStateFlow()

        private val _waitingCallState = MutableStateFlow(CallState.IDLE)
        val waitingCallState: StateFlow<CallState> = _waitingCallState.asStateFlow()

        // Conference state
        private val _isConference = MutableStateFlow(false)
        val isConference: StateFlow<Boolean> = _isConference.asStateFlow()

        // Call start timestamp (epoch ms), used for live duration in notification
        private val _callStartTime = MutableStateFlow(0L)
        val callStartTime: StateFlow<Long> = _callStartTime.asStateFlow()

        // Caller info from contact lookup
        private val _callerName = MutableStateFlow<String?>(null)
        val callerName: StateFlow<String?> = _callerName.asStateFlow()

        private val _callerPhotoUri = MutableStateFlow<String?>(null)
        val callerPhotoUri: StateFlow<String?> = _callerPhotoUri.asStateFlow()

        private var serviceInstance: GlyphDialCallService? = null

        // ── Primary call controls ──────────────────────────────────────────────
        fun answerCall() { _currentCall.value?.answer(0) }
        fun rejectCall() { _currentCall.value?.reject(false, null) }
        fun rejectWithMessage(message: String) { _currentCall.value?.reject(true, message) }
        fun endCall()    { _currentCall.value?.disconnect() }
        fun holdCall()   { _currentCall.value?.hold() }
        fun unholdCall() { _currentCall.value?.unhold() }

        fun playDtmfTone(digit: Char) { _currentCall.value?.playDtmfTone(digit) }
        fun stopDtmfTone()            { _currentCall.value?.stopDtmfTone() }

        // ── Waiting call controls ─────────────────────────────────────────────
        /** Hold primary, answer waiting */
        fun holdAndAnswerWaiting() {
            _currentCall.value?.hold()
            _waitingCall.value?.answer(0)
        }

        /** End primary, answer waiting */
        fun endAndAnswerWaiting() {
            _currentCall.value?.disconnect()
            _waitingCall.value?.answer(0)
        }

        /** Reject the waiting call without interrupting the primary */
        fun rejectWaiting() { _waitingCall.value?.reject(false, null) }

        /** Merge the held + active calls into a conference */
        fun mergeCall() {
            val primary = _currentCall.value ?: return
            val waiting = _waitingCall.value
            // If HOLDING, merge with waiting call if present, otherwise use conference() directly
            val canMerge = primary.details?.can(Call.Details.CAPABILITY_MERGE_CONFERENCE) == true
            if (canMerge) {
                primary.mergeConference()
                _isConference.value = true
            } else if (waiting != null) {
                primary.conference(waiting)
                _isConference.value = true
            }
        }

        // ── Audio controls ────────────────────────────────────────────────────
        fun mute(mute: Boolean) { serviceInstance?.setMuted(mute) }

        fun setSpeaker(enabled: Boolean) {
            val instance = serviceInstance ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val targetType = if (enabled) android.telecom.CallEndpoint.TYPE_SPEAKER
                                 else         android.telecom.CallEndpoint.TYPE_EARPIECE
                val endpoint = instance.availableEndpoints.firstOrNull { it.endpointType == targetType }
                if (endpoint != null) {
                    instance.requestCallEndpointChange(
                        endpoint,
                        instance.mainExecutor,
                        object : android.os.OutcomeReceiver<Void, android.telecom.CallEndpointException> {
                            override fun onResult(result: Void?) {}
                            override fun onError(error: android.telecom.CallEndpointException) {}
                        }
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                instance.setAudioRoute(
                    if (enabled) CallAudioState.ROUTE_SPEAKER else CallAudioState.ROUTE_EARPIECE
                )
            }
        }

        fun setAudioRouteBluetooth() {
            val instance = serviceInstance ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val endpoint = instance.availableEndpoints.firstOrNull {
                    it.endpointType == android.telecom.CallEndpoint.TYPE_BLUETOOTH
                }
                if (endpoint != null) {
                    instance.requestCallEndpointChange(
                        endpoint,
                        instance.mainExecutor,
                        object : android.os.OutcomeReceiver<Void, android.telecom.CallEndpointException> {
                            override fun onResult(result: Void?) {}
                            override fun onError(error: android.telecom.CallEndpointException) {}
                        }
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                instance.setAudioRoute(CallAudioState.ROUTE_BLUETOOTH)
            }
        }

        /** Get list of available audio route names for the UI sheet */
        fun getAvailableRoutes(): List<AudioRoute> {
            val instance = serviceInstance ?: return emptyList()
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                instance.availableEndpoints.map { ep ->
                    when (ep.endpointType) {
                        android.telecom.CallEndpoint.TYPE_EARPIECE  -> AudioRoute.Earpiece
                        android.telecom.CallEndpoint.TYPE_SPEAKER   -> AudioRoute.Speaker
                        android.telecom.CallEndpoint.TYPE_BLUETOOTH -> AudioRoute.Bluetooth(ep.endpointName.toString())
                        else -> AudioRoute.Earpiece
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val mask = instance.callAudioState?.supportedRouteMask ?: 0
                buildList {
                    if (mask and CallAudioState.ROUTE_EARPIECE  != 0) add(AudioRoute.Earpiece)
                    if (mask and CallAudioState.ROUTE_SPEAKER   != 0) add(AudioRoute.Speaker)
                    if (mask and CallAudioState.ROUTE_BLUETOOTH != 0) add(AudioRoute.Bluetooth("Bluetooth"))
                }
            }
        }

        /** Human-readable name of the current audio route */
        fun currentRouteName(): String {
            val instance = serviceInstance ?: return "Earpiece"
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                instance.currentCallEndpoint?.endpointName?.toString() ?: "Earpiece"
            } else {
                @Suppress("DEPRECATION")
                when (instance.callAudioState?.route) {
                    CallAudioState.ROUTE_SPEAKER   -> "Speaker"
                    CallAudioState.ROUTE_BLUETOOTH -> "Bluetooth"
                    else -> "Earpiece"
                }
            }
        }

        fun updateCallerInfo(name: String?, photoUri: String?) {
            _callerName.value = name
            _callerPhotoUri.value = photoUri
        }
    }

    // ── Per-instance endpoints list (API 34+) ─────────────────────────────────
    private var availableEndpoints: List<android.telecom.CallEndpoint> = emptyList()

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onAvailableCallEndpointsChanged(endpoints: List<android.telecom.CallEndpoint>) {
        super.onAvailableCallEndpointsChanged(endpoints)
        availableEndpoints = endpoints
    }

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // ── Duration ticker coroutine ─────────────────────────────────────────────
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var durationTickerJob: Job? = null

    private fun startDurationTicker(call: Call) {
        durationTickerJob?.cancel()
        durationTickerJob = serviceScope.launch {
            while (true) {
                delay(1000L)
                val currentState = _callState.value
                if (currentState == CallState.ACTIVE || currentState == CallState.HOLDING) {
                    showCallNotification(call)
                } else {
                    break
                }
            }
        }
    }

    private fun stopDurationTicker() {
        durationTickerJob?.cancel()
        durationTickerJob = null
    }

    // ── Primary call callback ─────────────────────────────────────────────────
    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            if (call == _currentCall.value) {
                val newState = mapCallState(state)
                val wasActive = _callState.value == CallState.ACTIVE
                _callState.value = newState
                // Start the 1-second ticker once the call becomes active
                if (newState == CallState.ACTIVE && !wasActive) {
                    _callStartTime.value = System.currentTimeMillis()
                    startDurationTicker(call)
                } else if (newState == CallState.DISCONNECTED || newState == CallState.IDLE) {
                    stopDurationTicker()
                    _isConference.value = false
                }
                showCallNotification(call)
            } else if (call == _waitingCall.value) {
                _waitingCallState.value = mapCallState(state)
            }
        }

        override fun onDetailsChanged(call: Call, details: Call.Details) {
            super.onDetailsChanged(call, details)
            if (call == _currentCall.value) showCallNotification(call)
        }
    }

    // ── Service lifecycle ─────────────────────────────────────────────────────
    override fun onCreate() {
        super.onCreate()
        serviceInstance = this
        createNotificationChannels()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDurationTicker()
        serviceInstance = null
    }

    // ── Call added / removed ──────────────────────────────────────────────────
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        call.registerCallback(callCallback)

        @Suppress("DEPRECATION")
        val state = call.details?.state ?: call.state

        when {
            // No primary call yet — this IS the primary call
            _currentCall.value == null -> {
                _currentCall.value = call
                _callState.value = mapCallState(state)
                _callStartTime.value = System.currentTimeMillis()
                launchInCallActivity()
                showCallNotification(call)
            }
            // Primary call exists and this one is ringing → call waiting
            mapCallState(state) == CallState.RINGING -> {
                _waitingCall.value = call
                _waitingCallState.value = CallState.RINGING
                // InCallActivity is already visible — it observes waitingCall
            }
            else -> {
                // Edge case: replace primary
                _currentCall.value = call
                _callState.value = mapCallState(state)
                _callStartTime.value = System.currentTimeMillis()
                showCallNotification(call)
            }
        }
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        call.unregisterCallback(callCallback)

        when (call) {
            _currentCall.value -> {
                val wasRinging = _callState.value == CallState.RINGING
                stopDurationTicker()
                _isConference.value = false

                // Promote waiting call to primary if present
                val waiting = _waitingCall.value
                if (waiting != null) {
                    _currentCall.value = waiting
                    @Suppress("DEPRECATION")
                    _callState.value = mapCallState(waiting.details?.state ?: waiting.state)
                    _waitingCall.value = null
                    _waitingCallState.value = CallState.IDLE
                    showCallNotification(waiting)
                } else {
                    // Post missed call notification when call was never answered
                    if (wasRinging) postMissedCallNotification(call)
                    _currentCall.value = null
                    _callState.value = CallState.IDLE
                    _callerName.value = null
                    _callerPhotoUri.value = null
                    _callStartTime.value = 0L
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }
            }
            _waitingCall.value -> {
                _waitingCall.value = null
                _waitingCallState.value = CallState.IDLE
            }
        }
    }

    @Deprecated("Deprecated in API 34. Kept for backward compatibility.")
    override fun onCallAudioStateChanged(audioState: CallAudioState) {
        @Suppress("DEPRECATION")
        super.onCallAudioStateChanged(audioState)
        _audioState.value = audioState
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private fun launchInCallActivity() {
        val intent = Intent(this, InCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        }
        startActivity(intent)
    }

    // ── Notification channels ─────────────────────────────────────────────────
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val incomingChannel = NotificationChannel(
                CHANNEL_INCOMING, "Incoming Calls", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming calls"
                setShowBadge(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .build()
                )
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(incomingChannel)

            val activeChannel = NotificationChannel(
                CHANNEL_ACTIVE, "Active Calls", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ongoing call notifications"
                setShowBadge(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(activeChannel)

            val missedChannel = NotificationChannel(
                CHANNEL_MISSED, "Missed Calls", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Missed call notifications"
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(missedChannel)
        }
    }

    // ── Active / Incoming call notification ───────────────────────────────────
    private fun showCallNotification(call: Call) {
        val callerNumber = call.details?.handle?.schemeSpecificPart ?: "Unknown"
        val displayName = _callerName.value
            ?: call.details?.callerDisplayName?.takeIf { it.isNotBlank() }
            ?: callerNumber

        @Suppress("DEPRECATION")
        val currentState = call.details?.state ?: call.state
        val isRinging  = currentState == Call.STATE_RINGING
        val isDialing  = currentState == Call.STATE_DIALING || currentState == Call.STATE_CONNECTING
        val isActive   = currentState == Call.STATE_ACTIVE
        val channelId  = if (isRinging) CHANNEL_INCOMING else CHANNEL_ACTIVE

        // Live duration string for active calls
        val elapsedSecs = if (isActive && _callStartTime.value > 0L) {
            (System.currentTimeMillis() - _callStartTime.value) / 1000L
        } else 0L

        val statusText = when {
            isRinging -> "Incoming call"
            isDialing -> "Calling…"
            isActive  -> formatNotificationDuration(elapsedSecs)
            else      -> "Call in progress"
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, InCallActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isMuted   = _audioState.value?.isMuted ?: false
        val isSpeaker = _audioState.value?.route == CallAudioState.ROUTE_SPEAKER
        val contactBitmap = loadContactPhoto(_callerPhotoUri.value)

        val person = Person.Builder()
            .setName(displayName)
            .apply { if (contactBitmap != null) setIcon(IconCompat.createWithBitmap(contactBitmap)) }
            .build()

        // Can this call be merged into a conference?
        val canMerge = call.details?.can(Call.Details.CAPABILITY_MERGE_CONFERENCE) == true

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            buildCallStyleNotification(
                channelId, person, displayName, callerNumber, statusText,
                isRinging, isMuted, isSpeaker, canMerge, contactBitmap, fullScreenPendingIntent
            )
        } else {
            buildLegacyNotification(
                channelId, displayName, callerNumber, statusText,
                isRinging, isMuted, isSpeaker, canMerge, contactBitmap, fullScreenPendingIntent
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    /** Format seconds as mm:ss for notification display */
    private fun formatNotificationDuration(seconds: Long): String {
        val m = TimeUnit.SECONDS.toMinutes(seconds)
        val s = seconds - TimeUnit.MINUTES.toSeconds(m)
        return "%02d:%02d".format(m, s)
    }

    private fun buildCallStyleNotification(
        channelId: String,
        person: Person,
        displayName: String,
        callerNumber: String,
        statusText: String,
        isRinging: Boolean,
        isMuted: Boolean,
        isSpeaker: Boolean,
        canMerge: Boolean,
        contactBitmap: Bitmap?,
        fullScreenPendingIntent: PendingIntent
    ): Notification {
        val style = if (isRinging) {
            NotificationCompat.CallStyle.forIncomingCall(
                person,
                createActionPendingIntent(ACTION_DECLINE),
                createActionPendingIntent(ACTION_ANSWER)
            )
        } else {
            NotificationCompat.CallStyle.forOngoingCall(
                person,
                createActionPendingIntent(ACTION_HANG_UP)
            )
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(displayName)
            .setContentText(statusText)
            .setSubText(if (displayName != callerNumber) callerNumber else null)
            .setStyle(style)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)

        if (contactBitmap != null) builder.setLargeIcon(contactBitmap)

        // Quick-action buttons for ongoing calls (mute + speaker + merge)
        if (!isRinging) {
            builder.addAction(
                NotificationCompat.Action.Builder(
                    if (isMuted) android.R.drawable.ic_lock_silent_mode
                    else         android.R.drawable.ic_lock_silent_mode_off,
                    if (isMuted) "Unmute" else "Mute",
                    createActionPendingIntent(ACTION_MUTE)
                ).build()
            )
            builder.addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_lock_power_off,
                    if (isSpeaker) "Earpiece" else "Speaker",
                    createActionPendingIntent(ACTION_SPEAKER)
                ).build()
            )
            if (canMerge) {
                builder.addAction(
                    NotificationCompat.Action.Builder(
                        android.R.drawable.ic_menu_share,
                        "Merge",
                        createActionPendingIntent(ACTION_MERGE)
                    ).build()
                )
            }
        }

        return builder.build()
    }

    private fun buildLegacyNotification(
        channelId: String,
        displayName: String,
        callerNumber: String,
        statusText: String,
        isRinging: Boolean,
        isMuted: Boolean,
        isSpeaker: Boolean,
        canMerge: Boolean,
        contactBitmap: Bitmap?,
        fullScreenPendingIntent: PendingIntent
    ): Notification {
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(displayName)
            .setContentText(statusText)
            .setSubText(if (displayName != callerNumber) callerNumber else null)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setPriority(if (isRinging) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)

        if (contactBitmap != null) builder.setLargeIcon(contactBitmap)

        if (isRinging) {
            builder.addAction(android.R.drawable.ic_menu_call, "Answer", createActionPendingIntent(ACTION_ANSWER))
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Decline", createActionPendingIntent(ACTION_DECLINE))
        } else {
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Hang Up", createActionPendingIntent(ACTION_HANG_UP))
            builder.addAction(
                android.R.drawable.ic_lock_silent_mode,
                if (isMuted) "Unmute" else "Mute",
                createActionPendingIntent(ACTION_MUTE)
            )
            builder.addAction(
                android.R.drawable.ic_lock_power_off,
                if (isSpeaker) "Earpiece" else "Speaker",
                createActionPendingIntent(ACTION_SPEAKER)
            )
            if (canMerge) {
                builder.addAction(
                    android.R.drawable.ic_menu_share,
                    "Merge",
                    createActionPendingIntent(ACTION_MERGE)
                )
            }
        }

        return builder.build()
    }

    // ── Missed call notification ───────────────────────────────────────────────
    private fun postMissedCallNotification(call: Call) {
        val number = call.details?.handle?.schemeSpecificPart ?: "Unknown"
        val name = _callerName.value ?: number

        val tapIntent = PendingIntent.getActivity(
            this, NOTIFICATION_MISSED_ID,
            Intent(this, com.evodart.glyphdial.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_MISSED)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Missed call")
            .setContentText(name)
            .setSubText(if (name != number) number else null)
            .setCategory(NotificationCompat.CATEGORY_MISSED_CALL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(tapIntent)
            .build()

        notificationManager.notify(NOTIFICATION_MISSED_ID, notification)
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    private fun loadContactPhoto(photoUri: String?): Bitmap? {
        if (photoUri == null) return null
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(Uri.parse(photoUri))
            inputStream?.use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) { null }
    }

    private fun createActionPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, CallActionReceiver::class.java).apply { this.action = action }
        return PendingIntent.getBroadcast(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun mapCallState(state: Int): CallState = when (state) {
        Call.STATE_NEW             -> CallState.NEW
        Call.STATE_DIALING         -> CallState.DIALING
        Call.STATE_RINGING         -> CallState.RINGING
        Call.STATE_HOLDING         -> CallState.HOLDING
        Call.STATE_ACTIVE          -> CallState.ACTIVE
        Call.STATE_DISCONNECTED    -> CallState.DISCONNECTED
        Call.STATE_CONNECTING      -> CallState.CONNECTING
        Call.STATE_DISCONNECTING   -> CallState.DISCONNECTING
        Call.STATE_SELECT_PHONE_ACCOUNT -> CallState.SELECT_PHONE_ACCOUNT
        else                       -> CallState.IDLE
    }
}

/** Sealed type for UI audio route picker */
sealed class AudioRoute {
    object Earpiece : AudioRoute()
    object Speaker  : AudioRoute()
    data class Bluetooth(val name: String) : AudioRoute()
}

enum class CallState {
    IDLE, NEW, DIALING, RINGING, CONNECTING,
    ACTIVE, HOLDING, DISCONNECTING, DISCONNECTED,
    SELECT_PHONE_ACCOUNT
}
