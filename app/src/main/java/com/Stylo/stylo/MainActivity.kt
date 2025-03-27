package com.Stylo.stylo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            navigateToHome()
            return
        }
        // Get references to views
        val background = findViewById<ImageView>(R.id.bg_vector)
        val logo = findViewById<ImageView>(R.id.logo)
        val mainLayout = findViewById<ConstraintLayout>(R.id.main)

        // Fade-in Animation
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 2500 // 1.5 seconds fade-in
            fillAfter = true
        }

        // Apply animation to views
        background.startAnimation(fadeIn)
        logo.startAnimation(fadeIn)
      //  mainLayout.startAnimation(fadeIn)

        // Navigate to next screen after delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, GetStart::class.java))
            finish() // Finish Splash Screen Activity
        }, 3000) // 2-second delay
    }
    // Function to check login state
    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    // Function to navigate to home screen
    private fun navigateToHome() {
        val intent = Intent(this, Bottom_Navigatio_Activity::class.java)
        startActivity(intent)
        finish() // Finish login activity to prevent going back
    }
}
