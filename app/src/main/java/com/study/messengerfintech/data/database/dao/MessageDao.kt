package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.messengerfintech.domain.model.Message
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE stream_id = :streamId AND topic_title = :topicTitle")
    fun getPublicMessages(streamId: Int, topicTitle: String): Single<List<Message>>

    @Query("SELECT * FROM messages WHERE user_email= :userEmail")
    fun getPrivateMessages(userEmail: String): Single<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(streams: List<Message>): Completable

    @Delete
    fun delete(message: Message): Single<Int>
}