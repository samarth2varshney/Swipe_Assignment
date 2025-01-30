package com.example.swipeassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.swipeassignment.databinding.ActivityMainBinding
import com.example.swipeassignment.ui.screens.AddNewProductFragment

class MainActivity : AppCompatActivity(),AddNewProductFragment.CardDialogListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var dialog: AddNewProductFragment /** BottomSheetDialogFragment for choose card */
    lateinit var listener: AddNewProductFragment.CardDialogListener /** Listener for choose card */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listener = this

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addNewProductFragment -> {
                    // Show the BottomSheetDialogFragment
                    showBottomSheetDialogFragment()
                    true
                }
                R.id.productListingFragment -> {
                    navController.navigate(R.id.productListingFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }

    }

    fun showBottomSheetDialogFragment() {
        dialog = AddNewProductFragment(listener)
        dialog.show(supportFragmentManager, "tag")
    }

    override fun close() {
        navController.navigate(R.id.productListingFragment)
        dialog.dismiss()
    }

}