package com.example.recipes.model.network

import com.example.recipes.model.entities.RandomDish
import com.example.recipes.model.entities.SearchRecipe
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RandomDishApiService {
    private val api = Retrofit.Builder().baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RandomDishAPI::class.java)

    fun getRandomDish(): Single<RandomDish.Recipes>{
        return api.getRandomDish(Constants.API_KEY_VALUE, Constants.LIMIT_LICENSE_VALUE, Constants.TAGS_VALUE, Constants.NUMBER_VALUE)
    }

    suspend fun getSearchRecipeResult(query: String) : Response<SearchRecipeResult>{
        return api.searchRecipes(Constants.API_KEY_VALUE, query, Constants.LIMIT_LICENSE_VALUE, 5)
    }

    suspend fun getRecipeDetails(id: Int) : Response<SearchRecipe.RecipesFromSearch>{
        return api.getRecipeDetails(id, Constants.API_KEY_VALUE)
    }
}