package com.evodart.glyphdial.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver for handling notification actions (Answer, Decline, Hang Up, Mute, Speaker, Merge)
 */
class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            CallConstants.ACTION_ANSWER  -> GlyphDialCallService.answerCall()
            CallConstants.ACTION_DECLINE -> GlyphDialCallService.rejectCall()
            CallConstants.ACTION_HANG_UP -> GlyphDialCallService.endCall()
            CallConstants.ACTION_MUTE -> {
                val currentlyMuted = GlyphDialCallService.audioState.value?.isMuted ?: false
                GlyphDialCallService.mute(!currentlyMuted)
            }
            CallConstants.ACTION_SPEAKER -> {
                val isSpeaker = GlyphDialCallService.audioState.value?.route ==
                        android.telecom.CallAudioState.ROUTE_SPEAKER
                GlyphDialCallService.setSpeaker(!isSpeaker)
            }
            CallConstants.ACTION_MERGE -> GlyphDialCallService.mergeCall()
        }
    }
}
