package com.example.recipes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.model.network.RandomDishApiService
import com.example.recipes.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchRecipeViewModel: ViewModel() {
    private val searchRecipeApiService = RandomDishApiService()
    val searchRecipeObserver : MutableLiveData<Resource<SearchRecipeResult>> = MutableLiveData()

    fun searchRecipe(query: String) = viewModelScope.launch {
        searchRecipeObserver.postValue(Resource.Loading())
        val response = searchRecipeApiService.getSearchRecipeResult(query)
        searchRecipeObserver.postValue(handleSearchRecipeResponse(response))
    }

    private fun handleSearchRecipeResponse(response: Response<SearchRecipeResult>): Resource<SearchRecipeResult> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }

        return Resource.Error(response.message())
    }
}