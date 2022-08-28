package com.example.recipes.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipes.databinding.ItemDishLayoutBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.view.fragments.AllDishesFragment

class FavDishAdapter(
    private val fragment: Fragment
): RecyclerView.Adapter<FavDishAdapter.ViewHolder>(){
//    Didn't pass dishes as argument because will be using an observer which notifies the changes in ui.
    private var dishes: List<FavDish> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDishLayoutBinding = ItemDishLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)
        holder.tvTitle.text = dish.title

        holder.itemView.setOnClickListener{
            if (fragment is AllDishesFragment){
                fragment.dishDetails(dish)
            }
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list: List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: ItemDishLayoutBinding): RecyclerView.ViewHolder(view.root){
        val ivDishImage = view.ivDishImageInCardView
        val tvTitle = view.tvDishTitle
    }
}