package com.evodart.glyphdial.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for GlyphDial local storage.
 * Entities will be added as features are implemented:
 * - Phase 3: SpeedDial, BlockedNumber
 * - Phase 4: CallNote, CallRecording, ScheduledCall
 */
@Database(
    entities = [],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()
