package com.pla.agen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
) {
    companion object {
        const val FONT_SCALE = "font_scale"
        const val HAPTIC = "haptic_enabled"
        const val SOUND = "sound_enabled"
        const val ANTI_GLARE = "anti_glare"
        const val PREFERRED_MODEL = "preferred_model"
    }
}