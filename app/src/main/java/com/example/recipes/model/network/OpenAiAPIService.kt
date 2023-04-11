package com.example.recipes.model.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class OpenAiAPIService {
    private val openAiAPI = Retrofit.Builder()
        .baseUrl("https://api.openai.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(ChatGPTApiInterface::class.java)


    suspend fun getResponse(request : OpenAIRequest) : ChatGPTResponse {
        return openAiAPI.getCompletion("application/json", "Bearer sk-hehe", request)
    }
}