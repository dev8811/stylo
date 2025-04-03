package com.Stylo.stylo.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.Stylo.stylo.LoginActivity
import com.Stylo.stylo.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!  // ViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve user data from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "N/A")
        val email = sharedPreferences.getString("userEmail", "N/A")
        val firstName = sharedPreferences.getString("firstName", "N/A")
        val lastName = sharedPreferences.getString("lastName", "N/A")

        // Set data to TextViews
        binding.tvUserId.text = "User ID: $userId"
        binding.tvUserEmail.text = "Email: $email"
        binding.tvFirstName.text = "First Name: $firstName"
        binding.tvLastName.text = "Last Name: $lastName"

        // Logout Button Click
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()  // Clear all saved data
            apply()
        }

        // Redirect to LoginActivity and clear back stack
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
