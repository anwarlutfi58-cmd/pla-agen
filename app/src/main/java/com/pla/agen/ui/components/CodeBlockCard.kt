package com.pla.agen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pla.agen.domain.model.CodeFile

@Composable
fun CodeBlockCard(
    file: CodeFile,
    onDownload: (CodeFile) -> Unit
) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(file.path, style = MaterialTheme.typography.labelLarge)
            IconButton(
                onClick = { onDownload(file) },
                modifier = Modifier.semantics {
                    contentDescription = "Unduh file ${file.path} ke folder Download"
                }
            ) { Icon(Icons.Filled.Download, contentDescription = null) }
        }
        HorizontalDivider()
        Text(
            text = file.content,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(12.dp)
        )
    }
}
