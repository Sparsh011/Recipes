package com.example.recipes.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipes.R
import com.example.recipes.model.entities.SearchRecipeResult

class SearchRecipesAdapter (
    private val context: Context
        ): RecyclerView.Adapter<SearchRecipesAdapter.SearchRecipeResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecipeResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_recipe_result, parent, false)
        return SearchRecipeResultViewHolder(view)
    }


    override fun onBindViewHolder(holder: SearchRecipeResultViewHolder, position: Int) {
        val recipes = recipesResponse!!.results
        val recipe = recipes[position]
        Glide.with(context)
            .load(recipe.image)
            .centerCrop()
            .placeholder(R.drawable.favorite_button_background)
            .into(holder.recipeImage)

        holder.recipeTitle.text = recipe.title
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