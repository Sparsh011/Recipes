package com.example.recipes.model.database

import androidx.annotation.WorkerThread
import com.example.recipes.model.entities.FavDish
import com.example.recipes.model.entities.SearchRecipe
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.model.network.RandomDishApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


class FavDishRepository(private val favDishDao: FavDishDAO) {

    @WorkerThread // We annotate a method or class with this to indicate it MUST be executed on a different thread than Main/UI Thread.
    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }


//    Retrieving all the dishes
    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishes()

//    Updating data-
    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish){
//    This favDish is received from FavDishViewModel and then this favDish is passed to DAO
        favDishDao.updateFavDishDetails(favDish)
    }

    val favoriteDishes: Flow<List<FavDish>> = favDishDao.getFavoriteDishesList()

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish){
//    This favDish is received from FavDishViewModel and then this favDish is passed to DAO
        favDishDao.deleteFavDishDetails(favDish)
    }

    fun filteredListDishes(value: String): Flow<List<FavDish>> = favDishDao.getFilteredDishes(value)

    private val retrofitInstance = RandomDishApiService()

    suspend fun getSearchedRecipes(query: String): Response<SearchRecipeResult> {
        return retrofitInstance.getSearchRecipeResult(query)
    }

    suspend fun recipeDetails(recipeId: Int) : Response<SearchRecipe.RecipesFromSearch>{
        return retrofitInstance.getRecipeDetails(recipeId)
    }
}









