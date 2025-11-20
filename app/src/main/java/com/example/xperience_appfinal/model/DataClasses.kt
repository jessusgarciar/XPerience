package com.example.xperience_appfinal.model

data class User(
    val name: String,
    var points: Int,
    var level: Int,
    var visitedPlacesCount: Int,
    var completedMissionsCount: Int
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