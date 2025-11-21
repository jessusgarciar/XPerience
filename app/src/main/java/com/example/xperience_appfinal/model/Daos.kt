package com.example.xperience_appfinal.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1")
    suspend fun getCurrentUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: User)
}

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places")
    suspend fun getAllPlaces(): List<Place>

    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getPlaceById(placeId: String): Place?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<Place>)

    @Update
    suspend fun updatePlace(place: Place)
}

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions")
    suspend fun getAllMissions(): List<Mission>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(missions: List<Mission>)

    @Update
    suspend fun updateMission(mission: Mission)
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards")
    suspend fun getAllRewards(): List<Reward>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rewards: List<Reward>)
}
