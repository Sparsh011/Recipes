package com.example.recipes.view.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recipes.R
import com.example.recipes.application.FavDishApplication
import com.example.recipes.databinding.FragmentDishDetailBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.utils.Constants
import com.example.recipes.viewmodel.FavDishViewModel
import com.example.recipes.viewmodel.FavDishViewModelFactory
import java.io.IOException
import java.util.*

class DishDetailFragment : Fragment() {
    private var mBinding: FragmentDishDetailBinding? = null
    private var mFavDishDetails: FavDish? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentDishDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args : DishDetailFragmentArgs by navArgs()
        mFavDishDetails = args.dishDetails

        args.let { it ->
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .into(mBinding!!.ivDishImage)
            } catch (e: IOException){
                e.printStackTrace()
            }

            mBinding!!.tvTitle.text = it.dishDetails.title
            mBinding!!.tvType.text = it.dishDetails.type.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }
            mBinding!!.tvCategory.text = resources.getString(R.string.category, it.dishDetails.category)
            mBinding!!.tvIngredients.text = it.dishDetails.ingredients

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                mBinding!!.tvCookingDirection.text = Html.fromHtml(
                    it.dishDetails.directionToCook,
                    Html.FROM_HTML_MODE_COMPACT
                )
            }
            else{
                @Suppress("DEPRECATION")
                mBinding!!.tvCookingDirection.text = Html.fromHtml(it.dishDetails.directionToCook).toString()
            }
            mBinding!!.tvCookingTime.text = resources.getString(R.string.approximate_cooking_time, it.dishDetails.cookingTime)
            if (args.dishDetails.favouriteDish){
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite_selected))
            }
            else{
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite_unselected))
            }
        }

        mBinding!!.ivFavoriteDish.setOnClickListener{
            args.dishDetails.favouriteDish = !args.dishDetails.favouriteDish
            mFavDishViewModel.update(args.dishDetails)
            if (args.dishDetails.favouriteDish){
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite_selected))
                Toast.makeText(requireActivity(), "Added To Favorites!", Toast.LENGTH_SHORT).show()
            }
            else{
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite_unselected))
                Toast.makeText(requireActivity(), "Removed From Favorites!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_share_dish -> {

                val type = "text/plain"
                val subject = "Checkout this dish recipe"
                var extraText = ""
                val shareWith = "Share with"

                mFavDishDetails?.let { favDish ->

                    var image = ""

                    if (favDish.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE) {
                        image = favDish.image
                    }

                    var cookingInstructions = ""

//                    Cooking instructions are present in <li> types, so converting from HTML to string -
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        cookingInstructions = Html.fromHtml(
                            favDish.directionToCook,
                            Html.FROM_HTML_MODE_COMPACT
                        ).toString()
                    } else {
                        @Suppress("DEPRECATION")
                        cookingInstructions = Html.fromHtml(favDish.directionToCook).toString()
                    }

                    extraText =
                        "$image \n" +
                                "\n Title:  ${favDish.title} \n\n Type: ${favDish.type} \n\n Category: ${favDish.category}" +
                                "\n\n Ingredients: \n ${favDish.ingredients} \n\n Instructions To Cook: \n $cookingInstructions" +
                                "\n\n Time required to cook the dish approx ${favDish.cookingTime} minutes."
                }


                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}