package com.khrlanamm.dicodingtales.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.di.Injection

class HomeFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: HomeFactory? = null
        fun getInstance(): HomeFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HomeFactory(
                    Injection.repository()
                )
            }.also { INSTANCE = it }
    }
}