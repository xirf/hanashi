package com.andka.hanashi.dummy

import com.andka.hanashi.data.response.StoryResponse

object DummyData {
    fun generateDummyStoryResponse(): List<StoryResponse> {
        val items: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..10) {
            val item = StoryResponse(
                id = i.toString(),
                createdAt = i.toString(),
                description = i.toString(),
                lat = 0.0,
                lon = 0.0,
                name = i.toString(),
                photoUrl = i.toString(),
            )
            items.add(item)
        }
        return items
    }
}