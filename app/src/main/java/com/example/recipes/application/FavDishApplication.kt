package com.example.recipes.application

import android.app.Application
import com.example.recipes.model.database.FavDishRepository
import com.example.recipes.model.database.FavDishRoomDatabase

class FavDishApplication : Application(){
//    This application is used to define the variable scopes that will be used throughout the application. We can setup our database and repository here. Using lazy because we don't want db to be loaded when app is started, but when it is needed.
    private val database by lazy{
        FavDishRoomDatabase.getDatabase(this@FavDishApplication)
    }
    val repository by lazy {
        FavDishRepository(database.favDishDao())
    }
}