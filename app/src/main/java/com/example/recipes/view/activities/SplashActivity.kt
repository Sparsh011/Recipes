package com.example.recipes.view.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipes.R
import com.example.recipes.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val splashBinding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(splashBinding.root) // Now we don't need setContentView(R.layout.activity_splash) because using ViewBinding, we will be inflating our layout

        splashBinding.imageView.setOnClickListener{
            Toast.makeText(this, "Image Clicked", Toast.LENGTH_SHORT).show()
        }
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = 1000
        val currentProgress = 1000
        progressBar.progressTintList = ColorStateList.valueOf(Color.BLUE)
        ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
            .setDuration(2000)
            .start()

//        Redirecting to MainActivity after splash screen is finished.
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 2100)

    }
}