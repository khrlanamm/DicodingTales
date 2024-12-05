package com.khrlanamm.dicodingtales.di

import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.data.remote.retrofit.ApiConfig

object Injection {
    fun repository(): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }
}