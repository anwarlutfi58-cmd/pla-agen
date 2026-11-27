package com.pla.agen.ui.screens.archive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pla.agen.ui.components.MessageBubble
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ArchiveScreen(vm: ArchiveViewModel = hiltViewModel()) {
    val state by vm.ui.collectAsState()
    val fmt = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID")) }

    if (state.selected == null) {
        LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
            item {
                Text(
                    "Arsip Memori Percakapan",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
            items(state.conversations, key = { it.id }) { conv ->
                ListItem(
                    headlineContent = { Text(conv.title) },
                    supportingContent = { Text(fmt.format(Date(conv.createdAt))) },
                    modifier = Modifier
                        .clickable { vm.open(conv) }
                        .semantics {
                            contentDescription = "Buka arsip ${conv.title}"
                        },
                    trailingContent = {
                        IconButton(
                            onClick = { vm.delete(conv) },
                            modifier = Modifier.semantics {
                                contentDescription = "Hapus arsip ${conv.title}"
                            }
                        ) { Icon(Icons.Filled.Delete, contentDescription = null) }
                    }
                )
                HorizontalDivider()
            }
        }
    } else {
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                TextButton(onClick = vm::close) { Text("Kembali") }
                Text(state.selected!!.title, style = MaterialTheme.typography.titleMedium)
            }
            LazyColumn(Modifier.weight(1f)) {
                items(state.messages) { msg ->
                    MessageBubble(role = msg.role) { Text(msg.text) }
                }
            }
        }
    }

    state.info?.let { info ->
        Snackbar(
            action = { TextButton(onClick = vm::clearInfo) { Text("Tutup") } }
        ) { Text(info) }
    }
}
