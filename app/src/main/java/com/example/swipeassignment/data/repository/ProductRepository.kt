package com.example.swipeassignment.data.repository

import com.example.swipeassignment.data.api.ProductApiService
import com.example.swipeassignment.data.models.Product
import com.example.swipeassignment.utils.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File

class ProductRepository(private val apiService: ProductApiService) {

    suspend fun fetchProducts(): List<Product> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getImageMultipart(imagePath:String): MultipartBody.Part {
        val imagedata = MultipartBody.Part.createFormData(
            "files[]",
            File(imagePath).name,
            File(imagePath).asRequestBody("image/*".toMediaTypeOrNull())
        )

        return imagedata
    }

    suspend fun uploadProduct(product: Product): Resource<ResponseBody> {
        return try {
            val nameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), product.product_name)
            val typeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), product.product_type)
            val priceBody = RequestBody.create("text/plain".toMediaTypeOrNull(), product.price.toString())
            val taxBody = RequestBody.create("text/plain".toMediaTypeOrNull(), product.tax.toString())

            val response = apiService.addProduct(nameBody, typeBody, priceBody, taxBody,
                product.imageFilePath?.let { getImageMultipart(it) })
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error("Failed to upload product: ${e.message}")
        }
    }
}
