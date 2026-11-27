package com.pla.agen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun InputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onAttach: () -> Unit,
    onMic: () -> Unit,
    onSend: () -> Unit,
    isSending: Boolean
) {
    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onAttach,
                modifier = Modifier.semantics { contentDescription = "Unggah gambar, dokumen, atau file" }
            ) { Icon(Icons.Filled.AttachFile, contentDescription = null) }

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tuliskan perintah pembuatan APK...") },
                maxLines = 4
            )

            IconButton(
                onClick = onMic,
                modifier = Modifier.semantics { contentDescription = "Diktekan perintah lewat suara" }
            ) { Icon(Icons.Filled.Mic, contentDescription = null) }

            IconButton(
                onClick = onSend,
                enabled = !isSending && text.isNotBlank(),
                modifier = Modifier.semantics { contentDescription = "Kirim perintah ke Gemini" }
            ) {
                if (isSending) CircularProgressIndicator(Modifier.size(24.dp))
                else Icon(Icons.Filled.Send, contentDescription = null)
            }
        }
    }
}
