package com.example.swipeassignment.data.api

import com.example.swipeassignment.data.models.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {

    @GET("get")
    suspend fun getProducts(): Response<List<Product>>

    @Multipart
    @POST("add")
    suspend fun addProduct(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: MultipartBody.Part?
    ): ResponseBody
}
