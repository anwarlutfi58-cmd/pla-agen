package com.pla.agen.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pla.agen.data.local.dao.ApiKeyDao
import com.pla.agen.data.local.dao.PreferenceDao
import com.pla.agen.data.local.entity.PreferenceEntity
import com.pla.agen.data.local.entity.SkillEntity
import com.pla.agen.data.remote.GeminiModel
import com.pla.agen.data.repository.ApiKeyManager
import com.pla.agen.data.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApiKeyRow(
    val id: Long,
    val label: String,
    val masked: String,
    val isActive: Boolean,
    val failCount: Int
)

data class SettingsUiState(
    val apiKeys: List<ApiKeyRow> = emptyList(),
    val skills: List<SkillEntity> = emptyList(),
    val fontScale: Float = 1.0f,
    val hapticEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val antiGlare: Boolean = true,
    val preferredModel: GeminiModel = GeminiModel.GEMINI_4_FLASH,
    val info: String? = null,
    val error: String? = null,
    val newKeyLabel: String = "",
    val newKeyValue: String = "",
    val newSkillName: String = "",
    val newSkillDesc: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    app: Application,
    private val apiKeyManager: ApiKeyManager,
    private val skillRepository: SkillRepository,
    private val prefDao: PreferenceDao,
    private val keyDao: ApiKeyDao
) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(SettingsUiState())
    val ui = _ui.asStateFlow()

    init {
        observeKeys()
        observeSkills()
        observePrefs()
    }

    private fun observeKeys() = viewModelScope.launch {
        keyDao.allFlow().collect { list ->
            val rows = list.map {
                ApiKeyRow(
                    id = it.id,
                    label = it.label,
                    masked = apiKeyManager.mask(apiKeyManager.decryptKey(it.keyValue) ?: "?"),
                    isActive = it.isActive,
                    failCount = it.failCount
                )
            }
            _ui.update { it.copy(apiKeys = rows) }
        }
    }

    private fun observeSkills() = viewModelScope.launch {
        skillRepository.all().collect { list ->
            _ui.update { it.copy(skills = list) }
        }
    }

    private fun observePrefs() = viewModelScope.launch {
        prefDao.all().collect { prefs ->
            val map = prefs.associate { it.key to it.value }
            _ui.update {
                it.copy(
                    fontScale = map[PreferenceEntity.FONT_SCALE]?.toFloatOrNull() ?: 1.0f,
                    hapticEnabled = map[PreferenceEntity.HAPTIC]?.toBooleanStrictOrNull() ?: true,
                    soundEnabled = map[PreferenceEntity.SOUND]?.toBooleanStrictOrNull() ?: true,
                    antiGlare = map[PreferenceEntity.ANTI_GLARE]?.toBooleanStrictOrNull() ?: true,
                    preferredModel = map[PreferenceEntity.PREFERRED_MODEL]
                        ?.let { GeminiModel.fromApiName(it) } ?: GeminiModel.GEMINI_4_FLASH
                )
            }
        }
    }

    fun onNewKeyLabel(v: String) = _ui.update { it.copy(newKeyLabel = v) }
    fun onNewKeyValue(v: String) = _ui.update { it.copy(newKeyValue = v) }

    fun addKey() {
        val label = _ui.value.newKeyLabel.trim()
        val raw = _ui.value.newKeyValue.trim()
        if (label.isBlank() || raw.length < 20) {
            _ui.update { it.copy(error = "Label wajib, API key minimal 20 karakter.") }
            return
        }
        viewModelScope.launch {
            runCatching { apiKeyManager.addKey(label, raw) }
                .onSuccess {
                    apiKeyManager.resetRotation()
                    _ui.update {
                        it.copy(
                            newKeyLabel = "", newKeyValue = "",
                            info = "API key '$label' tersimpan terenkripsi."
                        )
                    }
                }
                .onFailure { e -> _ui.update { it.copy(error = "Gagal simpan key: ${e.message}") } }
        }
    }

    fun deleteKey(id: Long) = viewModelScope.launch {
        runCatching { apiKeyManager.deleteKey(id) }
            .onSuccess { apiKeyManager.resetRotation() }
            .onFailure { e -> _ui.update { it.copy(error = "Gagal hapus key: ${e.message}") } }
    }

    fun onNewSkillName(v: String) = _ui.update { it.copy(newSkillName = v) }
    fun onNewSkillDesc(v: String) = _ui.update { it.copy(newSkillDesc = v) }

    fun addSkill() {
        val name = _ui.value.newSkillName.trim()
        val desc = _ui.value.newSkillDesc.trim()
        if (name.isBlank()) {
            _ui.update { it.copy(error = "Nama keahlian wajib diisi.") }
            return
        }
        viewModelScope.launch {
            runCatching { skillRepository.addSkill(name, desc) }
                .onSuccess {
                    _ui.update {
                        it.copy(newSkillName = "", newSkillDesc = "",
                            info = "Keahlian '$name' ditambahkan ke beranda percakapan.")
                    }
                }
                .onFailure { e -> _ui.update { it.copy(error = "Gagal tambah keahlian: ${e.message}") } }
        }
    }

    fun deleteSkill(id: Long) = viewModelScope.launch {
        skillRepository.deleteSkill(id)
    }

    fun setFontScale(v: Float) = save(PreferenceEntity.FONT_SCALE, v.toString())
    fun setHaptic(v: Boolean) = save(PreferenceEntity.HAPTIC, v.toString())
    fun setSound(v: Boolean) = save(PreferenceEntity.SOUND, v.toString())
    fun setAntiGlare(v: Boolean) = save(PreferenceEntity.ANTI_GLARE, v.toString())
    fun setPreferredModel(m: GeminiModel) = save(PreferenceEntity.PREFERRED_MODEL, m.apiName)

    private fun save(key: String, value: String) = viewModelScope.launch {
        prefDao.set(PreferenceEntity(key, value))
    }

    fun dismissMessages() = _ui.update { it.copy(info = null, error = null) }
}
