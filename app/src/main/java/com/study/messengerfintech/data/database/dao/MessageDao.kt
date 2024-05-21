package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.messengerfintech.data.database.dto.MessageDto
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE stream_id = :streamId AND topic_title = :topicTitle")
    fun getPublicMessages(streamId: Int, topicTitle: String): Single<List<MessageDto>>

    @Query("SELECT * FROM messages WHERE user_email= :userEmail")
    fun getPrivateMessages(userEmail: String): Single<List<MessageDto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(streams: List<MessageDto>): Completable

    @Delete
    fun delete(message: MessageDto): Single<Int>
}