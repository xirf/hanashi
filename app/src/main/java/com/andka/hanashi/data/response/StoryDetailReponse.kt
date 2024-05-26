package com.andka.hanashi.data.response

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: StoryResponse
)