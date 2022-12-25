package com.example.recipes.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.model.entities.RandomDish
import com.example.recipes.model.network.RandomDishApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishViewModel: ViewModel() {
    private val randomRecipeApiService = RandomDishApiService()

    private val compositeDisposable = CompositeDisposable()

    val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishResponse = MutableLiveData<RandomDish.Recipes>()
    val randomDishLoadingError = MutableLiveData<Boolean>()

    fun getRandomRecipeFromApi(context: Context){
        if (hasNetwork(context)){
            loadRandomDish.value = true
            compositeDisposable.add(
                randomRecipeApiService.getRandomDish()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<RandomDish.Recipes>(){
                        override fun onSuccess(value: RandomDish.Recipes) {
                            loadRandomDish.value = false
                            randomDishResponse.value = value
                            randomDishLoadingError.value = false
                        }

                        override fun onError(e: Throwable) {
                            loadRandomDish.value = false
                            randomDishLoadingError.value = true
                            e.printStackTrace()
                        }
                    })
            )
        }

       else{
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show()
            Log.e("RandomDishViewModel", "getRandomRecipeFromApi: No Internet Connection!")
        }
    }

    private fun hasNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        if (Build.VERSION.SDK_INT >= M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true

                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true

                    else -> false
                }
            }
        }

        return false
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}