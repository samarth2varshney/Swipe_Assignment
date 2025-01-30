package com.example.swipeassignment.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.swipeassignment.data.UploadWorker
import com.example.swipeassignment.data.models.Product
import com.example.swipeassignment.data.repository.AppDatabase
import com.example.swipeassignment.data.repository.ProductRepository
import com.example.swipeassignment.utils.Resource
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository,private val application: Application) : ViewModel() {

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

    private fun scheduleUploadWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(application).enqueue(uploadWorkRequest)
    }

    fun addProduct(product: Product,database: AppDatabase) {
        viewModelScope.launch {
            _addProductResult.value = Resource.Loading()

            try {
                when(val result=repository.uploadProduct(product)){
                    is Resource.Error -> {
                        database.productDao().insertProduct(product)
                        scheduleUploadWorker()
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
                database.productDao().insertProduct(product)
                scheduleUploadWorker()
                _addProductResult.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
}
