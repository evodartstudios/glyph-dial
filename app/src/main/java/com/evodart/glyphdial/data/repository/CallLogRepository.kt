package com.evodart.glyphdial.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.CallLog
import com.evodart.glyphdial.data.model.CallLogEntry
import com.evodart.glyphdial.data.model.CallType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing call history
 */
@Singleton
class CallLogRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver
    
    /**
     * Get recent calls
     */
    fun getRecentCalls(limit: Int = 100): Flow<List<CallLogEntry>> = flow {
        val calls = mutableListOf<CallLogEntry>()
        
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_PHOTO_URI,
            CallLog.Calls.IS_READ
        )
        
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )
        
        cursor?.use {
            var count = 0
            while (it.moveToNext() && count < limit) {
                val id = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls._ID))
                val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)) ?: "Unknown"
                val name = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME))
                val type = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                val date = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
                val duration = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DURATION))
                val photoUri = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.CACHED_PHOTO_URI))
                val isRead = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.IS_READ)) == 1
                
                calls.add(
                    CallLogEntry(
                        id = id,
                        number = number,
                        name = name,
                        type = mapCallType(type),
                        timestamp = Instant.ofEpochMilli(date),
                        duration = duration,
                        photoUri = photoUri,
                        isRead = isRead
                    )
                )
                count++
            }
        }
        
        emit(calls)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get missed calls
     */
    fun getMissedCalls(): Flow<List<CallLogEntry>> = flow {
        val calls = mutableListOf<CallLogEntry>()
        
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.CACHED_PHOTO_URI,
                CallLog.Calls.IS_READ
            ),
            "${CallLog.Calls.TYPE} = ?",
            arrayOf(CallLog.Calls.MISSED_TYPE.toString()),
            "${CallLog.Calls.DATE} DESC"
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls._ID))
                val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)) ?: "Unknown"
                val name = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME))
                val date = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
                val duration = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DURATION))
                val photoUri = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.CACHED_PHOTO_URI))
                val isRead = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.IS_READ)) == 1
                
                calls.add(
                    CallLogEntry(
                        id = id,
                        number = number,
                        name = name,
                        type = CallType.MISSED,
                        timestamp = Instant.ofEpochMilli(date),
                        duration = duration,
                        photoUri = photoUri,
                        isRead = isRead
                    )
                )
            }
        }
        
        emit(calls)
    }.flowOn(Dispatchers.IO)
    
    private fun mapCallType(type: Int): CallType {
        return when (type) {
            CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
            CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
            CallLog.Calls.MISSED_TYPE -> CallType.MISSED
            CallLog.Calls.REJECTED_TYPE -> CallType.REJECTED
            CallLog.Calls.BLOCKED_TYPE -> CallType.BLOCKED
            CallLog.Calls.VOICEMAIL_TYPE -> CallType.VOICEMAIL
            else -> CallType.INCOMING
        }
    }
}
