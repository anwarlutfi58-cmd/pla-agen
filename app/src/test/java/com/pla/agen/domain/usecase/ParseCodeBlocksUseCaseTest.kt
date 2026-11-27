package com.pla.agen.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ParseCodeBlocksUseCaseTest {

    private lateinit var useCase: ParseCodeBlocksUseCase

    @Before
    fun setup() {
        useCase = ParseCodeBlocksUseCase()
    }

    @Test
    fun `parse extracts multiple files with explicit path and language`() {
        val raw = """
            ```kotlin:app/src/main/java/com/pla/agen/MainActivity.kt
            package com.pla.agen
            class MainActivity
            ```
            Teks penjelasan di tengah.
            ```xml:app/src/main/res/values/strings.xml
            <resources><string name="app_name">PLA Agen</string></resources>
            ```
        """.trimIndent()

        val result = useCase.parse(raw)

        assertEquals(2, result.size)
        assertEquals("app/src/main/java/com/pla/agen/MainActivity.kt", result[0].path)
        assertEquals("kotlin", result[0].language)
        assertEquals(0, result[0].order)
        assertTrue(result[0].content.contains("class MainActivity"))

        assertEquals("app/src/main/res/values/strings.xml", result[1].path)
        assertEquals("xml", result[1].language)
        assertEquals(1, result[1].order)
    }

    @Test
    fun `parse assigns fallback path when path header is omitted`() {
        val raw = """
            ```kotlin
            val x = 1
            ```
            ```xml
            <tag />
            ```
        """.trimIndent()

        val result = useCase.parse(raw)

        assertEquals(2, result.size)
        assertEquals("generated/file_1.kt", result[0].path)
        assertEquals("generated/file_2.xml", result[1].path)
    }

    @Test
    fun `parse ignores blank code blocks and non-code text`() {
        val raw = """
            Hanya teks biasa tanpa blok kode.
            ```kotlin:app/src/main/Empty.kt
               
            ```
        """.trimIndent()

        val result = useCase.parse(raw)

        assertTrue(result.isEmpty())
    }
}
