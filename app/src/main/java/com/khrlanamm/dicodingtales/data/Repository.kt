package com.khrlanamm.dicodingtales.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.khrlanamm.dicodingtales.data.local.entity.StoryEntity
import com.khrlanamm.dicodingtales.data.local.room.StoryDatabase
import com.khrlanamm.dicodingtales.data.remote.response.ListStoryItem
import com.khrlanamm.dicodingtales.data.remote.response.LoginResponse
import com.khrlanamm.dicodingtales.data.remote.response.RegisterResponse
import com.khrlanamm.dicodingtales.data.remote.response.Story
import com.khrlanamm.dicodingtales.data.remote.response.UploadResponse
import com.khrlanamm.dicodingtales.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    suspend fun getAllStories(token: String): Result<List<ListStoryItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer $token"
                val response = apiService.getStories(token)
                if (!response.error) {
                    Result.Success(response.listStory)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: HttpException) {
                Result.Error("Http Exception: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An error occured: ${e.message}")
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
                Result.Error("An error occured: ${e.message}")
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

    suspend fun getStoryWithMap(token: String, location: Int): Result<List<ListStoryItem>> {
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
                Result.Error("An error occured: ${e.message}")
            }
        }
    }
    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesPaging(token: String): Flow<PagingData<ListStoryItem>> {
        val pagingSourceFactory = {
            database.storyDao().getAllStories().value?.map { it.toDomain() } ?: emptyList()
        }

        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoriesRemoteMediator(token, database, apiService),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }



    fun ListStoryItem.toEntity(): StoryEntity {
        return StoryEntity(
            id = id,
            photoUrl = photoUrl,
            createdAt = createdAt,
            name = name,
            description = description,
            lon = lon,
            lat = lat
        )
    }

    fun StoryEntity.toDomain(): ListStoryItem {
        return ListStoryItem(
            id = id,
            photoUrl = photoUrl,
            createdAt = createdAt,
            name = name,
            description = description,
            lon = lon,
            lat = lat
        )
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