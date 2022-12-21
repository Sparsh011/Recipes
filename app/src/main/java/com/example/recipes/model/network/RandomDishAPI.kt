package com.example.recipes.model.network

import com.example.recipes.model.entities.RandomDish
import com.example.recipes.model.entities.SearchRecipe
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RandomDishAPI {
    @GET(Constants.API_ENDPOINT_FOR_RANDOM_DISH)
    fun getRandomDish(
        @Query(Constants.API_KEY) apiKey: String,
        @Query(Constants.LIMIT_LICENSE) limitLicence: Boolean,
        @Query(Constants.TAGS) tags: String,
        @Query(Constants.NUMBER) number: Int
    ): Single<RandomDish.Recipes>


    @GET(Constants.API_ENDPOINT_FOR_SEARCH_RECIPE)
    suspend fun searchRecipes(
        @Query(Constants.API_KEY) apiKey: String,
        @Query(Constants.QUERY) query: String,
        @Query(Constants.LIMIT_LICENSE) limitLicence: Boolean,
        @Query(Constants.NUMBER) number: Int
    ) : Response<SearchRecipeResult>

    @GET(Constants.ENDPOINT_FOR_RECIPE_DETAILS)
    suspend fun getRecipeDetails(
        @Path("id") id: Int,
        @Query(Constants.API_KEY) apiKey: String,
    ) : Response<SearchRecipe.RecipesFromSearch>

}