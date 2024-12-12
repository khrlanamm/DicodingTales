package com.khrlanamm.dicodingtales

import com.khrlanamm.dicodingtales.data.remote.response.StoryEntity
import java.util.Random

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 1..100) {
            val story = StoryEntity(
                photoUrl = "https://picsum.photos/200/300?random=$i",
                createdAt = "2024-10-${i % 31 + 1}T12:34:56Z",
                name = "Story $i",
                description = "description for Story $i",
                lon = (100.0..140.0).random(),
                id = "$i",
                lat = (-10.0..10.0).random()
            )
            items.add(story)
        }
        return items
    }
    private fun ClosedRange<Double>.random() =
        Random().nextDouble() * (endInclusive - start) + start
}

