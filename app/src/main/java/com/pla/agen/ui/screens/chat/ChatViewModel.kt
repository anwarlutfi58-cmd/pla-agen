package com.pla.agen.ui.screens.chat

import android.app.Application
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pla.agen.data.local.dao.ConversationDao
import com.pla.agen.data.local.dao.MessageDao
import com.pla.agen.data.local.dao.PreferenceDao
import com.pla.agen.data.local.entity.ConversationEntity
import com.pla.agen.data.local.entity.MessageEntity
import com.pla.agen.data.local.entity.MessageRole
import com.pla.agen.data.local.entity.PreferenceEntity
import com.pla.agen.data.local.entity.SkillEntity
import com.pla.agen.data.remote.GeminiModel
import com.pla.agen.data.repository.ChatRepository
import com.pla.agen.data.repository.ChatTurn
import com.pla.agen.data.repository.SkillRepository
import com.pla.agen.domain.model.CodeFile
import com.pla.agen.domain.usecase.ExportZipUseCase
import com.pla.agen.domain.usecase.GitHubLinkUseCase
import com.pla.agen.domain.usecase.ParseCodeBlocksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<MessageEntity> = emptyList(),
    val skills: List<SkillEntity> = emptyList(),
    val activeSkillId: Long? = null,
    val input: String = "",
    val isSending: Boolean = false,
    val attachedImageName: String? = null,
    val info: String? = null,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    app: Application,
    private val chatRepository: ChatRepository,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val preferenceDao: PreferenceDao,
    private val skillRepository: SkillRepository,
    private val parseCodeBlocks: ParseCodeBlocksUseCase,
    private val exportZip: ExportZipUseCase,
    private val gitHubLink: GitHubLinkUseCase
) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(ChatUiState())
    val ui = _ui.asStateFlow()

    private var conversationId: Long = 0
    private var imageBase64: String? = null
    private var imageMime: String? = null

    init {
        viewModelScope.launch {
            skillRepository.enabled().collect { list ->
                _ui.update { it.copy(skills = list) }
            }
        }
    }

    fun onInput(v: String) = _ui.update { it.copy(input = v) }

    fun toggleSkill(id: Long) = _ui.update {
        it.copy(activeSkillId = if (it.activeSkillId == id) null else id)
    }

    fun attachImage(uri: Uri) {
        runCatching {
            val resolver = getApplication<Application>().contentResolver
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("Tidak bisa membaca file.")
            if (bytes.size > 4_000_000) error("Gambar terlalu besar (maks 4 MB).")
            imageBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            imageMime = resolver.getType(uri) ?: "image/jpeg"
            _ui.update { it.copy(attachedImageName = uri.lastPathSegment ?: "gambar") }
        }.onFailure { e ->
            _ui.update { it.copy(error = "Gagal melampirkan: ${e.message}") }
        }
    }

    fun clearAttachment() {
        imageBase64 = null
        imageMime = null
        _ui.update { it.copy(attachedImageName = null) }
    }

    fun send() {
        val text = _ui.value.input.trim()
        if (text.isBlank() || _ui.value.isSending) return
        _ui.update { it.copy(input = "", isSending = true, error = null) }

        viewModelScope.launch {
            runCatching {
                if (conversationId == 0L) {
                    conversationId = conversationDao.insert(
                        ConversationEntity(title = text.take(40))
                    )
                }
                val userMsg = MessageEntity(
                    conversationId = conversationId,
                    role = MessageRole.USER,
                    text = text
                )
                messageDao.insert(userMsg)
                refreshMessages()

                val skill = _ui.value.skills.firstOrNull { it.id == _ui.value.activeSkillId }
                val systemParts = mutableListOf<String>()
                skill?.let { systemParts.add("Keahlian aktif: ${it.name}. ${it.description}") }
                if (gitHubLink.isGitHubLink(text)) {
                    systemParts.add(gitHubLink.directExecutionInstruction(containsError = true))
                }
                val modelName = preferenceDao.get(PreferenceEntity.PREFERRED_MODEL)
                val model = modelName?.let { GeminiModel.fromApiName(it) }
                    ?: GeminiModel.GEMINI_4_FLASH

                val history = messageDao.messagesSync(conversationId).map {
                    ChatTurn(isUser = it.role == MessageRole.USER, text = it.text)
                }
                val reply = chatRepository.send(
                    history = history,
                    systemInstruction = systemParts.joinToString("\n\n").ifBlank { null },
                    model = model,
                    imageBase64 = imageBase64,
                    imageMime = imageMime
                )
                messageDao.insert(
                    MessageEntity(
                        conversationId = conversationId,
                        role = MessageRole.AGENT,
                        text = reply
                    )
                )
                clearAttachment()
                refreshMessages()
            }.onFailure { e ->
                _ui.update { it.copy(error = "Gagal kirim: ${e.message}") }
            }
            _ui.update { it.copy(isSending = false) }
        }
    }

    private suspend fun refreshMessages() {
        val msgs = messageDao.messagesSync(conversationId)
        _ui.update { it.copy(messages = msgs) }
    }

    fun codeFilesFor(message: MessageEntity): List<CodeFile> =
        parseCodeBlocks.parse(message.text)

    fun downloadZip(files: List<CodeFile>) {
        viewModelScope.launch {
            runCatching {
                val file = exportZip.execute("pla-agen-proyek", files)
                _ui.update { it.copy(info = "ZIP tersimpan: Download/${file.name}") }
            }.onFailure { e ->
                _ui.update { it.copy(error = "Gagal membuat ZIP: ${e.message}") }
            }
        }
    }

    fun newConversation() {
        conversationId = 0
        clearAttachment()
        _ui.update { it.copy(messages = emptyList(), info = "Percakapan baru dimulai.") }
    }

    fun dismissMessages() = _ui.update { it.copy(info = null, error = null) }
}