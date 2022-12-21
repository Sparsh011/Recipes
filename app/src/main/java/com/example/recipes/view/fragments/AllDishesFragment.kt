package com.example.recipes.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.R
import com.example.recipes.application.FavDishApplication
import com.example.recipes.databinding.DialogCustomListBinding
import com.example.recipes.databinding.FragmentAllDishesBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.utils.Constants
import com.example.recipes.view.activities.AddUpdateDish
import com.example.recipes.view.activities.MainActivity
import com.example.recipes.view.activities.SearchRecipeFromAPI
import com.example.recipes.view.adapters.CustomListItemAdapter
import com.example.recipes.view.adapters.FavDishAdapter
import com.example.recipes.viewmodel.FavDishViewModel
import com.example.recipes.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding
    private lateinit var mFavDishAdapter: FavDishAdapter
    private lateinit var mCustomListDialog: Dialog
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)
        mBinding.rvDishesList.adapter = mFavDishAdapter

//        Observing allDishes live data -
        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if (it.isNotEmpty()){
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE

                    mFavDishAdapter.dishesList(it)
                }
                else{
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }


        mBinding.btnSearchRecipe.setOnClickListener{
            val intentForSearchRecipe = Intent(activity, SearchRecipeFromAPI::class.java)
            startActivity(intentForSearchRecipe)
        }



        mBinding.etSearchSavedRecipes.addTextChangedListener{ editable ->

                if (isAdded){
                    if (editable.toString().isNotEmpty()) {
                        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){ dishes ->
                            val resultList = ArrayList<FavDish>()

                            for (recipe in dishes){
                                if (recipe.title.lowercase().startsWith(editable.toString().lowercase())){
                                    resultList.add(recipe)
                                }
                            }

                            resultList.let { result ->
                                if (result.isNotEmpty()) {
                                    mBinding.rvDishesList.visibility = View.VISIBLE
                                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                                    mFavDishAdapter.dishesList(result)
                                } else {
                                    mBinding.rvDishesList.visibility = View.GONE
                                    mBinding.tvNoDishesAddedYet.text = "No Dish Found!"
                                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
                                dishes.let {
                                    if (it.isNotEmpty()) {
                                        mBinding.rvDishesList.visibility = View.VISIBLE
                                        mBinding.tvNoDishesAddedYet.visibility = View.GONE
                                        mFavDishAdapter.dishesList(it)
                                    } else {
                                        mBinding.rvDishesList.visibility = View.GONE
                                        mBinding.tvNoDishesAddedYet.text = "No Dish Added Yet!"
                                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                                    }
                                }


                        }
                    }
                }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDish::class.java))
                return true
            }

            R.id.action_filter_dishes ->{
                filterDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun dishDetails(favDish: FavDish){
        findNavController().navigate(AllDishesFragmentDirections.actionAllDishesToDishDetails(favDish))

        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigation()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.showBottomNavigation()
        }
    }

    fun deleteDish(dish: FavDish){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish_title))
        builder.setMessage(resources.getString(R.string.title_delete_dish))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.yes_delete)){dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            Toast.makeText(requireActivity(), dish.title + " deleted", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.lbl_no)){dialogInterface, _ ->
            Toast.makeText(requireActivity(), "Unable to delete" + dish.title, Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog(){
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_filter_option)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(requireActivity(), this@AllDishesFragment, dishTypes, Constants.FILTER_SELECTION)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    fun filterSelection(filterItemSelection: String){
        mCustomListDialog.dismiss()
        Toast.makeText(requireActivity(), "Filtered by $filterItemSelection", Toast.LENGTH_SHORT).show()
        Log.i("Filter Selection", filterItemSelection)

        if (filterItemSelection == Constants.ALL_ITEMS){
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){ dishes ->
                dishes.let {
                    if (it.isNotEmpty()){
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    }
                    else{
                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.text = "No Dish Added Yet!"
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        }
        else{
            mFavDishViewModel.getFilteredList(filterItemSelection).observe(viewLifecycleOwner){ dishes->
                dishes.let {
                    if (it.isNotEmpty()){
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    }
                    else{
                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}