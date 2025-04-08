package com.Stylo.stylo.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.Stylo.stylo.RetrofitApi.ApiClient
import com.Stylo.stylo.RetrofitApi.Category
import com.Stylo.stylo.RetrofitApi.CategoryResponse
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

    private var currentApiCall: Call<FatchProduct>? = null
    private var lastRequestedCategoryId: String = "0"

    private var categoryList = ArrayList<Category>()
    private var categoryAdapter: CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupProductRecyclerView()
        setupSearchView()
        fetchCategories()

        return binding.root
    }

    private fun setupCategoryRecyclerView() {
        if (_binding == null) return

        categoryAdapter = CategoryAdapter(categoryList) { category ->
            lastRequestedCategoryId = category.category_id.toString()
            fetchProducts(category.category_id.toString())
        }

        binding.categoryTabs.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryTabs.adapter = categoryAdapter
    }

    private fun setupProductRecyclerView() {
        if (_binding == null) return

        binding.productGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productGrid.setHasFixedSize(true)

        adapter = ProductAdapter(filteredList) { product ->
            val intent = Intent(requireContext(), Product_detail_Activity::class.java).apply {
                putExtra("PRODUCT", product)
            }
            startActivity(intent)
        }

        binding.productGrid.adapter = adapter
    }

    private fun setupSearchView() {
        if (_binding == null) return

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
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
                it.name.lowercase().contains(lowerCaseQuery) || it.description.lowercase()
                    .contains(lowerCaseQuery)
            })
        }
        adapter?.notifyDataSetChanged()
    }

    private fun fetchCategories() {
        ApiClient.apiService.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(
                call: Call<CategoryResponse>,
                response: Response<CategoryResponse>
            ) {
                val currentBinding = _binding
                if (currentBinding == null) return  // Return early if binding is null

                val fetchedData = response.body()

                if (response.isSuccessful && fetchedData?.status == true) {
                    categoryList.clear()
                    fetchedData.data.let { categoryList.addAll(it) }


                    categoryList.forEach {

                    }

                    setupCategoryRecyclerView()
                    if (categoryList.isNotEmpty()) {
                        fetchProducts(categoryList[0].category_id.toString())
                    }
                } else {
                    showToast(fetchedData?.message ?: "Failed to load categories")

                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                if (_binding == null) return  // Return early if binding is null

                showToast("Error: ${t.localizedMessage}")
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchProducts(categoryId: String) {
        currentApiCall?.cancel()
        lastRequestedCategoryId = categoryId

        val currentBinding = _binding
        if (currentBinding == null) return  // Check binding early and return if null

        productList.clear()
        filteredList.clear()
        adapter?.notifyDataSetChanged()

        currentBinding.loadingProgressBar.visibility = View.VISIBLE
        currentBinding.productGrid.visibility = View.GONE

        currentApiCall = ApiClient.apiService.getProducts(categoryId)

        currentApiCall?.enqueue(object : Callback<FatchProduct> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<FatchProduct>, response: Response<FatchProduct>) {
                if (call.isCanceled || categoryId != lastRequestedCategoryId) return

                CoroutineScope(Dispatchers.IO).launch {
                    val fetchedData = response.body()

                    withContext(Dispatchers.Main) {
                        val binding = _binding
                        if (binding == null) return@withContext  // Check binding again before accessing UI

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
                CoroutineScope(Dispatchers.Main).launch {
                    val binding = _binding
                    if (binding == null) return@launch  // Check binding before accessing UI

                    binding.loadingProgressBar.visibility = View.GONE
                    binding.productGrid.visibility = View.GONE
                    showToast("Error fetching products: ${t.localizedMessage}")
                }
            }
        })
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentApiCall?.cancel()
        _binding = null
        adapter = null
    }
}