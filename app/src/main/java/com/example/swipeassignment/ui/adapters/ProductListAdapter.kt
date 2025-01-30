package com.example.swipeassignment.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.swipeassignment.R
import com.example.swipeassignment.data.models.Product
import com.example.swipeassignment.databinding.ItemProductBinding
import com.bumptech.glide.Glide

class ProductListAdapter : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    private val productList: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = productList[position]
        holder.bind(item)
    }

    fun updateList(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(product: Product) {
            binding.textViewProductName.text = product.product_name
            binding.textViewProductType.text = product.product_type
            binding.textViewPrice.text = "Price: \$${product.price}"
            binding.textViewTax.text = "Tax: ${product.tax}%"
            Glide.with(binding.imageViewProduct)
                .load(product.image)
                .placeholder(R.drawable.images)
                .into(binding.imageViewProduct)


        }
    }
}