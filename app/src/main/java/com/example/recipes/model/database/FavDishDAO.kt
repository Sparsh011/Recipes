package com.example.recipes.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.recipes.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

// Note - DAO must be an interface or abstract class. By default, all queries must be executed on separate thread.

@Dao
interface FavDishDAO {
    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID") // ID is the order in which dish was added to the table
    fun getAllDishes() : Flow<List<FavDish>> // Flow is used to observe the changes made to data.
}