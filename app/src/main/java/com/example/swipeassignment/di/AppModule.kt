package com.example.swipeassignment.di

import com.example.swipeassignment.data.repository.ProductRepository
import com.example.swipeassignment.ui.viewmodels.ProductViewModel
import com.example.swipeassignment.utils.RetrofitBuilder
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // RetrofitBuilder
    single { RetrofitBuilder }

    // ProductApiService
    single { get<RetrofitBuilder>().createService() }

    // Repository
    single { ProductRepository(get()) }

    // ViewModel
    viewModel { ProductViewModel(get()) }
}