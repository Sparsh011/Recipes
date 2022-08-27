package com.example.recipes.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.recipes.R
import com.example.recipes.application.FavDishApplication
import com.example.recipes.databinding.ActivityAddUpdateDishBinding
import com.example.recipes.databinding.DialogCustomImageSelectionBinding
import com.example.recipes.databinding.DialogCustomListBinding
import com.example.recipes.model.entities.FavDish
import com.example.recipes.utils.Constants
import com.example.recipes.view.adapters.CustomListItemAdapter
import com.example.recipes.viewmodel.FavDishViewModel
import com.example.recipes.viewmodel.FavDishViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AddUpdateDish : AppCompatActivity(), View.OnClickListener {
    private var mImagePath: String = ""
    private lateinit var mCustomListDialog: Dialog
    private lateinit var mBinding: ActivityAddUpdateDishBinding

//    Creating our ViewModel object
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

//        Adding back button in the toolbar -
        setupActionBar()

//        Setting onClickListener to the add image icon
        mBinding.ivAddDishImage.setOnClickListener(this)

//        For selecting the image of the dish, we have created a dialog box (on which there will be 2 options - gallery and camera) that appears when we click on ivAddDishImage icon. For creating that box, we have created a layout file, in which we have designed that box.

        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    //    This override method is to implement the onClickListener. We could have written the code in the onCreate method also but this is done to make the code cleaner.
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_dish_image -> {
                    customImageSelectionDialog()
                    return
                }

                R.id.et_type ->{
                    customItemListDialog("Select Dish Type", Constants.dishTypes(), Constants.DISH_TYPE)
                    return
                }

                R.id.et_category -> {
                    customItemListDialog("Select Category", Constants.dishCategories(), Constants.DISH_CATEGORY)
                    return
                }

                R.id.et_cooking_time -> {
                    customItemListDialog("Select Cooking Time", Constants.dishCookTime(), Constants.DISH_COOKING_TIME)
                    return
                }

                R.id.btn_add_dish -> {
                    val title =  mBinding.etTitle.text.toString().trim{it <= ' '} // trimming empty spaces
                    val type = mBinding.etType.text.toString().trim{it <= ' '}
                    val category = mBinding.etCategory.text.toString().trim{it <= ' '}
                    val ingredients = mBinding.etIngredients.text.toString().trim{it <= ' '}
                    val cookingTimeInMinutes = mBinding.etCookingTime.text.toString().trim{it <= ' '}
                    val cookingDirection = mBinding.etDirectionToCook.text.toString().trim{it <= ' '}
                    
                    when{
                        TextUtils.isEmpty(mImagePath) -> {
                            Toast.makeText(this@AddUpdateDish, "Image cannot be blank", Toast.LENGTH_SHORT).show()
                        }

                        TextUtils.isEmpty(title) -> {
                            Toast.makeText(this@AddUpdateDish, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(type) -> {
                            Toast.makeText(this@AddUpdateDish, "Please select the type of Dish", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(category) -> {
                            Toast.makeText(this@AddUpdateDish, "Please select the category of the Dish", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(ingredients) -> {
                            Toast.makeText(this@AddUpdateDish, "Ingredients cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingTimeInMinutes) -> {
                            Toast.makeText(this@AddUpdateDish, "Please select the cooking time", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingDirection) -> {
                            Toast.makeText(this@AddUpdateDish, "Cooking Direction cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val favDishDetails: FavDish = FavDish(
                                mImagePath,
                                Constants.DISH_IMAGE_SOURCE_LOCAL,
                                title,
                                type,
                                category,
                                ingredients,
                                cookingTimeInMinutes,
                                cookingDirection,
                                false
                            )
                            mFavDishViewModel.insert(favDishDetails)
                            Toast.makeText(this@AddUpdateDish, "Added Dish to DB", Toast.LENGTH_SHORT).show()
                            Log.e("Insertion", "Success")
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun customImageSelectionDialog() {
//        Setting up a new binding object. This new binding object will bind the dialog_custom_image_selection.xml file.
        val dialog = Dialog(this@AddUpdateDish)
        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startForResultToLoadImage.launch(intent)
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permission: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()

            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        imageChooser()
                    }
                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(this@AddUpdateDish, "You have denied the storage permission. Enable it to select image from storage.", Toast.LENGTH_SHORT).show()
                    }
                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread().check()

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("Looks like you have not allowed the required permissions. You have to enable them to use this feature.") // This will be the message of the dialog box. Two options will be shown in the dialog box and they are - Go to settings and Cancel.
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
//                    Opening the Recipe app's settings in phone.
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
//                Dismissing the dialog and returning to the AppUpdateDish ui
                dialog.dismiss()
            }.show()
    }

    private fun imageChooser() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        launchSomeActivity.launch(i)
    }

    private var launchSomeActivity = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {

//            Note - There are two ways to load image using gallery and camera. (This one is for Gallery). 1. We can either use Glide. 2.Or we can use the code written below. Output of both the codes is SAME.
            val data = result.data
            if (data != null && data.data != null) {
                val selectedImageUri = data.data
//                var selectedImageBitmap: Bitmap? = null
//                try {
//                    selectedImageBitmap = if (Build.VERSION.SDK_INT < 28) {
//                        MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri) // Content Resolver is used to share data among different parts in an application.
//                    } else {
//                        val source = selectedImageUri?.let {
//                            ImageDecoder.createSource(this.contentResolver, it) //ImageDecoder is a class for converting encoded images (like PNG, JPEG, WEBP, GIF, or HEIF) into Drawable or Bitmap objects.
//                        }
//                        source?.let {
//                            ImageDecoder.decodeBitmap(it)
//                        }
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//                mBinding.ivDishImage.setImageBitmap(selectedImageBitmap)
                Glide.with(this)
                    .load(selectedImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(object : RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("TAG", "Error loading image", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.let {
                                val bitmap: Bitmap = resource.toBitmap()
                                mImagePath = saveImageToInternalStorage(bitmap)
                                Log.i("ImagePath", mImagePath)
                            }
                            return false
                        }
                    })
                    .into(mBinding.ivDishImage)
                mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.change_image))
            }
        }
    }

    private val startForResultToLoadImage =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //            Note - There are two ways to load image using gallery and camera. (This one is for Camera). 1. We can either use Glide. 2.Or we can use the code written below. Output of both the codes is SAME.
                try {
                    val selectedImage: Uri? = result.data?.data
                    if (selectedImage != null) {
                        mBinding.ivDishImage.setImageURI(selectedImage)

                    } else {
                        // From Camera code goes here.
                        // Get the bitmap directly from camera
                        result.data?.extras?.let {
                            val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap
                            mBinding.ivDishImage.setImageBitmap(bitmap)

//                            Glide.with(this)
//                                .load(bitmap)
//                                .into(mBinding.ivDishImage)

                            mImagePath = saveImageToInternalStorage(bitmap)
                            Log.i("ImagePath", mImagePath)

                            mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.change_image))
                        }
                    }
                } catch (error: Exception) {
                    Log.d("log==>>", "Error : ${error.localizedMessage}")
                }
            }
        }

    private fun saveImageToInternalStorage(bitmap: Bitmap) : String{
        val wrapper = ContextWrapper(applicationContext) // This wrapper will tell which application wants to save image to gallery.

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE) //the default mode, where the created file can only be accessed by the calling application (or all applications sharing the same user ID).

        file = File(file, "${UUID.randomUUID()}.jpg")

//        creating bitmap of the image -
        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
//            Note - early all stream instances do not actually need to be closed after use. Generally, only streams whose source is an IO channel (such as those returned by Files.lines(Path, Charset)) will require closing. Closing the stream for resource management.
        }catch (e: IOException){
            e.printStackTrace()
        }

        return file.absolutePath // Returns the directory where the file exists as well as the name of the file.
    }

    private fun customItemListDialog(title: String, itemList: List<String>, selection: String){
//        Setting up a dialog for time to cook, dish type, etc.
        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomListItemAdapter(this, itemList, selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()

    }

    fun selectedListItem(item: String, selection: String) {

        when (selection) {

            Constants.DISH_TYPE -> {
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }

            Constants.DISH_CATEGORY -> {
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            else -> {
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }
    companion object {
        private const val IMAGE_DIRECTORY = "RecipeImage" // This is the folder where the image of the dish will be stored inside gallery.
    }
}
