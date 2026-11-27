package com.pla.agen.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pla.agen.data.remote.GeminiModel

@Composable
fun SettingsScreen(vm: SettingsViewModel = hiltViewModel()) {
    val state by vm.ui.collectAsState()
    val scroll = rememberScrollState()

    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Pengaturan", style = MaterialTheme.typography.headlineSmall)

        SectionHeader("Multi-API Key Gemini", "Rotasi real-time jika kuota habis / rate limit.")

        OutlinedTextField(
            value = state.newKeyLabel,
            onValueChange = vm::onNewKeyLabel,
            label = { Text("Label (contoh: Key Utama)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.newKeyValue,
            onValueChange = vm::onNewKeyValue,
            label = { Text("API Key Gemini") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = vm::addKey,
            modifier = Modifier.fillMaxWidth().semantics {
                contentDescription = "Tambah API key Gemini baru"
            }
        ) { Text("Tambah API Key") }

        state.apiKeys.forEach { row ->
            ListItem(
                headlineContent = { Text(row.label) },
                supportingContent = {
                    Text("${row.masked}  •  gagal: ${row.failCount}")
                },
                trailingContent = {
                    IconButton(
                        onClick = { vm.deleteKey(row.id) },
                        modifier = Modifier.semantics {
                            contentDescription = "Hapus API key ${row.label}"
                        }
                    ) { Icon(Icons.Filled.Delete, contentDescription = null) }
                }
            )
            HorizontalDivider()
        }

        SectionHeader("Tambah Fitur / Keahlian", "Muncul sebagai chip di Ruang Percakapan.")
        OutlinedTextField(
            value = state.newSkillName,
            onValueChange = vm::onNewSkillName,
            label = { Text("Nama keahlian (contoh: Arsitek APK Kotlin)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.newSkillDesc,
            onValueChange = vm::onNewSkillDesc,
            label = { Text("Deskripsi / instruksi sistem") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Button(
            onClick = vm::addSkill,
            modifier = Modifier.fillMaxWidth().semantics {
                contentDescription = "Tambah keahlian baru ke beranda percakapan"
            }
        ) { Text("Tambah Keahlian") }

        state.skills.forEach { skill ->
            ListItem(
                headlineContent = { Text(skill.name) },
                supportingContent = { Text(skill.description) },
                trailingContent = {
                    IconButton(
                        onClick = { vm.deleteSkill(skill.id) },
                        modifier = Modifier.semantics {
                            contentDescription = "Hapus keahlian ${skill.name}"
                        }
                    ) { Icon(Icons.Filled.Delete, contentDescription = null) }
                }
            )
            HorizontalDivider()
        }

        SectionHeader("Model Gemini", "Mulai dari Gemini 3 hingga Gemini 4 terbaru.")
        GeminiModel.entries.forEach { model ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = state.preferredModel == model,
                    onClick = { vm.setPreferredModel(model) },
                    modifier = Modifier.semantics {
                        contentDescription = "Pilih model ${model.label}"
                    }
                )
                Text(model.label)
            }
        }

        SectionHeader("Preferensi Aksesibilitas", "Nyaman untuk mata minus & screen reader.")

        Text("Ukuran teks: ${"%.2f".format(state.fontScale)}×")
        Slider(
            value = state.fontScale,
            onValueChange = vm::setFontScale,
            valueRange = 0.85f..1.6f,
            modifier = Modifier.semantics {
                contentDescription = "Atur ukuran teks anti-silau"
            }
        )

        SwitchRow("Getaran umpan balik", state.hapticEnabled, vm::setHaptic)
        SwitchRow("Suara umpan balik", state.soundEnabled, vm::setSound)
        SwitchRow("Mode anti-silau (gelap pekat)", state.antiGlare, vm::setAntiGlare)

        (state.info ?: state.error)?.let { msg ->
            Snackbar(
                action = { TextButton(onClick = vm::dismissMessages) { Text("Tutup") } }
            ) { Text(msg) }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(subtitle, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            modifier = Modifier.semantics { contentDescription = label }
        )
    }
}
