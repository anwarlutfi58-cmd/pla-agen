package com.pla.agen.domain.usecase

import com.pla.agen.domain.model.CodeFile
import javax.inject.Inject

class ParseCodeBlocksUseCase @Inject constructor() {

    private val blockRegex = Regex(
        """```([a-zA-Z0-9_+-]+)(?::([^\r\n]+))?\r?\n([\s\S]*?)```"""
    )

    fun parse(rawText: String): List<CodeFile> {
        var index = 0
        return blockRegex.findAll(rawText).mapNotNull { match ->
            val lang = match.groupValues[1].trim()
            val rawPath = match.groupValues[2].trim()
            val body = match.groupValues[3].trimEnd()
            if (body.isBlank()) return@mapNotNull null

            val resolvedPath = rawPath.ifBlank {
                "generated/file_${index + 1}.${extensionFor(lang)}"
            }
            val item = CodeFile(
                path = resolvedPath,
                language = lang,
                content = body,
                order = index
            )
            index++
            item
        }.toList()
    }

    private fun extensionFor(lang: String): String = when (lang.lowercase()) {
        "kotlin", "kt" -> "kt"
        "java" -> "java"
        "dart" -> "dart"
        "cpp", "c++" -> "cpp"
        "c" -> "c"
        "python", "py" -> "py"
        "xml" -> "xml"
        "gradle", "kts" -> "gradle.kts"
        "toml" -> "toml"
        "json" -> "json"
        "yaml", "yml" -> "yml"
        "sh", "bash" -> "sh"
        else -> "txt"
    }
}
