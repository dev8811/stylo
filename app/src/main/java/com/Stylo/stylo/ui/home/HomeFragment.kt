package com.Stylo.stylo.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapter: ProductAdapter? = null
    private val productList = mutableListOf<Product>()
    private val filteredList = mutableListOf<Product>()

    private val categories = listOf("All", "Tshirts", "Jeans", "Shoes", "Jackets")
    private val categoryIds = listOf("0", "1", "2", "3", "4")
    private var selectedCategoryId = "0"

    private var currentApiCall: Call<FatchProduct>? = null
    private var lastRequestedCategoryId: String = "0"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupCategoryRecyclerView()
        setupProductRecyclerView()
        setupSearchView()

        return binding.root
    }

    private fun setupCategoryRecyclerView() {
        val categoryAdapter = CategoryAdapter(categories) { position ->
            selectedCategoryId = categoryIds[position]
            fetchProducts(selectedCategoryId)
        }

        binding.categoryTabs.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryTabs.adapter = categoryAdapter
    }

    private fun setupProductRecyclerView() {
        binding.productGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productGrid.setHasFixedSize(true)

        adapter = ProductAdapter(filteredList) { product ->
            val intent = Intent(requireContext(), Product_detail_Activity::class.java).apply {
                putExtra("PRODUCT", product)
            }
            startActivity(intent)
        }

        binding.productGrid.adapter = adapter

        fetchProducts(selectedCategoryId)
    }

    private fun setupSearchView() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // not needed
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString().trim())
            }
        })
    }


    private fun filterProducts(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(productList)
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredList.addAll(productList.filter {
                it.name.lowercase().contains(lowerCaseQuery) ||
                        it.description.lowercase().contains(lowerCaseQuery)
            })
        }
        adapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchProducts(categoryId: String) {
        currentApiCall?.cancel()
        lastRequestedCategoryId = categoryId

        productList.clear()
        filteredList.clear()
        adapter?.notifyDataSetChanged()

        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.productGrid.visibility = View.GONE

        currentApiCall = ApiClient.apiService.getProducts(categoryId)

        currentApiCall?.enqueue(object : Callback<FatchProduct> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<FatchProduct>, response: Response<FatchProduct>) {
                if (call.isCanceled || categoryId != lastRequestedCategoryId) {
                    Log.d("HomeFragment", "Ignoring response for $categoryId (current: $lastRequestedCategoryId)")
                    return
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val fetchedData = response.body()

                    withContext(Dispatchers.Main) {
                        binding.loadingProgressBar.visibility = View.GONE

                        if (response.isSuccessful && fetchedData != null && fetchedData.status) {
                            if (fetchedData.products.isNotEmpty()) {
                                productList.clear()
                                productList.addAll(fetchedData.products)

                                filteredList.clear()
                                filteredList.addAll(productList)

                                binding.productGrid.visibility = View.VISIBLE
                                adapter?.notifyDataSetChanged()
                            } else {
                                binding.productGrid.visibility = View.GONE
                            }
                        } else {
                            binding.productGrid.visibility = View.GONE
                            showToast("No products found")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<FatchProduct>, t: Throwable) {
                if (call.isCanceled) {
                    Log.d("HomeFragment", "Request was canceled")
                    return
                }

                CoroutineScope(Dispatchers.Main).launch {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.productGrid.visibility = View.GONE
                    showToast("Error fetching products: ${t.localizedMessage}")
                    Log.e("HomeFragment", "Error fetching products: ${t.localizedMessage}")
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentApiCall?.cancel()
        currentApiCall = null
        _binding = null
        adapter = null
    }
}
