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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webClientId = "909263226608-ec6h6ledcorpp4bt2ivt58m84g20vhu6.apps.googleusercontent.com"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)  // Request ID token from Google
            .requestEmail() // Request email
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleSignUp.setOnClickListener {
            signInWithGoogle()
        }

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

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                handleSignInSuccess(account)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google sign-in failed: ${e.statusCode}", e)
                Toast.makeText(this, "Google Sign-In failed! Error code: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Google Sign-In failed: No data received!", Toast.LENGTH_LONG).show()
        }}


    private fun handleSignInSuccess(account: GoogleSignInAccount) {
        val email = account.email
        val displayName = account.displayName
        val profilePic = account.photoUrl.toString()

        Log.d("GoogleSignIn", "Email: $email")
        Log.d("GoogleSignIn", "Name: $displayName")
        Log.d("GoogleSignIn", "Profile Picture: $profilePic")

        Toast.makeText(this, "Signed in as $displayName", Toast.LENGTH_LONG).show()

        // ✅ Navigate to HomeActivity or process the Google sign-in result
        val intent = Intent(this, Bottom_Navigatio_Activity::class.java).apply {
            putExtra("USER_NAME", displayName)
            putExtra("USER_EMAIL", email)
            putExtra("USER_PROFILE", profilePic)
        }
        startActivity(intent)
        finish()
    }
}


/*

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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webClientId = "909263226608-ec6h6ledcorpp4bt2ivt58m84g20vhu6.apps.googleusercontent.com"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)  // Request ID token from Google
            .requestEmail() // Request email
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleSignUp.setOnClickListener {
            signInWithGoogle()
        }

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

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                handleSignInSuccess(account)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google sign-in failed: ${e.statusCode}", e)
                Toast.makeText(this, "Google Sign-In failed! Error code: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Google Sign-In failed: No data received!", Toast.LENGTH_LONG).show()
        }}


    private fun handleSignInSuccess(account: GoogleSignInAccount) {
        val email = account.email
        val displayName = account.displayName
        val profilePic = account.photoUrl.toString()

        Log.d("GoogleSignIn", "Email: $email")
        Log.d("GoogleSignIn", "Name: $displayName")
        Log.d("GoogleSignIn", "Profile Picture: $profilePic")

        Toast.makeText(this, "Signed in as $displayName", Toast.LENGTH_LONG).show()

        // ✅ Navigate to HomeActivity or process the Google sign-in result
        val intent = Intent(this, Bottom_Navigatio_Activity::class.java).apply {
            putExtra("USER_NAME", displayName)
            putExtra("USER_EMAIL", email)
            putExtra("USER_PROFILE", profilePic)
        }
        startActivity(intent)
        finish()
    }
}

*/