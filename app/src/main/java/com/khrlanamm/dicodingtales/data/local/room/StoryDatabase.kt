package com.khrlanamm.dicodingtales.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.khrlanamm.dicodingtales.data.local.entity.RemoteKeys
import com.khrlanamm.dicodingtales.data.local.entity.StoryEntity

@Database(entities = [StoryEntity::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
