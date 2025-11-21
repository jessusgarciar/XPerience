package com.example.xperience_appfinal.model

data class User(
    var name: String,
    var points: Int,
    var level: Int,
    var visitedPlacesCount: Int,
    var completedMissionsCount: Int,
    var profileImageUri: String? = null
)

data class Place(
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

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val pointsReward: Int,
    val maxProgress: Int,
    var currentProgress: Int,
    val iconResId: Int
)

data class Reward(
    val id: String,
    val title: String,
    val cost: Int,
    val iconResId: Int
)

data class LoginUser(
    val username: String,
    val pass: String
)
