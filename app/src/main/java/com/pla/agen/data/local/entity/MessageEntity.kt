package com.pla.agen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageRole { USER, AGENT }

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val role: MessageRole,
    val text: String,
    val attachmentUris: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
