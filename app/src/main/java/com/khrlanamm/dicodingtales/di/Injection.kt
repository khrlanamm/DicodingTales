package com.khrlanamm.dicodingtales.di

import android.content.Context
import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.data.remote.retrofit.ApiConfig


object Injection {
    fun repository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }
}