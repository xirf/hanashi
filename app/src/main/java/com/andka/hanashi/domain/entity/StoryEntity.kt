package com.andka.hanashi.domain.entity

data class StoryEntity(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val lat: Double?,
    val long: Double?,
    val createdAt: String
)