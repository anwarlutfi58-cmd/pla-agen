package com.pla.agen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pla.agen.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY createdAt DESC")
    fun all(): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversation: ConversationEntity): Long

    @Query("UPDATE conversations SET title = :newTitle WHERE id = :id")
    suspend fun rename(id: Long, newTitle: String)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun delete(id: Long)
}