package com.pla.agen.domain.usecase

import javax.inject.Inject

class GitHubLinkUseCase @Inject constructor() {

    private val githubRegex = Regex(
        """https?://(www\.)?github\.com/[^\s)]+"""
    )

    fun extractLinks(text: String): List<String> =
        githubRegex.findAll(text).map { it.value }.toList()

    fun isGitHubLink(text: String): Boolean = githubRegex.containsMatchIn(text)

    fun directExecutionInstruction(containsError: Boolean): String = """
        Mode: EKSEKUSI LANGSUNG TANPA BASA-BASI.
        ${if (containsError) "Pengguna mengirim kode error / link GitHub." else ""}
        Dilarang memberi intro panjang atau penjelasan bertele-tele.
        Langsung keluarkan hasil perbaikan kode/konfigurasi yang siap pakai,
        lengkap dengan path file yang jelas dalam blok kode terpisah.
        Tulis setiap file dalam format:
        ```<bahasa>:<path/file>
        <isi>
        ```
    """.trimIndent()
}
