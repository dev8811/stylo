package com.Stylo.stylo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.Stylo.stylo.RetrofitApi.ApiClient
import com.Stylo.stylo.RetrofitApi.FetchProduct
import com.Stylo.stylo.RetrofitApi.Product
import com.Stylo.stylo.adapter.CategoryAdapter
import com.Stylo.stylo.adapter.ProductAdapter
import com.Stylo.stylo.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapter: ProductAdapter? = null
    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupCategoryRecyclerView()
        setupProductRecyclerView()

        return binding.root
    }

    private fun setupCategoryRecyclerView() {
        val categoryList = listOf("All", "Tshirts", "Jeans", "Shoes", "Jackets")
        val categoryAdapter = CategoryAdapter(categoryList)

        binding.categoryTabs.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryTabs.adapter = categoryAdapter
    }

    private fun setupProductRecyclerView() {
        binding.productGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productGrid.setHasFixedSize(true)

        adapter = ProductAdapter(productList)
        binding.productGrid.adapter = adapter

        fetchProducts()
    }

    private fun fetchProducts() {
        ApiClient.apiService.getProducts().enqueue(object : Callback<FetchProduct> {
            override fun onResponse(call: Call<FetchProduct>, response: Response<FetchProduct>) {
                if (response.isSuccessful) {
                    response.body()?.let { fetchedData ->
                        productList.clear()
                        productList.addAll(fetchedData.products)
                        adapter?.notifyDataSetChanged()
                    } ?: showToast("No products found")
                } else {
                    showToast("Failed to load products: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FetchProduct>, t: Throwable) {
                showToast("Error fetching products: ${t.localizedMessage}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null // Prevent memory leaks
    }
}


