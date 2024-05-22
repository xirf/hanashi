package com.andka.penpal.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andka.penpal.domain.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: List<ListStoryItem>)

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getAll(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun delete()
}