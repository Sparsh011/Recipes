package com.example.recipes.model.entities

data class SearchRecipeResult(
    val number: Int,
    val offset: Int,
    val results: List<Result>,
    val totalResults: Int
)