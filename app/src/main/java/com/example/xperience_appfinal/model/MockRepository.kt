package com.example.xperience_appfinal.model

import com.example.xperience_appfinal.R
import kotlin.collections.minusAssign
import kotlin.collections.plusAssign
import kotlin.compareTo
import kotlin.div
import kotlin.inc


object MockRepository {

    val currentUser = User(
        name = "Explorador Hidrocálido",
        points = 150,
        level = 2,
        visitedPlacesCount = 12,
        completedMissionsCount = 5
    )

    // --- LUGARES (Lista Actualizada Aguascalientes) ---
    val placesList = listOf(
        Place(
            id = "p1",
            title = "Restaurante Apapacho",
            description = "Cocina de autor y ambiente acogedor",
            pointsAwarded = 70,
            category = "RESTAURANTE",
            latitude = 21.8756, // Zona Norte aprox21.87565771614443, -102.29275288340482
            longitude = -102.2927,
            imageResId = R.drawable.place_apapacho,
            isVisited = false
        ),
        Place(
            id = "p2",
            title = "El Sabinal",
            description = "Reserva natural",
            pointsAwarded = 50,
            category = "NATURALEZA",
            latitude = 21.7568, // Salto de los Salado21.75688594810799, -102.3745717954366
            longitude = -102.3745,
            imageResId = R.drawable.place_sabinal,
            isVisited = false
        ),
        Place(
            id = "p3",
            title = "Museo Aguascalientes",
            description = "Arte clásico en un edificio histórico. (Noche: 2x Puntos)",
            pointsAwarded = 30, // Base: 30 (Día). Lógica nocturna (60) se aplicará en Check-in
            category = "MUSEO",
            latitude = 21.8857, // Centro21.885795656807645, -102.29140750686173
            longitude = -102.2914,
            imageResId = R.drawable.place_museo,
            isVisited = false
        ),
        Place(
            id = "p4",
            title = "Punto y Aparte",
            description = "Restaurante y juegos de mesa en San Marcos",
            pointsAwarded = 70,
            category = "RESTAURANTE",
            latitude = 21.8791, // Barrio San Marcos21.87919405658317, -102.30252332247763
            longitude = -102.3025,
            imageResId = R.drawable.place_punto,
            isVisited = false
        ),
        Place(
            id = "p5",
            title = "Olive Greenspot",
            description = "Opción saludable y fresca",
            pointsAwarded = 70,
            category = "RESTAURANTE",
            latitude = 21.9274, // Zona Comercial21.927495258382287, -102.31853625110142
            longitude = -102.3185,
            imageResId = R.drawable.place_olive,
            isVisited = false
        ),
        Place(
            id = "p6",
            title = "Boca de Túnel",
            description = "Parque de aventura y puentes colgantes",
            pointsAwarded = 100,
            category = "AVENTURA",
            latitude = 22.2354, // San José de Gracia22.235486262146637, -102.44376525215549
            longitude = -102.4437,
            imageResId = R.drawable.place_boca,
            isVisited = false
        ),
        Place(
            id = "p7",
            title = "Parque Acuático La Alameda",
            description = "Diversión acuática en San José de Gracia",
            pointsAwarded = 100,
            category = "PARQUE",
            latitude = 22.1424, // San José de Gracia22.142424523574732, -102.41556648058953
            longitude = -102.4155,
            imageResId = R.drawable.place_parque,
            isVisited = false
        )
    )

    val missionsList = listOf(
        Mission("m1", "Aventura en Altura", "Cruza los puentes de Boca de Túnel", 100, 1, 0, R.drawable.ic_explore),
        Mission("m2", "Cultura Clásica", "Visita el Museo Aguascalientes", 30, 1, 0, R.drawable.ic_mission_museum),
        Mission("m3", "Tarde de Juegos", "Visita Punto y Aparte", 70, 1, 0, R.drawable.ic_mission_foodie)
    )

    val rewardsList = listOf(
        Reward("r1", "Sticker Xperience", 30, R.drawable.ic_star_points),
        Reward("r2", "Galleta de Cortesía", 50, R.drawable.ic_mission_foodie),
        Reward("r3", "Topping Extra", 60, R.drawable.ic_mission_foodie),
        Reward("r4", "Bebida Pequeña", 90, R.drawable.ic_mission_coffee)
    )

    fun addPoints(amount: Int) {
        currentUser.points += amount
        currentUser.level = (currentUser.points / 1000) + 1
    }

    fun redeemPoints(cost: Int): Boolean {
        if (currentUser.points >= cost) {
            currentUser.points -= cost
            return true
        }
        return false
    }

    fun markPlaceAsVisited(placeId: String) {
        placesList.find { it.id == placeId }?.isVisited = true
        currentUser.visitedPlacesCount++
    }

}