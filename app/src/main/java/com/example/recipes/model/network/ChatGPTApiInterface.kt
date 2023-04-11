package com.example.recipes.model.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatGPTApiInterface {
    @POST("/v1/completions")
    suspend fun getCompletion(
        @Header("Content-Type") contentType: String,
        @Header("Authorization") authorization: String,
        @Body request: OpenAIRequest
    ): ChatGPTResponse
}