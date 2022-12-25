package com.example.recipes.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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

    fun searchRecipe(query: String, context: Context) = viewModelScope.launch {
        searchRecipeObserver.postValue(Resource.Loading())

        if (hasNetwork(context)){
            val response = repository.getSearchedRecipes(query)
            searchRecipeObserver.postValue(handleSearchRecipeResponse(response))
        }
        else{
            searchRecipeObserver.postValue(Resource.Error("No Internet Connection!"))
        }
    }


    fun getRecipeDetails(recipeId: Int, context: Context) = viewModelScope.launch {
        recipeDetailsObserver.postValue(Resource.Loading())

        if (hasNetwork(context)){
            val response = repository.recipeDetails(recipeId)
            recipeDetailsObserver.postValue(handleRecipeDetailsResponse(response))
        }
        else{
            recipeDetailsObserver.postValue(Resource.Error("No Internet Connection!"))
        }
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

    private fun hasNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true

                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true

                    else -> false
                }
            }
        }

        return false
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