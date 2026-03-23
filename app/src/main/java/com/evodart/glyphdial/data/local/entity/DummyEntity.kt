package com.evodart.glyphdial.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Placeholder entity to satisfy Room's requirement of >0 entities.
 * Can be replaced in Phase 3/4 when actual entities (SpeedDial, CallNote, etc.) are added.
 */
@Entity(tableName = "dummy_entity")
data class DummyEntity(
    @PrimaryKey val id: Int = 1,
    val value: String = "placeholder"
)
