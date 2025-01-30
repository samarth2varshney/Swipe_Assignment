package com.example.swipeassignment.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.swipeassignment.data.repository.AppDatabase
import com.example.swipeassignment.utils.RetrofitBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class UploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val productDao = database.productDao()
        val products = productDao.getAllProducts()
        val apiService = RetrofitBuilder.createService()

        return try {
            products.forEach { product ->
                try {
                    val nameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), product.product_name)
                    val typeBody = RequestBody.create("text/plain".toMediaTypeOrNull(), product.product_type)
                    val priceBody = RequestBody.create("text/plain".toMediaTypeOrNull(), product.price.toString())
                    val taxBody = RequestBody.create("text/plain".toMediaTypeOrNull(), product.tax.toString())

                    apiService.addProduct(nameBody, typeBody, priceBody, taxBody,
                        product.imageFilePath?.let { getImageMultipart(it) })
                    productDao.deleteProductById(product.id)
                    Log.i("UploadWorker","Success")
                } catch (e: Exception) {
                    Log.i("UploadWorker",e.message.toString())
                }
            }
            Result.success()
        } catch (e: HttpException) {
            Log.i("UploadWorker",e.message())
            Result.retry()
        }
    }

    private fun getImageMultipart(imagePath:String): MultipartBody.Part {
        val imageData = MultipartBody.Part.createFormData(
            "files[]",
            File(imagePath).name,
            File(imagePath).asRequestBody("image/*".toMediaTypeOrNull())
        )

        return imageData
    }

}