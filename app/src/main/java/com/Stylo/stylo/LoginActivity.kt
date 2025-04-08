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
import org.json.JSONObject
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
                        Toast.makeText(this@LoginActivity, loginResponse?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showDetailedError(response)
                }
            }

            override fun onFailure(call: Call<LocalResponse>, t: Throwable) {
                binding.btnLogin.isEnabled = true
                Log.e("LoginError", "API call failed: ${t.message}", t)

                // Show more user-friendly network error message
                val errorMessage = when {
                    t.message?.contains("timeout", ignoreCase = true) == true ->
                        "Connection timed out. Please check your internet connection and try again."
                    t.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                        "No internet connection. Please check your network settings and try again."
                    else -> "Network error: Unable to connect to server. Please try again later."
                }

                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showDetailedError(response: Response<LocalResponse>) {
        try {
            val errorBody = response.errorBody()?.string()
            val errorMessage = when (response.code()) {
                400 -> parseErrorMessage(errorBody) ?: "Invalid request. Please check your credentials."
                401 -> "Incorrect email or password. Please try again."
                403 -> "Access denied. Your account may be locked or suspended."
                404 -> "Login service not found. Please try again later."
                429 -> "Too many login attempts. Please try again later."
                500, 502, 503, 504 -> "Server error. Please try again later."
                else -> parseErrorMessage(errorBody) ?: "Login failed with error code: ${response.code()}"
            }

            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
            Log.e("LoginError", "Error response: $errorBody")
        } catch (e: Exception) {
            Toast.makeText(this@LoginActivity, "An unexpected error occurred. Please try again.", Toast.LENGTH_LONG).show()
            Log.e("LoginError", "Error parsing response: ${e.message}", e)
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            errorBody?.let {
                val jsonObject = JSONObject(it)
                when {
                    jsonObject.has("message") -> jsonObject.getString("message")
                    jsonObject.has("error") -> jsonObject.getString("error")
                    else -> null
                }
            }
        } catch (e: Exception) {
            Log.e("LoginError", "Error parsing JSON: ${e.message}", e)
            null
        }
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