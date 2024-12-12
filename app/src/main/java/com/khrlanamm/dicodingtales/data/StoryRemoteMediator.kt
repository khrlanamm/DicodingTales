package com.khrlanamm.dicodingtales.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.khrlanamm.dicodingtales.data.local.entity.RemoteKeys
import com.khrlanamm.dicodingtales.data.remote.response.StoryEntity
import com.khrlanamm.dicodingtales.data.remote.retrofit.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val apiService: ApiService,
    private val database: StoryDatabase,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        try {
            Log.d("RemoteMediator", "Load initiated with loadType: $loadType")

            val page = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d("RemoteMediator", "LoadType.REFRESH triggered")
                    INITIAL_PAGE_INDEX
                }
                LoadType.PREPEND -> {
                    Log.d("RemoteMediator", "LoadType.PREPEND triggered")
                    val remoteKeys = getLastRemoteKey(state)
                    val prevKey = remoteKeys?.prevKey
                    Log.d("RemoteMediator", "PREPEND prevKey: $prevKey")
                    prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    Log.d("RemoteMediator", "LoadType.APPEND triggered")
                    val remoteKeys = getLastRemoteKey(state)
                    val nextKey = remoteKeys?.nextKey
                    Log.d("RemoteMediator", "APPEND nextKey: $nextKey")
                    nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            Log.d("RemoteMediator", "Fetching data from API: page = $page, pageSize = ${state.config.pageSize}")
            val response = apiService.getStories("Bearer $token", page, state.config.pageSize)
            val stories = response.listStory.map {
                StoryEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    photoUrl = it.photoUrl,
                    createdAt = it.createdAt,
                    lat = it.lat,
                    lon = it.lon
                )
            }

            val endOfPaginationReached = stories.isEmpty()
            Log.d("RemoteMediator", "Data fetched: ${stories.size} stories, endOfPaginationReached: $endOfPaginationReached")

            val keys = stories.map {
                RemoteKeys(
                    storyId = it.id,
                    prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                    nextKey = if (endOfPaginationReached) null else page + 1
                )
            }
            Log.d("RemoteMediator", "Generated RemoteKeys: $keys")

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Log.d("RemoteMediator", "Clearing old data for REFRESH")
                    database.remoteKeysDao().clearRemoteKeys()
                    database.storyDao().clearAllStories()
                }

                Log.d("RemoteMediator", "Inserting new data into database")
                database.remoteKeysDao().insertKeys(keys)
                Log.d("RemoteMediator", "Inserted RemoteKeys: $keys")
                database.storyDao().insertStories(stories)
            }

            Log.d("RemoteMediator", "Load completed successfully for page: $page")
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Log.e("RemoteMediator", "Error during load: ${e.message}", e)
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        val lastPage = state.pages.lastOrNull { it.data.isNotEmpty() }
        val lastStory = lastPage?.data?.lastOrNull()
        val remoteKeys = lastStory?.let { story ->
            database.remoteKeysDao().getRemoteKeys(story.id)
        }
        Log.d("RemoteMediator", "getLastRemoteKey: $remoteKeys for story: $lastStory")
        return remoteKeys
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}
