package com.andka.penpal.domain

import com.google.gson.annotations.SerializedName

data class GetAllStoryResponse(

    @field:SerializedName("listStory")
    val listStory: ArrayList<ListStoryItem>,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class ListStoryItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("lon")
    val lon: String? = null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lat")
    val lat: Any? = null
)
