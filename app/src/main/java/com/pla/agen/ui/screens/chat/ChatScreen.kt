package com.pla.agen.ui.screens.chat

import android.Manifest
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pla.agen.data.local.entity.MessageRole
import com.pla.agen.ui.components.CodeBlockCard
import com.pla.agen.ui.components.InputBar
import com.pla.agen.ui.components.MessageBubble
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(vm: ChatViewModel = hiltViewModel()) {
    val state by vm.ui.collectAsState()
    val listState = rememberLazyListState()

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let(vm::attachImage) }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spoken = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
        if (!spoken.isNullOrBlank()) {
            vm.onInput(if (state.input.isBlank()) spoken else state.input + " " + spoken)
        }
    }

    val micPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ucapkan perintahmu...")
            }
            runCatching { speechLauncher.launch(intent) }
        }
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ruang Percakapan", style = MaterialTheme.typography.titleMedium)
            IconButton(
                onClick = vm::newConversation,
                modifier = Modifier.semantics { contentDescription = "Mulai percakapan baru" }
            ) { Icon(Icons.Filled.Add, contentDescription = null) }
        }

        if (state.skills.isNotEmpty()) {
            FlowRow(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.skills.forEach { skill ->
                    FilterChip(
                        selected = state.activeSkillId == skill.id,
                        onClick = { vm.toggleSkill(skill.id) },
                        label = { Text(skill.name) },
                        modifier = Modifier.semantics {
                            contentDescription = "Keahlian ${skill.name}"
                        }
                    )
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            if (state.messages.isEmpty()) {
                item {
                    Text(
                        "Tulis perintahmu di bawah.\nContoh: \"Buatkan aplikasi kalkulator Kotlin lengkap\".\nHasil kode bisa langsung diunduh sebagai ZIP.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(state.messages, key = { it.id }) { msg ->
                MessageBubble(role = msg.role) {
                    Text(msg.text)
                    if (msg.role == MessageRole.AGENT) {
                        val files = remember(msg.id) { vm.codeFilesFor(msg) }
                        if (files.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            files.forEach { file ->
                                CodeBlockCard(file = file, onDownload = { vm.downloadZip(listOf(it)) })
                            }
                            Button(
                                onClick = { vm.downloadZip(files) },
                                modifier = Modifier.fillMaxWidth().semantics {
                                    contentDescription = "Unduh seluruh proyek sebagai ZIP"
                                }
                            ) {
                                Icon(Icons.Filled.FolderZip, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Unduh Semua (${files.size} file) sebagai ZIP")
                            }
                        }
                    }
                }
            }
            if (state.isSending) {
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Gemini sedang bekerja...")
                    }
                }
            }
        }

        state.attachedImageName?.let { name ->
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = vm::clearAttachment,
                    label = { Text("Lampiran: $name") },
                    trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Hapus lampiran") }
                )
            }
        }

        (state.info ?: state.error)?.let { msg ->
            Snackbar(
                action = { TextButton(onClick = vm::dismissMessages) { Text("Tutup") } },
                modifier = Modifier.padding(8.dp)
            ) { Text(msg) }
        }

        InputBar(
            text = state.input,
            onTextChange = vm::onInput,
            onAttach = { imagePicker.launch("image/*") },
            onMic = { micPermission.launch(Manifest.permission.RECORD_AUDIO) },
            onSend = vm::send,
            isSending = state.isSending
        )
    }
}