package com.andka.penpal.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andka.penpal.database.entity.StoryRemoteKeyEntity

@Dao
interface StoryRemoteKeyDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<StoryRemoteKeyEntity>)

    @Query("SELECT * FROM story_remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): StoryRemoteKeyEntity?

    @Query("DELETE FROM story_remote_keys")
    suspend fun deleteRemoteKeys()
}
