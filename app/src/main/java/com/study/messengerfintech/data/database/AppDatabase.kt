package com.study.messengerfintech.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.study.messengerfintech.data.database.dao.MessageDao
import com.study.messengerfintech.data.database.dao.StreamDao
import com.study.messengerfintech.data.database.dao.TopicDao
import com.study.messengerfintech.data.database.dao.UserDao
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.Topic
import com.study.messengerfintech.domain.model.User

@Database(
    entities = [
        Stream::class,
        Topic::class,
        User::class,
        Message::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun streamDao(): StreamDao
    abstract fun topicDao(): TopicDao
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao

    companion object {
        const val DATABASE = "room_database"
    }
}