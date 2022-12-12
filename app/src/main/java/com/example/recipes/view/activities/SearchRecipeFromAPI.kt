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
import com.example.recipes.view.adapters.SearchRecipeResultAdapter
import com.example.recipes.viewmodel.SearchRecipeViewModel

class SearchRecipeFromAPI : AppCompatActivity() {
    private var bindingSearchRecipeFromAPI : ActivitySearchRecipeFromApiBinding? = null
    private lateinit var mSearchRecipeViewModel: SearchRecipeViewModel
    private lateinit var searchRecipeResultAdapter: SearchRecipeResultAdapter
    private val TAG = "searchRecipeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSearchRecipeFromAPI = ActivitySearchRecipeFromApiBinding.inflate(layoutInflater)
        setContentView(bindingSearchRecipeFromAPI!!.root)
        supportActionBar!!.hide()

        mSearchRecipeViewModel = ViewModelProvider(this)[SearchRecipeViewModel::class.java]

        bindingSearchRecipeFromAPI!!.ivSearchRecipe.setOnClickListener{
            val querySearchRecipe = bindingSearchRecipeFromAPI!!.etSearchRecipeFromApi.text.toString()
            mSearchRecipeViewModel.searchRecipe(querySearchRecipe)

            mSearchRecipeViewModel.searchRecipeObserver.observe(this, Observer { response ->
                when(response) {
                    is Resource.Success -> {
                        response.data?.let { searchRecipeResult ->
                            Log.i(TAG, searchRecipeResult.results.size.toString())
                            bindingSearchRecipeFromAPI!!.pbSearchRecipes.visibility = View.GONE
                            populateDataInUI(searchRecipeResult)
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.e(TAG, "An error occurred: $message")
                            bindingSearchRecipeFromAPI!!.pbSearchRecipes.visibility = View.GONE
                        }
                    }
                    is Resource.Loading -> {
                        bindingSearchRecipeFromAPI!!.pbSearchRecipes.visibility = View.VISIBLE
                    }

                    else -> {}
                }
            })

        }
    }

    private fun populateDataInUI(searchRecipeResult: SearchRecipeResult) {
        searchRecipeResultAdapter = SearchRecipeResultAdapter(this)
        searchRecipeResultAdapter.updateRecipes(searchRecipeResult)

        bindingSearchRecipeFromAPI!!.rvSearchedRecipes.apply {
            adapter = searchRecipeResultAdapter
            layoutManager = LinearLayoutManager(this@SearchRecipeFromAPI)
            visibility = View.VISIBLE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        bindingSearchRecipeFromAPI = null
    }
}