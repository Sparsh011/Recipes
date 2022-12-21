package com.example.recipes.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.application.FavDishApplication
import com.example.recipes.databinding.ActivitySearchRecipeFromApiBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.model.entities.SearchRecipe
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.utils.Constants
import com.example.recipes.utils.Resource
import com.example.recipes.view.adapters.SearchRecipesAdapter
import com.example.recipes.viewmodel.SearchRecipeViewModel
import com.example.recipes.viewmodel.SearchRecipeViewModelFactory

class SearchRecipeFromAPI : AppCompatActivity() {
    private var mBinding : ActivitySearchRecipeFromApiBinding? = null
    private lateinit var searchRecipesAdapter: SearchRecipesAdapter
    private val TAG = "searchRecipeActivity"

    private val searchRecipeViewModel: SearchRecipeViewModel by viewModels {
        SearchRecipeViewModelFactory(((this.application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySearchRecipeFromApiBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        supportActionBar!!.hide()

        mBinding!!.ivSearchRecipe.setOnClickListener{
            val searchQuery = mBinding!!.etSearchRecipeFromApi.text.toString()

            searchRecipeViewModel.searchRecipe(searchQuery)
        }


        mBinding!!.ivGoBackFromSearchRecipe.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        observeSearchRecipeResult()
    }

    private fun observeSearchRecipeResult() {
        searchRecipeViewModel.searchRecipeObserver.observe(this, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let { recipes ->
                        Log.i(TAG, recipes.results.size.toString())
                        mBinding!!.pbSearchRecipes.visibility = View.GONE
                        populateDataInUI(recipes)
                    }
                }
                is Resource.Error -> {
                    response.message?.let { errorMessage ->
                        Log.e(TAG, "An error occurred: $errorMessage")
                        mBinding!!.pbSearchRecipes.visibility = View.GONE
                    }
                }
                is Resource.Loading -> {
                    mBinding!!.pbSearchRecipes.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun populateDataInUI(searchRecipeResult: SearchRecipeResult) {
        searchRecipesAdapter = SearchRecipesAdapter(this)
        searchRecipesAdapter.updateRecipes(searchRecipeResult)

        mBinding!!.rvSearchedRecipes.apply {
            adapter = searchRecipesAdapter
            layoutManager = LinearLayoutManager(this@SearchRecipeFromAPI)
            visibility = View.VISIBLE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}