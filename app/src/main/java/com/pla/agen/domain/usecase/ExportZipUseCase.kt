package com.pla.agen.domain.usecase

import android.content.Context
import android.os.Environment
import com.pla.agen.domain.model.CodeFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class ExportZipUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun execute(projectName: String, files: List<CodeFile>): File =
        withContext(Dispatchers.IO) {
            val downloads = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            if (!downloads.exists()) downloads.mkdirs()
            val safeName = projectName.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val zipFile = File(downloads, "$safeName-${System.currentTimeMillis()}.zip")
            ZipOutputStream(FileOutputStream(zipFile)).use { zip ->
                files.forEach { file ->
                    zip.putNextEntry(ZipEntry(file.path))
                    zip.write(file.content.toByteArray(Charsets.UTF_8))
                    zip.closeEntry()
                }
            }
            zipFile
        }
}