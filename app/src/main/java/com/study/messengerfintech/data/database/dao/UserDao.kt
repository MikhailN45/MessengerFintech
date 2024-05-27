package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.messengerfintech.data.model.db.UserDb
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Single<List<UserDb>>

    @Query("SELECT * FROM users WHERE id = 708846")
    fun getOwnUser(): Single<UserDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserDb): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<UserDb>)
}