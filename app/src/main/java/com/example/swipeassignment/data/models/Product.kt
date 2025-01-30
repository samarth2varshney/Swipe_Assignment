package com.example.swipeassignment.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    val image: String?=null,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageFilePath: String? = null
)