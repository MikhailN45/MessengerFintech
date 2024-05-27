package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.messengerfintech.data.model.db.StreamDb
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface StreamDao {
    @Query("SELECT * FROM streams WHERE isSubscribed = 0 ORDER BY title")
    fun getAll(): Flowable<List<StreamDb>>

    @Query("SELECT * FROM streams WHERE isSubscribed = 1 ORDER BY title")
    fun getSubscribed(): Flowable<List<StreamDb>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(streams: List<StreamDb>): Completable
}