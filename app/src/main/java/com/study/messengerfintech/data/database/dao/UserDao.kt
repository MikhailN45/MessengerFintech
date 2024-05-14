package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.messengerfintech.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Single<List<User>>

    @Query("SELECT * FROM users WHERE id = 708846")
    fun getOwnUser(): Single<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<User>)
}