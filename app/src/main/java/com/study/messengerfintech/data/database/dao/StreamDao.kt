package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.messengerfintech.domain.model.Stream
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface StreamDao {
    @Query("SELECT * FROM streams WHERE isSubscribed = 0")
    fun getAll(): Flowable<List<Stream>>

    @Query("SELECT * FROM streams WHERE isSubscribed = 1")
    fun getSubscribed(): Flowable<List<Stream>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(streams: List<Stream>): Completable
}