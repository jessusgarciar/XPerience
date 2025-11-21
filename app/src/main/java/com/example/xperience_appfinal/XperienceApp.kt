package com.example.xperience_appfinal

import android.app.Application
import com.example.xperience_appfinal.database.AppDatabase
import com.example.xperience_appfinal.model.Repository

class XperienceApp : Application() {

    private val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: Repository by lazy { Repository(database.userDao(), database.placeDao(), database.missionDao(), database.rewardDao()) }

}