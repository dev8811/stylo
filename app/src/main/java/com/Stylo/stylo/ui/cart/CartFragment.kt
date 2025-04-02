package com.Stylo.stylo.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.Stylo.stylo.R
import com.Stylo.stylo.adapters.ProductAdapter
import com.Stylo.stylo.data.model.Product
import com.Stylo.stylo.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productList = listOf(
            Product("Regular Fit Polo", "$1,190", R.drawable.cart, false),
            Product("Casual Shirt", "$900", R.drawable.home, false),
            Product("Denim Jeans", "$1,500", R.drawable.logos_google_icon, false)
        )

        productAdapter = ProductAdapter(productList)
        binding.recyclerView.adapter = productAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
