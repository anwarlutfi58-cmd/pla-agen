package com.pla.agen.domain.usecase

import android.content.Context
import android.os.Environment
import com.pla.agen.domain.model.CodeFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class ExportZipUseCase @Inject constructor(
    private val context: Context
) {
    suspend fun execute(projectName: String, files: List<CodeFile>): File =
        withContext(Dispatchers.IO) {
            val downloads = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            val zipFile = File(downloads, "$projectName-${System.currentTimeMillis()}.zip")
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
