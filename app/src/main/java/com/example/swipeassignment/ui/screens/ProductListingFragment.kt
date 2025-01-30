package com.example.swipeassignment.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swipeassignment.databinding.FragmentProductListingBinding
import com.example.swipeassignment.ui.adapters.ProductListAdapter
import com.example.swipeassignment.ui.viewmodels.ProductViewModel
import com.example.swipeassignment.utils.Resource
import com.example.swipeassignment.utils.toast
import org.koin.android.ext.android.inject
import java.util.*

class ProductListingFragment : Fragment() {

    private lateinit var binding: FragmentProductListingBinding
    private val viewModel by inject<ProductViewModel>()
    private lateinit var searchView: SearchView
    private lateinit var productAdapter: ProductListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductListingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()


        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })
        binding.noProductFound.visibility = View.GONE

    }




    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            productAdapter = ProductListAdapter()
            adapter = productAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.productListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.noProductFound.visibility = View.GONE

                    binding.progressBar.visibility = View.GONE
                    state.data?.let {
                        productAdapter.updateList(it)
                    }
                }
                is Resource.Error -> {
                    binding.noProductFound.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    toast(state.message)

                }
            }
        }
    }



    private fun filterList(query: String?) {
        val deviceListState = viewModel.productListState.value
        if (query != null && deviceListState is Resource.Success) {
            val deviceList = deviceListState.data
            val filteredList = deviceList?.filter { item ->
                item.product_name.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                        item.product_type.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                        item.tax.toString().contains(query) ||
                        item.price.toString().contains(query)
            }

            if (filteredList!=null && filteredList.isEmpty()) {
                binding.noProductFound.visibility = View.VISIBLE
                productAdapter.updateList(mutableListOf())
                toast("No Data found")
            } else {
                binding.noProductFound.visibility = View.GONE
                productAdapter.updateList(filteredList?.toMutableList() ?: emptyList())
            }
        } else {
            binding.noProductFound.visibility = View.GONE
            productAdapter.updateList(mutableListOf())
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProducts()
    }

}