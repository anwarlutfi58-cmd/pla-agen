package com.pla.agen.ui.screens.archive

import androidx.lifecycle.ViewModel
import com.pla.agen.data.local.entity.ConversationEntity
import com.pla.agen.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.pla.agen.data.local.dao.ConversationDao
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    chatRepository: ChatRepository,
    private val conversationDao: ConversationDao
) : ViewModel() {

    val conversations: StateFlow<List<ConversationEntity>> = chatRepository
        .allConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteConversation(id: Long) {
        viewModelScope.launch { conversationDao.delete(id) }
    }
}
