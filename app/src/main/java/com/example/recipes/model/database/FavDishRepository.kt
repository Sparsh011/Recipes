package com.example.recipes.model.database

import androidx.annotation.WorkerThread
import com.example.recipes.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

// Note - Use of repository - Repository will mediates between the domain and data mapping layers using a collection-like interface for accessing domain objects. It means that repository will just need access to DAO as DAO contains all the read and write methods. There's no need to expose the entire database to the repository. In using the Repository design pattern, we can hide the details of how the data is eventually stored or retrieved to and from the data store.

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
}









