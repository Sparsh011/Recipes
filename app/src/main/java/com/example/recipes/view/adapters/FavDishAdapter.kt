package com.example.recipes.view.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipes.R
import com.example.recipes.databinding.ItemDishLayoutBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.utils.Constants
import com.example.recipes.view.activities.AddUpdateDish
import com.example.recipes.view.fragments.AllDishesFragment
import com.example.recipes.view.fragments.FavoriteDishesFragment

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
            if (fragment is FavoriteDishesFragment){
                fragment.dishDetails(dish)
            }
        }

        holder.ibMore.setOnClickListener{
            val popup = PopupMenu(fragment.context, holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)

            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_edit_dish){
                    val intent = Intent(fragment.requireActivity(), AddUpdateDish::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                    fragment.requireActivity().startActivity(intent)
                }
                else if (it.itemId === R.id.action_delete_dish){
//                    Log.i("Clicked on ", "Delete")
                    if (fragment is AllDishesFragment){
                        fragment.deleteDish(dish)
                    }
                }
                true
            }
            popup.show()

            if (fragment is AllDishesFragment){
                holder.ibMore.visibility = View.VISIBLE
            }
            else if (fragment is FavoriteDishesFragment){
                holder.ibMore.visibility = View.INVISIBLE
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
        val ibMore = view.ibMore
    }
}