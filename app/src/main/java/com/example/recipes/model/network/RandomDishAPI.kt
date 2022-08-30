package com.example.recipes.model.network

import com.example.recipes.model.entities.RandomDish
import com.example.recipes.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomDishAPI {
//    In this function, pass the three parameters that are there in the documentation
    @GET(Constants.API_ENDPOINT)
    fun getRandomDish(
        @Query(Constants.API_KEY) apiKey: String,
        @Query(Constants.LIMIT_LICENSE) limitLicence: Boolean,
        @Query(Constants.TAGS) tags: String,
        @Query(Constants.NUMBER) number: Int
    ): Single<RandomDish.Recipes>
//    Single class is used to get only a single kind of response, i.e success or failure.
}