package com.evodart.glyphdial.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver for handling notification actions (Answer, Decline, Hang Up)
 */
class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            CallConstants.ACTION_ANSWER -> GlyphDialCallService.answerCall()
            CallConstants.ACTION_DECLINE -> GlyphDialCallService.rejectCall()
            CallConstants.ACTION_HANG_UP -> GlyphDialCallService.endCall()
        }
    }
}
