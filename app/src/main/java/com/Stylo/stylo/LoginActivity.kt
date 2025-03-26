package com.Stylo.stylo

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)

        // Show loading indicator and disable button
       // binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        ApiClient.apiService.loginUser(request).enqueue(object : Callback<LocalResponse> {
            override fun onResponse(call: Call<LocalResponse>, response: Response<LocalResponse>) {
                // Hide loading indicator and enable button
             //   binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, Bottom_Navigatio_Activity::class.java)
                        startActivity(intent)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Login failed"
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LocalResponse >, t: Throwable) {
                // Hide loading indicator and enable button
              //  binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Login error", "${t.message}")
            }
        })
    }
}
