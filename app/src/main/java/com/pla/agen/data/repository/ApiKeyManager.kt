package com.pla.agen.data.repository

import com.pla.agen.data.local.dao.ApiKeyDao
import com.pla.agen.data.local.entity.ApiKeyEntity
import com.pla.agen.data.remote.ApiKeyRotator
import com.pla.agen.data.security.CryptoManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyManager @Inject constructor(
    private val apiKeyDao: ApiKeyDao,
    private val cryptoManager: CryptoManager,
    private val rotator: ApiKeyRotator
) {
    suspend fun addKey(label: String, rawKey: String) {
        val encrypted = cryptoManager.encrypt(rawKey)
        apiKeyDao.insert(ApiKeyEntity(label = label, keyValue = encrypted))
    }

    suspend fun deleteKey(id: Long) {
        apiKeyDao.delete(id)
    }

    fun decryptKey(stored: String): String? = cryptoManager.decrypt(stored)

    fun mask(rawKey: String): String {
        if (rawKey.length <= 8) return "••••••••"
        return rawKey.take(4) + "••••••••" + rawKey.takeLast(4)
    }

    suspend fun resetRotation() {
        rotator.resetRotation()
    }
}