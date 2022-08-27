package com.example.recipes.viewmodel

import androidx.lifecycle.*
import com.example.recipes.model.database.FavDishRepository
import com.example.recipes.model.entities.FavDish
import kotlinx.coroutines.launch

// ViewModel provides data to the UI and survive configuration changes(like phone rotation). It acts as a communication centre between UI and repository
class FavDishViewModel(private val repository: FavDishRepository): ViewModel() {
    fun insert(dish: FavDish) = viewModelScope.launch {
//        insertFavDishData will be called in a background thread(which is ViewModel's own coroutine), then this insertFavDishDetails is called in DAO. Then insertFavDishDetails will insert our data into the database
        repository.insertFavDishData(dish)

    }
    val allDishesList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()
}

class FavDishViewModelFactory(private val repository: FavDishRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}