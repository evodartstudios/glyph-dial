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
            ACTION_ANSWER -> GlyphDialCallService.answerCall()
            ACTION_DECLINE -> GlyphDialCallService.rejectCall()
            ACTION_HANG_UP -> GlyphDialCallService.endCall()
        }
    }
}

private const val ACTION_ANSWER = "com.evodart.glyphdial.ACTION_ANSWER"
private const val ACTION_DECLINE = "com.evodart.glyphdial.ACTION_DECLINE"
private const val ACTION_HANG_UP = "com.evodart.glyphdial.ACTION_HANG_UP"
