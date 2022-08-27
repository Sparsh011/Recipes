package com.example.recipes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recipes.model.database.FavDishRepository
import com.example.recipes.model.entities.FavDish
import kotlinx.coroutines.launch

// ViewModel provides data to the UI and survive configuration changes(like phone rotation). It acts as a communication centre between UI and repository
class FavDishViewModel(private val repository: FavDishRepository): ViewModel() {
    fun insert(dish: FavDish) = viewModelScope.launch {
//        insertFavDishData will be called in a background thread, then this insertFavDishDetails is called in DAO. Then insertFavDishDetails will insert our data into the database
        repository.insertFavDishData(dish)
//        This is called on a separate thread (via coroutine)
    }
}

//
class FavDishViewModelFactory(private val repository: FavDishRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}