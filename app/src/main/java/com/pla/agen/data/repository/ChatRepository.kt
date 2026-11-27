package com.pla.agen.data.repository

import com.pla.agen.data.local.dao.ApiKeyDao
import com.pla.agen.data.remote.Content
import com.pla.agen.data.remote.GeminiApi
import com.pla.agen.data.remote.GeminiModel
import com.pla.agen.data.remote.GeminiRequest
import com.pla.agen.data.remote.GenerationConfig
import com.pla.agen.data.remote.InlineData
import com.pla.agen.data.remote.Part
import com.pla.agen.data.security.CryptoManager
import javax.inject.Inject
import javax.inject.Singleton

data class ChatTurn(val isUser: Boolean, val text: String)

@Singleton
class ChatRepository @Inject constructor(
    private val api: GeminiApi,
    private val apiKeyDao: ApiKeyDao,
    private val cryptoManager: CryptoManager
) {
    suspend fun send(
        history: List<ChatTurn>,
        systemInstruction: String?,
        model: GeminiModel,
        imageBase64: String? = null,
        imageMime: String? = null
    ): String {
        val keys = apiKeyDao.getActiveKeys()
        if (keys.isEmpty()) {
            return "Belum ada API key. Buka tab Pengaturan dan tambahkan API key Gemini terlebih dahulu."
        }

        val contents = history.map { turn ->
            Content(
                role = if (turn.isUser) "user" else "model",
                parts = listOf(Part(text = turn.text))
            )
        }.toMutableList()

        if (imageBase64 != null && imageMime != null && contents.isNotEmpty()) {
            val last = contents.removeAt(contents.size - 1)
            contents.add(
                Content(
                    role = "user",
                    parts = last.parts + Part(
                        inlineData = InlineData(mimeType = imageMime, data = imageBase64)
                    )
                )
            )
        }

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = systemInstruction
                ?.takeIf { it.isNotBlank() }
                ?.let { Content(role = "user", parts = listOf(Part(text = it))) },
            generationConfig = GenerationConfig()
        )

        var lastError = "Gagal terhubung ke Gemini."
        repeat(keys.size) {
            val key = keys[it]
            val raw = cryptoManager.decrypt(key.keyValue)
            if (raw.isNullOrBlank()) {
                apiKeyDao.setFailCount(key.id, key.failCount + 1)
                return@repeat
            }
            try {
                val response = api.generate(model.apiName, raw, request)
                val text = response.candidates
                    ?.firstOrNull()?.content?.parts
                    ?.mapNotNull { p -> p.text }
                    ?.joinToString("")
                if (!text.isNullOrBlank()) {
                    apiKeyDao.setLastUsed(key.id, System.currentTimeMillis())
                    return text
                }
                lastError = "Gemini membalas kosong (finishReason: " +
                    (response.candidates?.firstOrNull()?.finishReason ?: "tidak diketahui") + ")."
            } catch (e: Exception) {
                lastError = e.message ?: "Error jaringan."
                apiKeyDao.setFailCount(key.id, key.failCount + 1)
            }
        }
        return "Semua API key gagal / kuota habis. Detail: $lastError\nSilakan tambah atau periksa API key di Pengaturan."
    }
}