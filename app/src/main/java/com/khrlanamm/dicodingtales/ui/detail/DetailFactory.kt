package com.khrlanamm.dicodingtales.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.di.Injection

class DetailFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: DetailFactory? = null
        fun getInstance(): DetailFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DetailFactory(
                    Injection.repository()
                )
            }.also { INSTANCE = it }
    }
}