package com.khrlanamm.dicodingtales.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.khrlanamm.dicodingtales.data.local.entity.StoryEntity
import com.khrlanamm.dicodingtales.data.remote.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<ListStoryItem>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM stories")
    suspend fun clearStories()
}

