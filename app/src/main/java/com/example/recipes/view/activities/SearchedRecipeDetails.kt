package com.example.recipes.view.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.recipes.R
import com.example.recipes.application.FavDishApplication
import com.example.recipes.databinding.ActivitySearchedRecipeDetailsBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.model.entities.SearchRecipe
import com.example.recipes.utils.Resource
import com.example.recipes.viewmodel.FavDishViewModel
import com.example.recipes.viewmodel.FavDishViewModelFactory
import com.example.recipes.viewmodel.SearchRecipeViewModel
import com.example.recipes.viewmodel.SearchRecipeViewModelFactory

@Suppress("DEPRECATION")
class SearchedRecipeDetails : AppCompatActivity() {
    private var mBinding: ActivitySearchedRecipeDetailsBinding? = null
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((this.application) as FavDishApplication).repository)
    }
    private val searchRecipeViewModel: SearchRecipeViewModel by viewModels {
        SearchRecipeViewModelFactory(((this.application) as FavDishApplication).repository)
    }
    private val TAG = "SearchRecipeDetails"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySearchedRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        supportActionBar!!.hide()

        val recipeId = intent.getIntExtra("recipeId", -1)
        searchRecipeViewModel.getRecipeDetails(recipeId, this)
        var recipe: FavDish? = null

        searchRecipeViewModel.recipeDetailsObserver.observe(this, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let { recipeFromSearch ->
                        Log.i(TAG, "Successful Search Recipe Response")

                        val ingredients = recipeFromSearch.extendedIngredients
                        val finalIngredients = convertIngredientsListToString(ingredients)
                        var dishType = "Other"

                        if (recipeFromSearch.dishTypes.isNotEmpty()){
                            dishType = recipeFromSearch.dishTypes[0]
                        }

                        recipe = FavDish(recipeFromSearch.image,
                            recipeFromSearch.sourceUrl,
                            recipeFromSearch.title,
                            dishType,
                            "Other",
                            finalIngredients,
                            recipeFromSearch.readyInMinutes.toString(),
                            recipeFromSearch.instructions,
                            false,
                            0
                        )

                        populateDataInUI(recipe)

                    }
                }

                is Resource.Error -> {
                    response.message?.let { errorMessage ->
                        Toast.makeText(this, "An error occurred: $errorMessage", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "An error occurred: $errorMessage")
                    }
                }

                is Resource.Loading -> {
                }
            }
        })



        mBinding!!.ivFavoriteDish.setOnClickListener{
            recipe!!.favouriteDish = !recipe!!.favouriteDish

            if (recipe!!.favouriteDish){
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_selected))
                Toast.makeText(this, "Added To Favorites!", Toast.LENGTH_SHORT).show()
                mFavDishViewModel.insert(recipe!!)
            }
            else{
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_unselected))
                Toast.makeText(this, "Removed From Favorites!", Toast.LENGTH_SHORT).show()
                mFavDishViewModel.update(recipe!!)
            }
        }
    }

    private fun populateDataInUI(recipe: FavDish?) {
        Glide.with(this)
            .load(recipe!!.image)
            .into(mBinding!!.ivDishImage)

        mBinding!!.tvTitle.text = recipe.title
        mBinding!!.tvCategory.text =  recipe.category
        mBinding!!.tvIngredients.text = recipe.ingredients
        mBinding!!.tvCookingTime.text = "Approximate Cooking Time: " + recipe.cookingTime + " minutes"
        mBinding!!.tvType.text = recipe.type


        var directionsToCook = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            directionsToCook = Html.fromHtml(
                recipe.directionToCook,
                Html.FROM_HTML_MODE_COMPACT
            ).toString()
        }
        else {
            directionsToCook = Html.fromHtml(recipe.directionToCook).toString()
        }

        mBinding!!.tvCookingDirection.text = directionsToCook

        if (recipe.favouriteDish){
            mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_selected))
        }
        else{
            mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_unselected))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    override fun onBackPressed() {
        finish()
    }

    private fun convertIngredientsListToString(ingredients: List<SearchRecipe.ExtendedIngredient>): String {
        var result = ""
        for (ingredient in ingredients){
            result += ingredient.name + " and it's quantity - " + ingredient.amount + " ${ingredient.measures.metric.unitShort}" + "\n"
        }

        return result
    }
}