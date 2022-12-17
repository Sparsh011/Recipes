package com.example.recipes.view.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.recipes.R
import com.example.recipes.application.FavDishApplication
import com.example.recipes.databinding.ActivitySearchedRecipeDetailsBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.utils.Constants
import com.example.recipes.viewmodel.FavDishViewModel
import com.example.recipes.viewmodel.FavDishViewModelFactory

@Suppress("DEPRECATION")
class SearchedRecipeDetails : AppCompatActivity() {
    private var mBinding: ActivitySearchedRecipeDetailsBinding? = null
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((this.application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySearchedRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)


        val recipe = intent.getParcelableExtra<FavDish>(Constants.SEARCH_RECIPE)
        populateDataInUI(recipe)


        mBinding!!.ivFavoriteDish.setOnClickListener{
            recipe!!.favouriteDish = !recipe.favouriteDish

            if (recipe.favouriteDish){
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_selected))
                Toast.makeText(this, "Added To Favorites!", Toast.LENGTH_SHORT).show()
                mFavDishViewModel.insert(recipe)
            }
            else{
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_unselected))
                Toast.makeText(this, "Removed From Favorites!", Toast.LENGTH_SHORT).show()
                mFavDishViewModel.update(recipe)
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
}