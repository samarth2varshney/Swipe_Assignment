package com.example.swipeassignment.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipeassignment.data.models.Product
import com.example.swipeassignment.data.repository.ProductRepository
import com.example.swipeassignment.utils.Resource
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _productListState = MutableLiveData<Resource<List<Product>>>()
    val productListState: LiveData<Resource<List<Product>>> get() = _productListState

    private val _addProductResult = MutableLiveData<Resource<Boolean>>()
    val addProductResult: LiveData<Resource<Boolean>> get() = _addProductResult

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _productListState.value = Resource.Loading()

            try {
                _productListState.value = Resource.Success(repository.fetchProducts())
            } catch (e: Exception) {
                _productListState.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }




    fun addProduct(product: Product, image: MultipartBody.Part?) {
        viewModelScope.launch {
            _addProductResult.value = Resource.Loading()

            try {
                when(val result=repository.uploadProduct(product,image)){
                    is Resource.Error -> {
                        Log.i("Upload", "Error ${result.message}")
                        _addProductResult.value = Resource.Error(result.message ?: "Unknown error")
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Log.i("Upload", "Success ${result.data}")
                        _addProductResult.value = Resource.Success(result.data!=null)
                    }
                }
            } catch (e: Exception) {
                _addProductResult.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
}
