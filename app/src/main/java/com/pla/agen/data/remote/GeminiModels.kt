package com.pla.agen.data.remote

import com.google.gson.annotations.SerializedName

enum class GeminiModel(val apiName: String, val label: String) {
    GEMINI_3_FLASH("gemini-3-flash", "Gemini 3 Flash"),
    GEMINI_3_PRO("gemini-3-pro", "Gemini 3 Pro"),
    GEMINI_4_FLASH("gemini-4-flash", "Gemini 4 Flash (Terbaru)"),
    GEMINI_4_PRO("gemini-4-pro", "Gemini 4 Pro (Terbaru)");

    companion object {
        fun fromApiName(name: String): GeminiModel =
            entries.firstOrNull { it.apiName == name } ?: GEMINI_4_FLASH
    }
}

data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val generationConfig: GenerationConfig? = null
)

data class Content(
    val role: String,
    val parts: List<Part>
)

data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

data class InlineData(
    @SerializedName("mime_type") val mimeType: String,
    val data: String
)

data class GenerationConfig(
    val temperature: Float = 0.4f,
    val maxOutputTokens: Int = 8192
)

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?,
    @SerializedName("finishReason") val finishReason: String?
)
