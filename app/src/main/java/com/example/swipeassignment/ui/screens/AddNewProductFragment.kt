package com.example.swipeassignment.ui.screens

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.swipeassignment.R
import com.example.swipeassignment.data.models.Product
import com.example.swipeassignment.data.repository.AppDatabase
import com.example.swipeassignment.databinding.FragmentAddNewProductBinding
import com.example.swipeassignment.ui.viewmodels.ProductViewModel
import com.example.swipeassignment.utils.Resource
import com.example.swipeassignment.utils.getRealPathFromURI
import com.example.swipeassignment.utils.isValidImage
import com.example.swipeassignment.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import java.io.File


class AddNewProductFragment(listener: CardDialogListener) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddNewProductBinding
    private val viewModel by inject<ProductViewModel>()
    private var mBottomSheetListener: CardDialogListener?=null

    init {
        this.mBottomSheetListener = listener
    }


    private var selectedImageUri: Uri? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {

                if (it.isValidImage(requireContext())) {
                    selectedImageUri = it
                    binding.editTextImageFileName.setText(File(it.path!!).name)
                }

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddNewProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /** attach listener from parent fragment */
        try {
            mBottomSheetListener = context as CardDialogListener?
        }
        catch (e: ClassCastException){
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        val productTypes = resources.getStringArray(R.array.product_types)
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, productTypes)
        binding.spinnerProductType.adapter = spinnerAdapter

        binding.btnAddProduct.setOnClickListener {
            val productName = binding.editTextProductName.text.toString()
            val productType = binding.spinnerProductType.selectedItem.toString()
            val price = binding.editTextSellingPrice.text.toString().toDoubleOrNull()
            val tax = binding.editTextTaxRate.text.toString().toDoubleOrNull()

            if (validateInputs(productName, productType, price, tax)) {
                val product = Product(price =  price!!,product_name =  productName, product_type =  productType, tax = tax!!, imageFilePath = getRealPathFromURI(selectedImageUri))
                viewModel.addProduct(product, AppDatabase.getDatabase(requireContext()))
            }
        }

        binding.addProductBack.setOnClickListener {
            mBottomSheetListener?.close()
        }

        binding.btnAddImage.setOnClickListener {
            getContent.launch("image/*")
        }


    }

    private fun observeViewModel() {

        viewModel.addProductResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.addProductProgress.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.addProductProgress.visibility = View.GONE
                    if (result.data!=null) {
                        toast("Product added successfully!")
                        mBottomSheetListener?.close()
                    } else {
                            toast("Failed to add product.")
                    }
                }

                is Resource.Error -> {
                    binding.addProductProgress.visibility = View.GONE
                    toast("Failed to add product.")
                    Log.e("add", "${result.message}")
                }
            }


        }
    }

    private fun validateInputs(
        productName: String,
        productType: String,
        price: Double?,
        tax: Double?
    ): Boolean {
        if (productName.isEmpty()) {
            binding.editTextProductName.error = "Product name cannot be empty"
            return false
        }

        if (productType.isEmpty()) {
        toast("Please select a product type")
            return false
        }

        if (price == null) {
            binding.editTextSellingPrice.error = "Invalid price"
            return false
        }

        if (tax == null) {
            binding.editTextTaxRate.error = "Invalid tax rate"
            return false
        }

        return true
    }

    interface CardDialogListener {
        fun close()
    }

}