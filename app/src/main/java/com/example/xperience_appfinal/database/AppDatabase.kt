package com.example.xperience_appfinal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.xperience_appfinal.model.Mission
import com.example.xperience_appfinal.model.MissionDao
import com.example.xperience_appfinal.model.Place
import com.example.xperience_appfinal.model.PlaceDao
import com.example.xperience_appfinal.model.Reward
import com.example.xperience_appfinal.model.RewardDao
import com.example.xperience_appfinal.model.User
import com.example.xperience_appfinal.model.UserDao

@Database(entities = [User::class, Place::class, Mission::class, Reward::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun placeDao(): PlaceDao
    abstract fun missionDao(): MissionDao
    abstract fun rewardDao(): RewardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "xperience_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}