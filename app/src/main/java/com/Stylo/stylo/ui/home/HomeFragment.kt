package com.Stylo.stylo.ui.home


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.Stylo.stylo.RetrofitApi.ApiClient
import com.Stylo.stylo.RetrofitApi.FatchProduct
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

    private val categories = listOf("All", "Tshirts", "Jeans", "Shoes", "Jackets")
    private val categoryIds = listOf("0", "1", "2", "3", "4") // Replace with actual category IDs
    private var selectedCategoryId = "0" // Default category (All)

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
        val categoryAdapter = CategoryAdapter(categories) { position ->
            selectedCategoryId = categoryIds[position] // Set selected category ID
            FatchProducts(selectedCategoryId)
        }

        binding.categoryTabs.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryTabs.adapter = categoryAdapter
    }

    private fun setupProductRecyclerView() {
        binding.productGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productGrid.setHasFixedSize(true)

        adapter = ProductAdapter(productList) { product ->
            val intent = Intent(requireContext(), Product_detail_Activity::class.java).apply {
                putExtra("PRODUCT", product)
            }
            startActivity(intent)
        }
        binding.productGrid.adapter = adapter

        FatchProducts(selectedCategoryId) // Load products initially
    }

    private fun FatchProducts(categoryId: String) {
        ApiClient.apiService.getProducts(categoryId).enqueue(object : Callback<FatchProduct> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<FatchProduct>, response: Response<FatchProduct>) {
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

            override fun onFailure(call: Call<FatchProduct>, t: Throwable) {
                showToast("Error fetching products: ${t.localizedMessage}")
                Log.e("HomeFragment", "Error fetching products: ${t.localizedMessage}")
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