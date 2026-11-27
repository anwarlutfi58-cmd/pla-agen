package com.pla.agen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pla.agen.data.local.entity.MessageRole

@Composable
fun MessageBubble(
    role: MessageRole,
    content: @Composable ColumnScope.() -> Unit
) {
    val isUser = role == MessageRole.USER
    Surface(
        color = if (isUser) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = if (isUser) "PERTANYAAN / PERINTAH" else "JAWABAN AGEN GEMINI",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            content()
        }
    }
}
