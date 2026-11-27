package com.pla.agen.di

import android.content.Context
import androidx.room.Room
import com.pla.agen.data.local.AppDatabase
import com.pla.agen.data.local.dao.ApiKeyDao
import com.pla.agen.data.local.dao.ConversationDao
import com.pla.agen.data.local.dao.MessageDao
import com.pla.agen.data.local.dao.PreferenceDao
import com.pla.agen.data.local.dao.SkillDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "pla_agen.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()

    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()

    @Provides
    fun provideApiKeyDao(db: AppDatabase): ApiKeyDao = db.apiKeyDao()

    @Provides
    fun provideSkillDao(db: AppDatabase): SkillDao = db.skillDao()

    @Provides
    fun providePreferenceDao(db: AppDatabase): PreferenceDao = db.preferenceDao()
}