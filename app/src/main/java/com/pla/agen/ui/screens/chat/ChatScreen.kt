package com.pla.agen.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pla.agen.data.local.entity.MessageRole
import com.pla.agen.ui.components.InputBar
import com.pla.agen.ui.components.MessageBubble

private data class ChatMessage(val role: MessageRole, val text: String)

@Composable
fun ChatScreen() {
    var input by rememberSaveable { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Text(
                        "Ruang Percakapan PLA Agen.\nTulis perintahmu di bawah untuk memulai.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(messages) { msg ->
                MessageBubble(role = msg.role) { Text(msg.text) }
            }
        }
        InputBar(
            text = input,
            onTextChange = { input = it },
            onAttach = { },
            onMic = { },
            onSend = {
                if (input.isNotBlank()) {
                    messages.add(ChatMessage(MessageRole.USER, input.trim()))
                    messages.add(
                        ChatMessage(
                            MessageRole.AGENT,
                            "Sambungan ke Gemini sedang disiapkan. Tambahkan API key di Pengaturan."
                        )
                    )
                    input = ""
                }
            },
            isSending = false
        )
    }
}