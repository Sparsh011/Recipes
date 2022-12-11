package com.example.recipes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.model.network.RandomDishApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class SearchRecipeViewModel: ViewModel() {
    private val searchRecipeApiService = RandomDishApiService()

    fun searchRecipe(query: String) : SearchRecipeResult?{
        var searchRecipeResult : SearchRecipeResult? = null

        viewModelScope.launch(Dispatchers.IO) {
             val searchDishesResponse = try {
                searchRecipeApiService.getSearchRecipeResult(query)
            } catch (e : IOException){
                Log.e("SearchRecipeException", e.message.toString())
                 return@launch
            }

            if (searchDishesResponse.isSuccessful && searchDishesResponse.body() != null){
                searchRecipeResult = searchDishesResponse.body()
                Log.i("searchRecipe", searchDishesResponse.body()!!.results.size.toString())
            }
        }

        return searchRecipeResult
    }
}