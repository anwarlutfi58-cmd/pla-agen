package com.pla.agen.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pla.agen.data.local.SecureKeyStore
import com.pla.agen.data.local.dao.ApiKeyDao
import com.pla.agen.data.local.entity.ApiKeyEntity
import com.pla.agen.data.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val keys: List<ApiKeyEntity> = emptyList(),
    val skillName: String = "",
    val skillDesc: String = "",
    val skills: List<Pair<Long, String>> = emptyList(),
    val statusMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiKeyDao: ApiKeyDao,
    private val secureKeyStore: SecureKeyStore,
    private val skillRepository: SkillRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(SettingsUiState())
    val ui: StateFlow<SettingsUiState> = _ui.asStateFlow()

    init {
        refreshKeys()
        viewModelScope.launch {
            skillRepository.all().collect { list ->
                _ui.update { it.copy(skills = list.map { s -> s.id to s.name }) }
            }
        }
    }

    fun refreshKeys() {
        viewModelScope.launch {
            val active = apiKeyDao.getActiveKeys()
            _ui.update { it.copy(keys = active) }
        }
    }

    fun addApiKey(label: String, secretKey: String) {
        if (label.isBlank() || secretKey.isBlank()) return
        viewModelScope.launch {
            val rowId = apiKeyDao.upsert(
                ApiKeyEntity(
                    label = label.trim(),
                    keyValue = "",
                    isActive = true
                )
            )
            secureKeyStore.saveSecret(rowId, secretKey.trim())
            _ui.update { it.copy(statusMessage = "API key '$label' tersimpan terenkripsi.") }
            refreshKeys()
        }
    }

    fun deleteApiKey(id: Long) {
        viewModelScope.launch {
            apiKeyDao.delete(id)
            secureKeyStore.removeSecret(id)
            refreshKeys()
        }
    }

    fun onSkillNameChange(v: String) = _ui.update { it.copy(skillName = v) }
    fun onSkillDescChange(v: String) = _ui.update { it.copy(skillDesc = v) }

    fun addSkill() {
        val name = _ui.value.skillName.trim()
        val desc = _ui.value.skillDesc.trim()
        if (name.isBlank() || desc.isBlank()) return
        viewModelScope.launch {
            skillRepository.addSkill(name, desc)
            _ui.update {
                it.copy(
                    skillName = "",
                    skillDesc = "",
                    statusMessage = "Keahlian '$name' ditambahkan."
                )
            }
        }
    }

    fun deleteSkill(id: Long) {
        viewModelScope.launch { skillRepository.deleteSkill(id) }
    }

    fun clearStatus() = _ui.update { it.copy(statusMessage = null) }
}
