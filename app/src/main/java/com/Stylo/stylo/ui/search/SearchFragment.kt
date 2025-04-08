package com.Stylo.stylo.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.Stylo.stylo.RetrofitApi.ApiClient
import com.Stylo.stylo.RetrofitApi.ApiInterface
import com.Stylo.stylo.adapter.SearchAdapter
import com.Stylo.stylo.data.SearchResponse
import com.Stylo.stylo.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var apiService: ApiInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()

        apiService = ApiClient.apiService
        searchAdapter = SearchAdapter()

        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResultsRecyclerView.adapter = searchAdapter

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                } else {
                    searchAdapter.updateProducts(emptyList())
                    binding.emptyResultView.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupActionBar() {
        val backButton = binding.myToolbar.btnBack
        val titleTextView = binding.myToolbar.tvTitle
        val notificationButton = binding.myToolbar.btnNotification

        titleTextView.text = "Search"

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // 💡 Fixed: use requireActivity()
        }

        notificationButton.setOnClickListener {
            Toast.makeText(requireContext(), "Notifications", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSearch(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.emptyResultView.visibility = View.GONE

        val categoryId: Int? = null  // Replace with actual category ID if needed
        val limit = 50
        val offset = 0

        apiService.searchProducts(query, categoryId, limit, offset)
            .enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val productList = response.body()?.products ?: emptyList()
                        if (productList.isNotEmpty()) {
                            searchAdapter.updateProducts(productList)
                            binding.emptyResultView.visibility = View.GONE
                        } else {
                            searchAdapter.updateProducts(emptyList())
                            binding.emptyResultView.visibility = View.VISIBLE
                        }
                    } else {
                        searchAdapter.updateProducts(emptyList())
                        binding.emptyResultView.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    searchAdapter.updateProducts(emptyList())
                    binding.emptyResultView.visibility = View.VISIBLE
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
