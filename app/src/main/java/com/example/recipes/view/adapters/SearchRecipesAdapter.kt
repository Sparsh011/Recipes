package com.example.recipes.view.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipes.R
import com.example.recipes.model.entities.SearchRecipeResult
import com.example.recipes.view.activities.SearchRecipeFromAPI
import com.example.recipes.view.activities.SearchedRecipeDetails

class SearchRecipesAdapter (
    private val activity: Activity
        ): RecyclerView.Adapter<SearchRecipesAdapter.SearchRecipeResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecipeResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_recipe_result, parent, false)
        return SearchRecipeResultViewHolder(view)
    }


    override fun onBindViewHolder(holder: SearchRecipeResultViewHolder, position: Int) {
        val recipes = recipesResponse!!.results
        val recipe = recipes[position]
        Glide.with(activity.applicationContext)
            .load(recipe.image)
            .centerCrop()
            .placeholder(R.drawable.favorite_button_background)
            .into(holder.recipeImage)

        holder.recipeTitle.text = recipe.title

        holder.itemView.setOnClickListener{
            val recipeID = recipe.id
            Log.i("InAdapter", "Clicked")

            val intent = Intent(activity, SearchedRecipeDetails::class.java)
            intent.putExtra("recipeId", recipeID)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.applicationContext.startActivity(intent)
        }
    }

    private var recipesResponse : SearchRecipeResult?  = null

    override fun getItemCount(): Int {
        return recipesResponse!!.results.size
    }

    fun updateRecipes(recipeResult: SearchRecipeResult){
        recipesResponse = recipeResult
        notifyDataSetChanged()
    }

    inner class SearchRecipeResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val recipeImage : ImageView = itemView.findViewById(R.id.iv_search_recipe_image)
        val recipeTitle : TextView = itemView.findViewById(R.id.tv_search_recipe_title)
    }
}