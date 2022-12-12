package com.example.recipes.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.databinding.ActivitySearchRecipeFromApiBinding
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.utils.Resource
import com.example.recipes.view.adapters.SearchRecipesAdapter
import com.example.recipes.viewmodel.SearchRecipeViewModel

class SearchRecipeFromAPI : AppCompatActivity() {
    private var mBinding : ActivitySearchRecipeFromApiBinding? = null
    private lateinit var searchRecipeViewModel: SearchRecipeViewModel
    private lateinit var searchRecipesAdapter: SearchRecipesAdapter
    private val TAG = "searchRecipeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySearchRecipeFromApiBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        supportActionBar!!.hide()

        searchRecipeViewModel = ViewModelProvider(this)[SearchRecipeViewModel::class.java]

        mBinding!!.ivSearchRecipe.setOnClickListener{
            val searchQuery = mBinding!!.etSearchRecipeFromApi.text.toString()

            searchRecipeViewModel.searchRecipe(searchQuery)

            observeSearchRecipeResult()

        }
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

                else -> {}
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