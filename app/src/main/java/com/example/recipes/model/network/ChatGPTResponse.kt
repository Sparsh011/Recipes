package com.example.recipes.model.network

data class ChatGPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val text: String,
    val index: Int,
    val logprobs: Any?,
    val finish_reason: String?
)
