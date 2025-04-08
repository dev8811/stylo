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

    // Track if fragment is active to prevent callbacks after destruction
    private var isFragmentActive = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        isFragmentActive = true
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        if (!isFragmentActive || _binding == null) return

        try {
            // Retrieve user data from SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("userId", "N/A")
            val email = sharedPreferences.getString("userEmail", "N/A")
            val firstName = sharedPreferences.getString("firstName", "N/A")
            val lastName = sharedPreferences.getString("lastName", "N/A")

            // Set data to TextViews with null safety
            _binding?.let { binding ->
                binding.tvUserId.text = "User ID: $userId"
                binding.tvUserEmail.text = "Email: $email"
                binding.tvFirstName.text = "First Name: $firstName"
                binding.tvLastName.text = "Last Name: $lastName"
            }
        } catch (e: Exception) {
            // Handle exceptions that might occur if fragment is being destroyed
            // or activity is no longer available
        }
    }

    private fun setupClickListeners() {
        // Logout Button Click with null safety
        _binding?.btnLogout?.setOnClickListener {
            if (isFragmentActive) {
                logoutUser()
            }
        }
    }

    private fun logoutUser() {
        // Check if fragment is still attached to activity
        if (!isAdded || !isFragmentActive) return

        try {
            val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                clear()  // Clear all saved data
                apply()
            }

            // Redirect to LoginActivity and clear back stack
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } catch (e: Exception) {
            // Handle any exceptions that might occur if the fragment
            // is detached during the logout process
        }
    }

    override fun onDestroyView() {
        // Set fragment inactive flag to prevent callback processing
        isFragmentActive = false

        // Clear binding
        _binding = null

        super.onDestroyView()
    }
}