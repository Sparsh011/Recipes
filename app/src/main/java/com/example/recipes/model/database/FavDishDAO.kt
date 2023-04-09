package com.example.recipes.model.database

import androidx.room.*
import com.example.recipes.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

// Note - DAO must be an interface or abstract class. By default, all queries must be executed on separate thread.

@Dao
interface FavDishDAO {
    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID") // ID is the order in which dish was added to the table
    fun getAllDishes() : Flow<List<FavDish>> // Flow is used to observe the changes made to data. We don't need to make @Query functions suspend if we return Flow/LiveData as room handles it asynchronously itself.

    @Update
    suspend fun updateFavDishDetails(favDish: FavDish) // This favDish is received from repository

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favourite_dish = 1")
    fun getFavoriteDishesList(): Flow<List<FavDish>>

    @Delete
    suspend fun deleteFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE type = :filterType")
    fun getFilteredDishes(filterType: String): Flow<List<FavDish>>
}








