package com.khrlanamm.dicodingtales.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.khrlanamm.dicodingtales.data.local.entity.RemoteKeys
import com.khrlanamm.dicodingtales.data.StoryDatabase
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
        val page = when (loadType) {
            LoadType.REFRESH -> INITIAL_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
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
            val keys = stories.map {
                RemoteKeys(
                    storyId = it.id,
                    prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                    nextKey = if (endOfPaginationReached) null else page + 1
                )
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.storyDao().clearAllStories()
                }
                database.remoteKeysDao().insertKeys(keys)
                database.storyDao().insertStories(stories)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { story -> database.remoteKeysDao().getRemoteKeys(story.id) }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}
