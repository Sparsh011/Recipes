package com.example.recipes.model.database

import androidx.room.Dao
import androidx.room.Insert
import com.example.recipes.model.entities.FavDish

// Note - DAO must be an interface or abstract class. By default, all queries must be executed on separate thread.

@Dao
interface FavDishDAO {

    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish) // use suspend keyword, which is similar to async. TODO - we added the suspend modifier to the function. That tells the compiler that this function needs to be executed inside a coroutine. Note - Room executes suspend queries OFF the main thread
}