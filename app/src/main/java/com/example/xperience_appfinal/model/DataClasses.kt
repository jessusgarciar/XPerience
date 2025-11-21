package com.example.xperience_appfinal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 1, // ID fijo para el usuario actual
    var name: String,
    var points: Int,
    var level: Int,
    var visitedPlacesCount: Int,
    var completedMissionsCount: Int,
    var profileImageUri: String? = null
)

@Entity(tableName = "places")
data class Place(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val pointsAwarded: Int,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val imageResId: Int,
    var isVisited: Boolean = false
)

@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val pointsReward: Int,
    val maxProgress: Int,
    var currentProgress: Int,
    val iconResId: Int
)

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey
    val id: String,
    val title: String,
    val cost: Int,
    val iconResId: Int
)

data class LoginUser(
    val username: String,
    val pass: String
)
