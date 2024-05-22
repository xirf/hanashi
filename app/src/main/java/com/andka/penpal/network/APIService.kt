package com.andka.penpal.network

import com.andka.penpal.domain.GetAllStoryResponse
import com.andka.penpal.domain.NewStoryResponse
import com.andka.penpal.domain.RegisterResponse
import com.andka.penpal.domain.StoryDetailResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): Call<RegisterResponse>

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): GetAllStoryResponse

    @GET("stories/{id}")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): StoryDetailResponse

    @POST("stories")
    @Multipart
    suspend fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: String,
        @Part("lat") lat: Float?,
        @Part("long") long: Float?
    ): Call<NewStoryResponse>
}