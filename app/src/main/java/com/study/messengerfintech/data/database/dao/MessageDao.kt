package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.messengerfintech.data.database.model.MessageDb
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE stream_id = :streamId AND topic_title = :topicTitle")
    fun getPublicMessages(streamId: Int, topicTitle: String): Single<List<MessageDb>>

    @Query("SELECT * FROM messages WHERE user_email= :userEmail")
    fun getPrivateMessages(userEmail: String): Single<List<MessageDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(streams: List<MessageDb>): Completable

    @Delete
    fun delete(message: MessageDb): Single<Int>
}