package com.khrlanamm.dicodingtales.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.khrlanamm.dicodingtales.data.StoryDatabase
import com.khrlanamm.dicodingtales.data.remote.response.*
import com.khrlanamm.dicodingtales.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class Repository private constructor(
    private val apiService: ApiService,
    private val database: StoryDatabase
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(email, password)
                if (response.error == false) {
                    Result.Success(response)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: Exception) {
                Result.Error("${e.message}")
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(name, email, password)

                if (!response.error) {
                    Result.Success(response)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: HttpException) {
                Result.Error("${e.message}")
            }
        }
    }

    suspend fun getDetail(token: String, id: String): Result<Story> {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer $token"
                val response = apiService.getDetail(token, id)
                if (!response.error) {
                    Result.Success(response.story)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: HttpException) {
                Result.Error("Http Exception: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An error occurred: ${e.message}")
            }
        }
    }

    suspend fun upload(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Result<UploadResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer $token"
                val response = apiService.upload(token, description, photo, lat, lon)
                if (!response.error) {
                    Result.Success(response)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: HttpException) {
                Result.Error("HTTP Exception: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An error occurred: ${e.message}")
            }
        }
    }

    suspend fun getStoryWithMap(token: String, location: Int): Result<List<StoryEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer $token"
                val response = apiService.getStories(token, location = location)
                if (!response.error) {
                    Result.Success(response.listStory)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: HttpException) {
                Result.Error("Http Exception: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An error occurred: ${e.message}")
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesPagingWithMediator(token: String): LiveData<PagingData<StoryEntity>> {
        val pagingSourceFactory = { database.storyDao().getStories() } // Gunakan DAO dari database
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(apiService, database, token), // Berikan database
            pagingSourceFactory = pagingSourceFactory
        ).liveData
    }

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null
        fun getInstance(
            apiService: ApiService,
            database: StoryDatabase
        ): Repository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Repository(apiService, database)
        }.also { INSTANCE = it }
    }
}
