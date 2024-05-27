package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.messengerfintech.data.model.db.TopicDb
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE stream_id = :streamId ORDER BY title")
    fun getTopicsInStream(streamId: Int): Single<List<TopicDb>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(topic: List<TopicDb>): Completable
}