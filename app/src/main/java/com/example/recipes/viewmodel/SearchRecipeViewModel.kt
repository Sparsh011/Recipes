package com.example.recipes.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.recipes.model.database.FavDishRepository
import com.example.recipes.model.entities.SearchRecipe
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchRecipeViewModel(private val repository: FavDishRepository): ViewModel() {

    val searchRecipeObserver : MutableLiveData<Resource<SearchRecipeResult>> = MutableLiveData()
    val recipeDetailsObserver : MutableLiveData<Resource<SearchRecipe.RecipesFromSearch>> = MutableLiveData()

    fun searchRecipe(query: String) = viewModelScope.launch {
        searchRecipeObserver.postValue(Resource.Loading())
        val response = repository.getSearchedRecipes(query)
        searchRecipeObserver.postValue(handleSearchRecipeResponse(response))
    }


    fun getRecipeDetails(recipeId: Int) = viewModelScope.launch {
        recipeDetailsObserver.postValue(Resource.Loading())
        val response = repository.recipeDetails(recipeId)
        recipeDetailsObserver.postValue(handleRecipeDetailsResponse(response))
    }

    private fun handleRecipeDetailsResponse(response: Response<SearchRecipe.RecipesFromSearch>): Resource<SearchRecipe.RecipesFromSearch> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }

        return Resource.Error(response.message())
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

class SearchRecipeViewModelFactory(private val repository: FavDishRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchRecipeViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return SearchRecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}