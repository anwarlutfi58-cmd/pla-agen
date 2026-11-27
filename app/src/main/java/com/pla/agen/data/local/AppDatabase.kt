package com.pla.agen.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pla.agen.data.local.dao.ApiKeyDao
import com.pla.agen.data.local.dao.ConversationDao
import com.pla.agen.data.local.dao.MessageDao
import com.pla.agen.data.local.dao.PreferenceDao
import com.pla.agen.data.local.dao.SkillDao
import com.pla.agen.data.local.entity.ApiKeyEntity
import com.pla.agen.data.local.entity.ConversationEntity
import com.pla.agen.data.local.entity.MessageEntity
import com.pla.agen.data.local.entity.PreferenceEntity
import com.pla.agen.data.local.entity.SkillEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        ApiKeyEntity::class,
        SkillEntity::class,
        PreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun apiKeyDao(): ApiKeyDao
    abstract fun skillDao(): SkillDao
    abstract fun preferenceDao(): PreferenceDao
}