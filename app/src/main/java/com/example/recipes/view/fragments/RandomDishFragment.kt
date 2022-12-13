package com.example.recipes.view.fragments

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.recipes.R
import com.example.recipes.application.FavDishApplication
import com.example.recipes.databinding.FragmentRandomDishBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.model.entities.RandomDish
import com.example.recipes.utils.Constants
import com.example.recipes.viewmodel.FavDishViewModel
import com.example.recipes.viewmodel.FavDishViewModelFactory
import com.example.recipes.viewmodel.RandomDishViewModel

@Suppress("DEPRECATION")
class RandomDishFragment : Fragment() {
    private var mBinding: FragmentRandomDishBinding? = null
    private lateinit var mRandomDishViewModel: RandomDishViewModel
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       mBinding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this)[RandomDishViewModel::class.java]

        mRandomDishViewModel.getRandomRecipeFromApi()
        randomDishViewModelObserver()

        mBinding!!.srlRandomDish.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromApi()
        }
    }

    private fun randomDishViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner) { randomDishResponse ->
            randomDishResponse?.let {
                Log.i("Random Dish response", "${randomDishResponse.recipes[0]}")
                setRandomDishResponseInUI(randomDishResponse.recipes[0])
                if (mBinding!!.srlRandomDish.isRefreshing){
                    mBinding!!.srlRandomDish.isRefreshing = false
                }
            }
        }

        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner){ dataError ->
            dataError?.let {
                Log.i("Random Dish API error", "$dataError")
                if (mBinding!!.srlRandomDish.isRefreshing){
                    mBinding!!.srlRandomDish.isRefreshing = false
                }
            }
        }

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner){ loadRandomDish ->
            loadRandomDish?.let {
                Log.i("Random Dish Loading", "$loadRandomDish")

                if (loadRandomDish && !mBinding!!.srlRandomDish.isRefreshing){
                    showCustomProgressDialog()
                }
                else{
                    hideProgressDialog()
                }
            }
        }
    }

    private fun showCustomProgressDialog(){
        mProgressDialog = Dialog(requireActivity())

        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideProgressDialog(){
        mProgressDialog?.let {
            it.dismiss()
        }
    }

    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe){
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(mBinding!!.ivDishImage)

        mBinding!!.tvTitle.text = recipe.title

        var dishType = "Other"
        if (recipe.dishTypes.isNotEmpty()){
            dishType = recipe.dishTypes[0]
            mBinding!!.tvType.text = dishType
        }

        mBinding!!.tvCategory.text = resources.getString(R.string.category, "Other")
        var ingredients = ""
        for (value in recipe.extendedIngredients){
            if (ingredients.isEmpty()){
                ingredients = value.original
            }
            else{
                ingredients = ingredients + ", \n" + value.original
            }
        }
        mBinding!!.tvIngredients.text = ingredients
//        Instructions are present in HTML format (ordered list), so converting that html -
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){ // For newer versions that android N
            mBinding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        }
        else{
            mBinding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )

        var addedToFavorites = false

        mBinding!!.tvCookingTime.text = resources.getString(R.string.approximate_cooking_time, recipe.readyInMinutes.toString())

        mBinding!!.ivFavoriteDish.setOnClickListener{
            if (addedToFavorites){
                Toast.makeText(requireActivity(), "Already Added To Favorites!", Toast.LENGTH_SHORT).show()
            }
            else{
                val randomDishDetails = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )
                val mFavDishViewModel: FavDishViewModel by viewModels{
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }

                mFavDishViewModel.insert(randomDishDetails)
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )

                Toast.makeText(requireActivity(), "Added To Favorites!", Toast.LENGTH_SHORT).show()
                addedToFavorites = true
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}