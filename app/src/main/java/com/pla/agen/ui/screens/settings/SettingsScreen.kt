package com.pla.agen.ui.screens.settings

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

@Composable
fun SettingsScreen(vm: SettingsViewModel = hiltViewModel()) {
    val state by vm.ui.collectAsState()
    var label by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Multi-API Key Gemini (Rotasi Otomatis)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label Key (mis. Key 1)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = secret,
                onValueChange = { secret = it },
                label = { Text("Nilai API Key") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Button(
                onClick = {
                    vm.addApiKey(label, secret)
                    label = ""
                    secret = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simpan Key Terenkripsi") }
        }

        items(state.keys, key = { it.id }) { keyItem ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${keyItem.label} (Gagal: ${keyItem.failCount})")
                    IconButton(onClick = { vm.deleteApiKey(keyItem.id) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Hapus key")
                    }
                }
            }
        }

        item {
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text("Tambah Keahlian Agen", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = state.skillName,
                onValueChange = vm::onSkillNameChange,
                label = { Text("Nama Keahlian") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = state.skillDesc,
                onValueChange = vm::onSkillDescChange,
                label = { Text("Instruksi Keahlian / Sistem") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Button(onClick = vm::addSkill, modifier = Modifier.fillMaxWidth()) {
                Text("Tambah Keahlian")
            }
        }

        items(state.skills, key = { it.first }) { (id, name) ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(name)
                    IconButton(onClick = { vm.deleteSkill(id) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Hapus keahlian")
                    }
                }
            }
        }

        state.statusMessage?.let { msg ->
            item {
                Snackbar(
                    action = { TextButton(onClick = vm::clearStatus) { Text("Tutup") } }
                ) { Text(msg) }
            }
        }
    }
}
