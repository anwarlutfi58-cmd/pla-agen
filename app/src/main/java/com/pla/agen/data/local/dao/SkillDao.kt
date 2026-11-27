package com.pla.agen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pla.agen.data.local.entity.SkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills ORDER BY id ASC")
    fun all(): Flow<List<SkillEntity>>

    @Query("SELECT * FROM skills WHERE isEnabled = 1 ORDER BY id ASC")
    fun enabled(): Flow<List<SkillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(skill: SkillEntity): Long

    @Query("DELETE FROM skills WHERE id = :id")
    suspend fun delete(id: Long)
}