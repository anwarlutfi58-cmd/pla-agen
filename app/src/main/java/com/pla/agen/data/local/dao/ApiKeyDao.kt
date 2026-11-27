package com.pla.agen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pla.agen.data.local.entity.ApiKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiKeyDao {
    @Query("SELECT * FROM api_keys ORDER BY id ASC")
    fun allFlow(): Flow<List<ApiKeyEntity>>

    @Query("SELECT * FROM api_keys WHERE isActive = 1 ORDER BY id ASC")
    suspend fun getActiveKeys(): List<ApiKeyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: ApiKeyEntity): Long

    @Query("DELETE FROM api_keys WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE api_keys SET failCount = :failCount WHERE id = :id")
    suspend fun setFailCount(id: Long, failCount: Int)

    @Query("UPDATE api_keys SET lastUsedAt = :timestamp WHERE id = :id")
    suspend fun setLastUsed(id: Long, timestamp: Long)
}