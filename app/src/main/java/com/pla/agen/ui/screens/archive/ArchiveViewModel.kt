package com.pla.agen.ui.screens.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pla.agen.data.local.dao.ConversationDao
import com.pla.agen.data.local.dao.MessageDao
import com.pla.agen.data.local.entity.ConversationEntity
import com.pla.agen.data.local.entity.MessageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArchiveUiState(
    val conversations: List<ConversationEntity> = emptyList(),
    val selected: ConversationEntity? = null,
    val messages: List<MessageEntity> = emptyList(),
    val info: String? = null
)

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ViewModel() {

    private val _ui = MutableStateFlow(ArchiveUiState())
    val ui = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            conversationDao.all().collect { list ->
                _ui.update { it.copy(conversations = list) }
            }
        }
    }

    fun open(conv: ConversationEntity) = viewModelScope.launch {
        val msgs = messageDao.messagesSync(conv.id)
        _ui.update { it.copy(selected = conv, messages = msgs) }
    }

    fun close() = _ui.update { it.copy(selected = null, messages = emptyList()) }

    fun rename(conv: ConversationEntity, newTitle: String) = viewModelScope.launch {
        conversationDao.rename(conv.id, newTitle)
        _ui.update { it.copy(info = "Judul diperbarui.") }
    }

    fun delete(conv: ConversationEntity) = viewModelScope.launch {
        messageDao.deleteForConversation(conv.id)
        conversationDao.delete(conv.id)
        if (_ui.value.selected?.id == conv.id) close()
        _ui.update { it.copy(info = "Percakapan dihapus.") }
    }

    fun clearInfo() = _ui.update { it.copy(info = null) }
}
