package com.pla.agen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_keys")
data class ApiKeyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val keyValue: String,
    val isActive: Boolean = true,
    val lastUsedAt: Long? = null,
    val failCount: Int = 0
)
