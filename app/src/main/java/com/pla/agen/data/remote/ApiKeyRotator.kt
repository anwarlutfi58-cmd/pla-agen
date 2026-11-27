package com.pla.agen.data.remote

import com.pla.agen.data.local.dao.ApiKeyDao
import com.pla.agen.data.local.entity.ApiKeyEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRotator @Inject constructor(
    private val apiKeyDao: ApiKeyDao
) {
    private val mutex = Mutex()
    private var currentIndex = 0
    private var cachedKeys: List<ApiKeyEntity> = emptyList()

    suspend fun allKeys(): List<ApiKeyEntity> = mutex.withLock {
        cachedKeys = apiKeyDao.getActiveKeys()
        cachedKeys
    }

    suspend fun currentKey(): ApiKeyEntity? = mutex.withLock {
        if (cachedKeys.isEmpty()) cachedKeys = apiKeyDao.getActiveKeys()
        if (cachedKeys.isEmpty()) return null
        cachedKeys[currentIndex % cachedKeys.size]
    }

    suspend fun rotate(): ApiKeyEntity? = mutex.withLock {
        if (cachedKeys.isEmpty()) cachedKeys = apiKeyDao.getActiveKeys()
        if (cachedKeys.isEmpty()) return null
        currentIndex = (currentIndex + 1) % cachedKeys.size
        cachedKeys[currentIndex]
    }

    suspend fun resetRotation() = mutex.withLock {
        currentIndex = 0
        cachedKeys = apiKeyDao.getActiveKeys()
    }
}
