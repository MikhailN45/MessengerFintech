package com.study.messengerfintech.di

import android.content.Context
import androidx.room.Room
import com.study.messengerfintech.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Singleton
    @Provides
    fun provideDatabase(applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, AppDatabase.DATABASE
    ).build()
}