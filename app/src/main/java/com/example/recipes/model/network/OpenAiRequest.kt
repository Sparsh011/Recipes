package com.example.recipes.model.network

data class OpenAIRequest(
    val model: String,
    val prompt: String,
    val max_tokens: Int,
    val temperature: Double
)
