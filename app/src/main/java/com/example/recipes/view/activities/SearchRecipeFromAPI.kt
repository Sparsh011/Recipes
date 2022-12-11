package com.example.recipes.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.databinding.ActivitySearchRecipeFromApiBinding
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.view.adapters.SearchRecipeResultAdapter
import com.example.recipes.viewmodel.SearchRecipeViewModel

class SearchRecipeFromAPI : AppCompatActivity() {
    private var bindingSearchRecipeFromAPI : ActivitySearchRecipeFromApiBinding? = null
    private lateinit var mSearchRecipeViewModel: SearchRecipeViewModel
    private lateinit var searchRecipeResultAdapter: SearchRecipeResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSearchRecipeFromAPI = ActivitySearchRecipeFromApiBinding.inflate(layoutInflater)
        setContentView(bindingSearchRecipeFromAPI!!.root)
        supportActionBar!!.hide()

        mSearchRecipeViewModel = ViewModelProvider(this)[SearchRecipeViewModel::class.java]

        bindingSearchRecipeFromAPI!!.ivSearch.setOnClickListener{
            val querySearchRecipe = bindingSearchRecipeFromAPI!!.etSearchRecipeFromApi.text.toString()
            val dishes = mSearchRecipeViewModel.searchRecipe(querySearchRecipe)


        }
    }


    override fun onDestroy() {
        super.onDestroy()
        bindingSearchRecipeFromAPI = null
    }
}