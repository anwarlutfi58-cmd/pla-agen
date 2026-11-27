package com.pla.agen.domain.model

data class CodeFile(
    val path: String,
    val language: String,
    val content: String,
    val order: Int
)