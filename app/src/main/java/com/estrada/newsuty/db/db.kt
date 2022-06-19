package com.estrada.newsuty.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [News::class, User::class, NewsVotes::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDAO(): NewsDAO
    abstract fun userDAO(): UserDAO
    abstract fun voteDAO(): VoteDAO

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "newsuty")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                    .also { instancia = it }
            }
        }
    }
}