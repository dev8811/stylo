package com.Stylo.stylo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.Stylo.stylo.RetrofitApi.ApiClient
import com.Stylo.stylo.RetrofitApi.LocalResponse
import com.Stylo.stylo.RetrofitApi.SignupRequest
import com.Stylo.stylo.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signupUser(name, email, password)
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signupUser(name: String, email: String, password: String) {
        val request = SignupRequest(name, email, password)

        ApiClient.apiService.signupUser(request).enqueue(object : Callback<LocalResponse> {
            override fun onResponse(call: Call<LocalResponse>, response: Response<LocalResponse>) {
                if (response.isSuccessful) {
                    val signupResponse = response.body()
                    if (signupResponse != null && signupResponse.status) {
                        Toast.makeText(
                            this@SignupActivity,
                            "Signup Successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish() // Go back to login screen
                    } else {
                        Toast.makeText(
                            this@SignupActivity,
                            signupResponse?.message ?: "Signup Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        "Error: ${response.errorBody()?.string()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LocalResponse>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e("Signup Error", t.message ?: "Unknown error")
            }
        })
    }
}
