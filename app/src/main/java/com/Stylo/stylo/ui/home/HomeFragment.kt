package com.Stylo.stylo.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.Stylo.stylo.R
import com.Stylo.stylo.adapters.ProductAdapter
import com.Stylo.stylo.data.model.Product
import com.Stylo.stylo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productList = listOf(
            Product("Regular Fit Polo", "$1,190", R.drawable.cart, false),
            Product("Casual Shirt", "$900", R.drawable.home, true),
            Product("Denim Jeans", "$1,500", R.drawable.logos_google_icon, false)
        )

        Log.d("HomeFragment", "Product List Size: ${productList.size}")

        // Initialize adapter
        productAdapter = ProductAdapter(productList)

        // Set up RecyclerView with GridLayoutManager
        binding.productGrid.apply {
            layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
            adapter = productAdapter
            visibility = View.VISIBLE // Ensure RecyclerView is visible
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
