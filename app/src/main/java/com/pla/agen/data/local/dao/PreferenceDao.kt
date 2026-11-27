package com.pla.agen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pla.agen.data.local.entity.PreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferenceDao {
    @Query("SELECT * FROM preferences")
    fun all(): Flow<List<PreferenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(preference: PreferenceEntity)

    @Query("SELECT value FROM preferences WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): String?
}