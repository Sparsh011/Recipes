package com.example.recipes.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipes.application.FavDishApplication
import com.example.recipes.databinding.FragmentFavoriteDishesBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.view.activities.MainActivity
import com.example.recipes.view.adapters.FavDishAdapter
import com.example.recipes.viewmodel.FavDishViewModel
import com.example.recipes.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var _binding: FragmentFavoriteDishesBinding? = null
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner){ dishes ->
            dishes.let {
                _binding!!.rvFavoriteDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
                val adapter = FavDishAdapter(this)
                _binding!!.rvFavoriteDishesList.adapter = adapter

                if (it.isNotEmpty()){
                    _binding!!.rvFavoriteDishesList.visibility = View.VISIBLE
                    _binding!!.tvNoFavoriteDishesAvailable.visibility = View.GONE
                    adapter.dishesList(it)
                }
                else{
                    _binding!!.rvFavoriteDishesList.visibility = View.GONE
                    _binding!!.tvNoFavoriteDishesAvailable.visibility = View.VISIBLE
                }
            }
        }
    }

    fun dishDetails(favDish: FavDish){
//        As we have used argument tag in mobile_navigation in DishDetailFragment, we don't have to pass any data
        findNavController().navigate(FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(favDish))
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.hideBottomNavigation()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavigation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}