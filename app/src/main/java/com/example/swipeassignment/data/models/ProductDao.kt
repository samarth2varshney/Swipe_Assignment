package com.example.swipeassignment.data.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {
    @Insert
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)
}