package com.study.messengerfintech.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.study.messengerfintech.data.database.dto.UserDto
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Single<List<UserDto>>

    @Query("SELECT * FROM users WHERE id = 708846")
    fun getOwnUser(): Single<UserDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserDto): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<UserDto>)
}