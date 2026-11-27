package com.pla.agen.data.local

import androidx.room.TypeConverter
import com.pla.agen.data.local.entity.MessageRole

class Converters {
    @TypeConverter
    fun fromRole(role: MessageRole): String = role.name

    @TypeConverter
    fun toRole(value: String): MessageRole = MessageRole.valueOf(value)
}
