package com.khrlanamm.dicodingtales.ui.upload

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.di.Injection

class UploadFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: UploadFactory? = null
        fun getInstance(context: Context): UploadFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UploadFactory(
                    Injection.repository(context)
                )
            }.also { INSTANCE = it }
    }
}