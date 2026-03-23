package com.evodart.glyphdial.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.evodart.glyphdial.InCallActivity
import com.evodart.glyphdial.R
import com.evodart.glyphdial.service.CallConstants.ACTION_ANSWER
import com.evodart.glyphdial.service.CallConstants.ACTION_DECLINE
import com.evodart.glyphdial.service.CallConstants.ACTION_HANG_UP
import com.evodart.glyphdial.service.CallConstants.CHANNEL_ACTIVE
import com.evodart.glyphdial.service.CallConstants.CHANNEL_INCOMING
import com.evodart.glyphdial.service.CallConstants.NOTIFICATION_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream

/**
 * InCallService implementation for handling phone calls
 * Launches InCallActivity and shows media-style notifications
 */
class GlyphDialCallService : InCallService() {
    
    companion object {
        private val _currentCall = MutableStateFlow<Call?>(null)
        val currentCall: StateFlow<Call?> = _currentCall.asStateFlow()
        
        private val _callState = MutableStateFlow(CallState.IDLE)
        val callState: StateFlow<CallState> = _callState.asStateFlow()
        
        private val _audioState = MutableStateFlow<CallAudioState?>(null)
        val audioState: StateFlow<CallAudioState?> = _audioState.asStateFlow()
        
        // Caller info updated from contact lookup
        private val _callerName = MutableStateFlow<String?>(null)
        val callerName: StateFlow<String?> = _callerName.asStateFlow()
        
        private val _callerPhotoUri = MutableStateFlow<String?>(null)
        val callerPhotoUri: StateFlow<String?> = _callerPhotoUri.asStateFlow()
        
        private var serviceInstance: GlyphDialCallService? = null
        
        // Call control functions
        fun answerCall() {
            _currentCall.value?.answer(0)
        }
        
        fun rejectCall() {
            _currentCall.value?.reject(false, null)
        }
        
        fun endCall() {
            _currentCall.value?.disconnect()
        }
        
        fun holdCall() {
            _currentCall.value?.hold()
        }
        
        fun unholdCall() {
            _currentCall.value?.unhold()
        }
        
        fun mute(mute: Boolean) {
            serviceInstance?.setMuted(mute)
        }
        
        fun setSpeaker(enabled: Boolean) {
            val instance = serviceInstance ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Modern API: requestCallEndpointChange (API 34+)
                val targetType = if (enabled) {
                    android.telecom.CallEndpoint.TYPE_SPEAKER
                } else {
                    android.telecom.CallEndpoint.TYPE_EARPIECE
                }
                val currentEndpoints = instance.availableEndpoints
                val targetEndpoint = currentEndpoints.firstOrNull { it.endpointType == targetType }
                if (targetEndpoint != null) {
                    instance.requestCallEndpointChange(
                        targetEndpoint,
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
                    if (enabled) CallAudioState.ROUTE_SPEAKER
                    else CallAudioState.ROUTE_EARPIECE
                )
            }
        }
        
        fun updateCallerInfo(name: String?, photoUri: String?) {
            _callerName.value = name
            _callerPhotoUri.value = photoUri
        }
    }
    
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            _callState.value = mapCallState(state)
            showCallNotification(call)
        }
        
        override fun onDetailsChanged(call: Call, details: Call.Details) {
            super.onDetailsChanged(call, details)
            showCallNotification(call)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        serviceInstance = this
        createNotificationChannel()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceInstance = null
    }

    private var availableEndpoints: List<android.telecom.CallEndpoint> = emptyList()

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onAvailableCallEndpointsChanged(endpoints: List<android.telecom.CallEndpoint>) {
        super.onAvailableCallEndpointsChanged(endpoints)
        availableEndpoints = endpoints
    }
    
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        _currentCall.value = call
        @Suppress("DEPRECATION")
        val callState = call.details?.state ?: call.state
        _callState.value = mapCallState(callState)
        call.registerCallback(callCallback)
        
        // Launch InCallActivity
        launchInCallActivity()
        
        // Show notification
        showCallNotification(call)
    }
    
    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        call.unregisterCallback(callCallback)
        if (_currentCall.value == call) {
            _currentCall.value = null
            _callState.value = CallState.IDLE
            _callerName.value = null
            _callerPhotoUri.value = null
        }
        // Stop foreground service and remove notification
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    
    @Deprecated("Deprecated in API 34. Kept for backward compatibility on older devices.")
    override fun onCallAudioStateChanged(audioState: CallAudioState) {
        @Suppress("DEPRECATION")
        super.onCallAudioStateChanged(audioState)
        _audioState.value = audioState
    }
    
    private fun launchInCallActivity() {
        val intent = Intent(this, InCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        }
        startActivity(intent)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Incoming call channel - high priority with sound
            val incomingChannel = NotificationChannel(
                CHANNEL_INCOMING,
                "Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
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
            
            // Active call channel - lower priority, no sound
            val activeChannel = NotificationChannel(
                CHANNEL_ACTIVE,
                "Active Calls",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ongoing call notifications"
                setShowBadge(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(activeChannel)
        }
    }
    
    private fun showCallNotification(call: Call) {
        val callerNumber = call.details?.handle?.schemeSpecificPart ?: "Unknown"
        val displayName = _callerName.value 
            ?: call.details?.callerDisplayName?.takeIf { it.isNotBlank() } 
            ?: formatPhoneNumber(callerNumber)
        
        val currentState = call.details?.state ?: @Suppress("DEPRECATION") call.state
        val isRinging = currentState == Call.STATE_RINGING
        val isDialing = currentState == Call.STATE_DIALING || currentState == Call.STATE_CONNECTING
        val channelId = if (isRinging) CHANNEL_INCOMING else CHANNEL_ACTIVE
        
        val fullScreenIntent = Intent(this, InCallActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Status text
        val statusText = when {
            isRinging -> "Incoming call"
            isDialing -> "Calling..."
            else -> "Call in progress"
        }
        
        // Load contact photo bitmap
        val contactBitmap = loadContactPhoto(_callerPhotoUri.value)
        
        // Person for CallStyle notification
        val person = Person.Builder()
            .setName(displayName)
            .apply {
                if (contactBitmap != null) {
                    setIcon(IconCompat.createWithBitmap(contactBitmap))
                }
            }
            .build()
        
        // Build notification using CallStyle (Android 12+) or fallback
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            buildCallStyleNotification(
                channelId = channelId,
                person = person,
                displayName = displayName,
                callerNumber = callerNumber,
                statusText = statusText,
                isRinging = isRinging,
                contactBitmap = contactBitmap,
                fullScreenPendingIntent = fullScreenPendingIntent
            )
        } else {
            buildLegacyNotification(
                channelId = channelId,
                displayName = displayName,
                callerNumber = callerNumber,
                statusText = statusText,
                isRinging = isRinging,
                contactBitmap = contactBitmap,
                fullScreenPendingIntent = fullScreenPendingIntent
            )
        }
        
        // Use startForeground for CallStyle notification (required by Android)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID, 
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, 
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }
    
    private fun buildCallStyleNotification(
        channelId: String,
        person: Person,
        displayName: String,
        callerNumber: String,
        statusText: String,
        isRinging: Boolean,
        contactBitmap: Bitmap?,
        fullScreenPendingIntent: PendingIntent
    ): Notification {
        val answerIntent = createActionPendingIntent(ACTION_ANSWER)
        val declineIntent = createActionPendingIntent(ACTION_DECLINE)
        val hangUpIntent = createActionPendingIntent(ACTION_HANG_UP)
        
        val style = if (isRinging) {
            NotificationCompat.CallStyle.forIncomingCall(person, declineIntent, answerIntent)
        } else {
            NotificationCompat.CallStyle.forOngoingCall(person, hangUpIntent)
        }
        
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(displayName)
            .setContentText(statusText)
            .setSubText(if (displayName != callerNumber) callerNumber else null)
            .setStyle(style)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .apply {
                if (contactBitmap != null) {
                    setLargeIcon(contactBitmap)
                }
            }
            .build()
    }
    
    private fun buildLegacyNotification(
        channelId: String,
        displayName: String,
        callerNumber: String,
        statusText: String,
        isRinging: Boolean,
        contactBitmap: Bitmap?,
        fullScreenPendingIntent: PendingIntent
    ): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(displayName)
            .setContentText(statusText)
            .setSubText(if (displayName != callerNumber) callerNumber else null)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setPriority(if (isRinging) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .apply {
                if (contactBitmap != null) {
                    setLargeIcon(contactBitmap)
                }
                if (isRinging) {
                    addAction(android.R.drawable.ic_menu_call, "Answer", 
                        createActionPendingIntent(ACTION_ANSWER))
                    addAction(android.R.drawable.ic_menu_close_clear_cancel, "Decline", 
                        createActionPendingIntent(ACTION_DECLINE))
                } else {
                    addAction(android.R.drawable.ic_menu_close_clear_cancel, "Hang Up", 
                        createActionPendingIntent(ACTION_HANG_UP))
                }
            }
            .build()
    }
    
    private fun loadContactPhoto(photoUri: String?): Bitmap? {
        if (photoUri == null) return null
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(Uri.parse(photoUri))
            inputStream?.use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun formatPhoneNumber(number: String): String {
        // Keep number as-is, no US-specific formatting
        return number
    }
    
    private fun createActionPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, CallActionReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
    
    private fun mapCallState(state: Int): CallState {
        return when (state) {
            Call.STATE_NEW -> CallState.NEW
            Call.STATE_DIALING -> CallState.DIALING
            Call.STATE_RINGING -> CallState.RINGING
            Call.STATE_HOLDING -> CallState.HOLDING
            Call.STATE_ACTIVE -> CallState.ACTIVE
            Call.STATE_DISCONNECTED -> CallState.DISCONNECTED
            Call.STATE_CONNECTING -> CallState.CONNECTING
            Call.STATE_DISCONNECTING -> CallState.DISCONNECTING
            Call.STATE_SELECT_PHONE_ACCOUNT -> CallState.SELECT_PHONE_ACCOUNT
            else -> CallState.IDLE
        }
    }
}

/**
 * Call state enum for easier handling in UI
 */
enum class CallState {
    IDLE,
    NEW,
    DIALING,
    RINGING,
    CONNECTING,
    ACTIVE,
    HOLDING,
    DISCONNECTING,
    DISCONNECTED,
    SELECT_PHONE_ACCOUNT
}
