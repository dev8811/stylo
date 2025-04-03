package com.Stylo.stylo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.Stylo.stylo.RetrofitApi.ApiClient
import com.Stylo.stylo.RetrofitApi.LoginRequest
import com.Stylo.stylo.RetrofitApi.LocalResponse
import com.Stylo.stylo.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)
        binding.btnLogin.isEnabled = false // Disable button to prevent multiple clicks

        ApiClient.apiService.loginUser(request).enqueue(object : Callback<LocalResponse> {
            override fun onResponse(call: Call<LocalResponse>, response: Response<LocalResponse>) {
                binding.btnLogin.isEnabled = true

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status) {
                        Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()

                        // Extract user details from response
                        val userId = loginResponse.user?.userId ?: ""
                        val email = loginResponse.user?.email ?: ""
                        val firstName = loginResponse.user?.firstName ?: ""
                        val lastName = loginResponse.user?.lastName ?: ""

                        // Save user data in SharedPreferences
                        saveUserLoginState(userId, email, firstName, lastName)

                        navigateToHome()
                    } else {
                        showError(response)
                    }
                } else {
                    showError(response)
                }
            }

            override fun onFailure(call: Call<LocalResponse>, t: Throwable) {
                binding.btnLogin.isEnabled = true
                Log.e("LoginError", "API call failed: ${t.message}", t)
                Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showError(response: Response<LocalResponse>) {
        val errorMessage = response.errorBody()?.string() ?: "Login failed. Please try again."
        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
        Log.e("LoginError", "Error response: $errorMessage")
    }

    private fun saveUserLoginState(userId: String, email: String, firstName: String, lastName: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", true)
            putString("userId", userId)
            putString("userEmail", email)
            putString("firstName", firstName)
            putString("lastName", lastName)
            apply()
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, Bottom_Navigatio_Activity::class.java))
        finish()
    }
}
