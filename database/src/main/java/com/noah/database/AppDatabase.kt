package com.noah.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var instance: AppDatabase? = null
        fun getDatabase(context: Context) : AppDatabase?{
            if (instance == null) {
                synchronized(Database::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "database-app").
                        allowMainThreadQueries().build()
                    }
                }
            }
            return instance
        }
    }
    abstract fun userDao(): UserDao
}
    