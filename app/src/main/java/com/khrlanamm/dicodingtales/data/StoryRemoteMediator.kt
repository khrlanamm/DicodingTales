package com.khrlanamm.dicodingtales.data


import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.khrlanamm.dicodingtales.data.local.room.RemoteKeysDao
import com.khrlanamm.dicodingtales.data.local.room.StoryDao
import com.khrlanamm.dicodingtales.data.local.entity.RemoteKeys
import com.khrlanamm.dicodingtales.data.remote.response.StoryEntity
import com.khrlanamm.dicodingtales.data.remote.retrofit.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val apiService: ApiService,
    private val storyDao: StoryDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        } ?: 1

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
                RemoteKeys(it.id, prevKey = if (page == 1) null else page - 1, nextKey = if (endOfPaginationReached) null else page + 1)
            }

            // Save to database
            if (loadType == LoadType.REFRESH) {
                remoteKeysDao.clearRemoteKeys()
                storyDao.clearAllStories()
            }
            remoteKeysDao.insertKeys(keys)
            storyDao.insertStories(stories)

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.lastItemOrNull()?.let { remoteKeysDao.getRemoteKeys(it.id) }
    }
}
