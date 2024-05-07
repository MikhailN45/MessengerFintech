package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.study.messengerfintech.domain.model.Topic
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE stream_id = :streamId")
    fun getTopicsInStream(streamId: Int): Single<List<Topic>>

    @Query("SELECT * FROM topics WHERE title = :title")
    fun getTopicByTitle(title: String): Single<Topic>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(topic: List<Topic>): Completable

    @Update
    fun update(topic: Topic)
}