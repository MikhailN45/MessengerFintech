package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.study.messengerfintech.data.database.dto.TopicDto
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE stream_id = :streamId ORDER BY title")
    fun getTopicsInStream(streamId: Int): Single<List<TopicDto>>

    @Query("SELECT * FROM topics WHERE title = :title")
    fun getTopicByTitle(title: String): Single<TopicDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(topic: List<TopicDto>): Completable

    @Update
    fun update(topic: TopicDto)
}