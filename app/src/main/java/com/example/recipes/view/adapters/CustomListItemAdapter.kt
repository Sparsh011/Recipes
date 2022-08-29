package com.example.recipes.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.databinding.ItemCustomListBinding
import com.example.recipes.view.activities.AddUpdateDish


class CustomListItemAdapter(
    private val activity: Activity,
    private val listItems: List<String>,
    private val selection: String
) :
    RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCustomListBinding =
            ItemCustomListBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listItems[position]

        holder.tvText.text = item

        // TODO Step 4: Define the ItemView click event and send the result to the base class.
        // START
        holder.itemView.setOnClickListener {

            if (activity is AddUpdateDish) {
                activity.selectedListItem(item, selection)
            }
        }
        // END
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return listItems.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: ItemCustomListBinding) : RecyclerView.ViewHolder(view.root) {
        // Holds the TextView that will add each item to
        val tvText = view.tvText
    }
}