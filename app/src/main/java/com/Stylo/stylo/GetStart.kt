package com.Stylo.stylo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.Stylo.stylo.databinding.ActivityGetStartBinding

class GetStart : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityGetStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listener with transition animation
        binding.getStartedButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)


            // Transition animation
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(binding.getStartedButton, "buttonTransition") // Shared element transition
            )

            startActivity(intent, options.toBundle())
        }
    }
}
