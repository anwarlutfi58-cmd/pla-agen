package com.pla.agen.ui.screens.archive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ArchiveScreen(vm: ArchiveViewModel = hiltViewModel()) {
    val list by vm.conversations.collectAsState()
    val formatter = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Arsip Memori Percakapan", style = MaterialTheme.typography.titleMedium)
        }
        items(list, key = { it.id }) { conv ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(conv.title, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            formatter.format(Date(conv.createdAt)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    IconButton(onClick = { vm.deleteConversation(conv.id) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Hapus arsip")
                    }
                }
            }
        }
    }
}
