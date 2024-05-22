package com.study.messengerfintech.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.study.messengerfintech.data.database.dao.MessageDao
import com.study.messengerfintech.data.database.dao.StreamDao
import com.study.messengerfintech.data.database.dao.TopicDao
import com.study.messengerfintech.data.database.dao.UserDao
import com.study.messengerfintech.data.database.model.MessageDb
import com.study.messengerfintech.data.database.model.StreamDb
import com.study.messengerfintech.data.database.model.TopicDb
import com.study.messengerfintech.data.database.model.UserDb

@Database(
    entities = [
        StreamDb::class,
        TopicDb::class,
        UserDb::class,
        MessageDb::class
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