package com.example.xperience_appfinal.model

import com.example.xperience_appfinal.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Repository(
    private val userDao: UserDao,
    private val placeDao: PlaceDao,
    private val missionDao: MissionDao,
    private val rewardDao: RewardDao
) {

    // --- Inicialización de datos ---
    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (placeDao.getAllPlaces().isEmpty()) {
                placeDao.insertAll(getInitialPlaces())
            }
            if (missionDao.getAllMissions().isEmpty()) {
                missionDao.insertAll(getInitialMissions())
            }
            if (rewardDao.getAllRewards().isEmpty()) {
                rewardDao.insertAll(getInitialRewards())
            }
            if (userDao.getCurrentUser() == null) {
                userDao.insertOrUpdateUser(getInitialUser())
            }
        }
    }

    // --- USUARIO ---
    suspend fun getCurrentUser() = userDao.getCurrentUser()
    suspend fun updateUser(user: User) = userDao.insertOrUpdateUser(user)

    // --- LUGARES ---
    suspend fun getAllPlaces() = placeDao.getAllPlaces()
    suspend fun getPlaceById(id: String) = placeDao.getPlaceById(id)
    suspend fun updatePlace(place: Place) = placeDao.updatePlace(place)

    // --- MISIONES ---
    suspend fun getAllMissions() = missionDao.getAllMissions()
    suspend fun updateMission(mission: Mission) = missionDao.updateMission(mission)

    // --- RECOMPENSAS ---
    suspend fun getAllRewards() = rewardDao.getAllRewards()

    // --- LÓGICA DE NEGOCIO (LOGIN) ---
    private val loginUsers = listOf(
        LoginUser("usuario1", "123456"),
        LoginUser("usuario2", "123456"),
        LoginUser("usuario3", "123456")
    )

    fun verifyUser(username: String, pass: String): Boolean {
        return loginUsers.any { it.username == username && it.pass == pass }
    }

    // --- DATOS INICIALES ---
    private fun getInitialUser() = User(
        name = "Explorador Hidrocálido",
        points = 0,
        level = 0,
        visitedPlacesCount = 0,
        completedMissionsCount = 0,
        profileImageUri = null
    )

    private fun getInitialPlaces() = listOf(
        Place(
            id = "p1",
            title = "Restaurante Apapacho",
            description = "Cocina de autor y ambiente acogedor",
            pointsAwarded = 70,
            category = "RESTAURANTE",
            latitude = 21.8756,
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
        ),
        Place(
            id = "p8",
            title = "Playas",
            description = "Diversión en las playas de San José de Gracia",
            pointsAwarded = 150,
            category = "PLAYA",
            latitude = 22.1502, // 22.150269332174528, -102.42160556432289
            longitude = -102.4216,
            imageResId = R.drawable.place_playasjg,
            isVisited = false
        ),
        Place(
            id = "p9",
            title = "Cristo Roto",
            description = "Isla de San Jose de Gracia",
            pointsAwarded = 100,
            category = "AVENTURA",
            latitude = 22.1389, // 22.138969632249914, -102.43065687967793
            longitude = -102.4306,
            imageResId = R.drawable.place_cristo,
            isVisited = false
        ),
        Place(
            id = "p10",
            title = "Plaza Principal Pabellon",
            description = "Plaza de Pabellon de Arteaga",
            pointsAwarded = 150,
            category = "PLAZA",
            latitude = 22.1477, //22.147750744505863, -102.27850090877669
            longitude = -102.2785,
            imageResId = R.drawable.place_plazapabellon,
            isVisited = false
        ),
        Place(
            id = "p11",
            title = "Altaria",
            description = "Centro Comercial",
            pointsAwarded = 100,
            category = "CENTRO_COMERCIAL",
            latitude = 21.9238, //21.9238133898988, -102.29028648159235
            longitude = -102.2902,
            imageResId = R.drawable.place_altaria,
            isVisited = false
        ),
        Place(
            id = "p12",
            title = "Universidad Autonoma de Aguascalientes",
            description = "Universidad",
            pointsAwarded = 200,
            category = "EDUCACION",
            latitude = 21.9102,
            longitude = -102.3153,
            imageResId = R.drawable.place_uaa,
            isVisited = false
        )
    )

    private fun getInitialMissions() = listOf(
        Mission("m1", "Comida Rapida", "Visita el Restaurante Apapacho", 100, 1, 0, R.drawable.ic_explore),
        Mission("m2", "Cultura Clásica", "Visita el Museo Aguascalientes", 30, 1, 0, R.drawable.ic_mission_museum),
        Mission("m3", "Tarde de Juegos con amigos", "Visita Punto y Aparte", 70, 1, 0, R.drawable.ic_mission_foodie)
    )

    private fun getInitialRewards() = listOf(
        Reward("r1", "Sticker Xperience", 30, R.drawable.ic_star_points),
        Reward("r2", "Promocion de Cinepolis", 1000, R.drawable.ic_mission_cinema),
        Reward("r3", "Galleta de Cortesía", 50, R.drawable.ic_mission_foodie),
        Reward("r4", "Topping Extra", 60, R.drawable.ic_mission_foodie),
        Reward("r6", "Experiencia Boca de Tunel todo pagado", 10000, R.drawable.ic_mission_adventure),
        Reward("r7", "Bebida Pequeña", 90, R.drawable.ic_mission_coffee)
    )
}
