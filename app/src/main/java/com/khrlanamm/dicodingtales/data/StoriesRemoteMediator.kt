package com.khrlanamm.dicodingtales.data

import androidx.paging.*
import androidx.room.withTransaction
import com.khrlanamm.dicodingtales.data.local.entity.RemoteKeys
import com.khrlanamm.dicodingtales.data.local.room.StoryDatabase
import com.khrlanamm.dicodingtales.data.remote.response.ListStoryItem
import com.khrlanamm.dicodingtales.data.remote.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class StoriesRemoteMediator(
    private val token: String,
    private val database: StoryDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, ListStoryItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val response = apiService.getStories("Bearer $token", page ?: 1, state.config.pageSize)
            val stories = response.listStory
            val endOfPaginationReached = stories.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.storyDao().clearStories()
                }
                val keys = stories.map {
                    RemoteKeys(storyId = it.id, prevKey = null, nextKey = page?.plus(1))
                }
                database.remoteKeysDao().insertAll(keys)
                database.storyDao().insertStories(stories)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.lastItemOrNull()?.let { story ->
            database.remoteKeysDao().remoteKeysByStoryId(story.id)
        }
    }
}
