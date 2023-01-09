package com.example.recipes.view.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.example.recipes.R
import com.example.recipes.databinding.ActivityMainBinding
import com.example.recipes.model.notification.NotifyWorker
import com.example.recipes.utils.Constants
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mNavController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mNavController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_all_dishes, R.id.navigation_favorite_dishes, R.id.navigation_random_dish
            )
        )
        setupActionBarWithNavController(mNavController, appBarConfiguration)
        binding.navView.setupWithNavController(mNavController)

        if(intent.hasExtra(Constants.NOTIFICATION_ID)){
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            binding.navView.selectedItemId = R.id.navigation_random_dish
        }

        startWork()
    }


//    Override this method to use the back button in DishDetailFragment
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }

    fun hideBottomNavigation(){
//        Hiding the bottom navigation using animation -
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(binding.navView.height.toFloat()).duration = 300
        binding.navView.visibility = View.GONE
    }
    fun showBottomNavigation(){
//        Hiding the bottom navigation using animation -
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(0f).duration = 300
        binding.navView.visibility = View.VISIBLE
    }

    private fun startWork(){
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("Recipes Notify Work", //allows you to enqueue a uniquely-named PeriodicWorkRequest
            ExistingPeriodicWorkPolicy.KEEP, // If a worker already exists, a new one won't be created in .KEEP. In case of .REPLACE, the old worker is replaced with a new one
            createWorkRequest())
    }

    private fun createWorkRequest() = PeriodicWorkRequestBuilder<NotifyWorker>(15, TimeUnit.MINUTES)
        .setConstraints(createConstraints())
        .build()

    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(true)
        .build()

}