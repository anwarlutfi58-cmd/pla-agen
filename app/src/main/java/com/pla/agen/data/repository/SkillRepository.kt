package com.pla.agen.data.repository

import com.pla.agen.data.local.dao.SkillDao
import com.pla.agen.data.local.entity.SkillEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillRepository @Inject constructor(
    private val skillDao: SkillDao
) {
    fun all(): Flow<List<SkillEntity>> = skillDao.all()

    fun enabled(): Flow<List<SkillEntity>> = skillDao.enabled()

    suspend fun addSkill(name: String, description: String) {
        skillDao.insert(
            SkillEntity(name = name, description = description, isBuiltIn = false)
        )
    }

    suspend fun deleteSkill(id: Long) {
        skillDao.delete(id)
    }
}