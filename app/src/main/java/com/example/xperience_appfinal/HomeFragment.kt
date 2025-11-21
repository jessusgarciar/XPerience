package com.example.xperience_appfinal

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.xperience_appfinal.databinding.FragmentHomeBinding
import com.example.xperience_appfinal.model.MockRepository
import com.example.xperience_appfinal.model.Place

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Launcher para pedir permiso de GPS
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, "Permiso concedido. Intenta hacer Check-in de nuevo.", Toast.LENGTH_SHORT).show()
            } else {
                showErrorDialog("Permiso Requerido", "Necesitamos tu ubicación para validar que estás en el lugar.")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar datos iniciales
        updateUserData()
        loadPlacesList()

        // Funcionalidad del botón "Ver Mapa" (General del encabezado)
        binding.btnViewAllMap.setOnClickListener {
            // Abre el mapa centrado en Aguascalientes
            openGoogleMaps(21.8853, -102.2916, "Lugares en Aguascalientes")
        }

        // Botón Debug (Para pruebas rápidas sin GPS)
        binding.btnDebugAdd.setOnClickListener {
            MockRepository.addPoints(50)
            updateUserData()
            Toast.makeText(context, "+50 Puntos (Debug)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserData() {
        val user = MockRepository.currentUser
        binding.textHomePoints.text = String.format("%,d", user.points)
        binding.textStatVisited.text = user.visitedPlacesCount.toString()
        binding.textStatMissions.text = user.completedMissionsCount.toString()
    }

    private fun loadPlacesList() {
        binding.placesContainer.removeAllViews()
        val places = MockRepository.placesList
        val inflater = LayoutInflater.from(context)

        for (place in places) {
            // Inflar el diseño individual de cada lugar
            val placeView = inflater.inflate(R.layout.item_place, binding.placesContainer, false)

            // Enlazar vistas
            val imageView = placeView.findViewById<ImageView>(R.id.item_place_image)
            val titleView = placeView.findViewById<TextView>(R.id.item_place_title)
            val descView = placeView.findViewById<TextView>(R.id.item_place_desc)
            val pointsView = placeView.findViewById<TextView>(R.id.item_place_points)
            val categoryView = placeView.findViewById<TextView>(R.id.item_place_category)
            val btnCheckin = placeView.findViewById<Button>(R.id.item_btn_checkin)
            val btnMap = placeView.findViewById<Button>(R.id.item_btn_map)

            // Asignar datos del repositorio a la vista
            imageView.setImageResource(place.imageResId) // Imagen real
            titleView.text = place.title
            descView.text = place.description
            pointsView.text = "+ ${place.pointsAwarded} pts"
            categoryView.text = place.category

            // Lógica Botón "Ver Mapa"
            btnMap.setOnClickListener {
                openGoogleMaps(place.latitude, place.longitude, place.title)
            }

            // Lógica Botón "Check-in"
            if (place.isVisited) {
                btnCheckin.text = "Visitado"
                btnCheckin.isEnabled = false
                btnCheckin.alpha = 0.5f
            } else {
                btnCheckin.setOnClickListener {
                    checkInWithGPS(place, btnCheckin)
                }
            }

            // Agregar la tarjeta a la lista
            binding.placesContainer.addView(placeView)
        }
    }

    // Función para abrir Google Maps con las coordenadas
    private fun openGoogleMaps(lat: Double, lon: Double, label: String) {
        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon($label)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: Exception) {
            // Si no tiene la app, abre en navegador
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon"))
            startActivity(browserIntent)
        }
    }

    // Lógica principal de GPS
    private fun checkInWithGPS(place: Place, button: Button) {
        val context = requireContext()

        // 1. Verificar Permisos
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        // 2. Obtener Ubicación
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        val currentLocation = lastKnownLocationGPS ?: lastKnownLocationNetwork

        if (currentLocation != null) {
            // 3. Calcular Distancia
            val distanceInMeters = calculateDistance(
                currentLocation.latitude, currentLocation.longitude,
                place.latitude, place.longitude
            )

            // 4. Validar Proximidad (Radio de 200 metros)
            if (distanceInMeters <= 200) {
                verifySuccess(place, button)
            } else {
                // Mostrar Diálogo de Error si está lejos
                showErrorDialog(
                    "Estás un poco lejos",
                    "El GPS indica que estás a ${distanceInMeters.toInt()}m de distancia.\n\nAcércate más a ${place.title} para hacer check-in."
                )
            }

        } else {
            showErrorDialog("GPS no encontrado", "No pudimos obtener tu ubicación. Asegúrate de tener el GPS encendido.")
        }
    }

    // Matemáticas para calcular distancia
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    // Acción cuando el check-in es exitoso
    private fun verifySuccess(place: Place, button: Button) {
        // Actualizar repositorio
        MockRepository.addPoints(place.pointsAwarded)
        MockRepository.markPlaceAsVisited(place.id)

        // Mostrar Diálogo de Éxito
        showSuccessDialog("¡Check-in Exitoso!", "Has ganado ${place.pointsAwarded} puntos por visitar ${place.title}")

        // Actualizar UI
        updateUserData()
        button.text = "Visitado"
        button.isEnabled = false
        button.alpha = 0.5f
    }

    // Mostrar ventana emergente de Éxito
    private fun showSuccessDialog(title: String, message: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_success, null)

        dialogView.findViewById<TextView>(R.id.dialog_title).text = title
        dialogView.findViewById<TextView>(R.id.dialog_message).text = message

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<Button>(R.id.btn_dialog_ok).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Mostrar ventana emergente de Error
    private fun showErrorDialog(title: String, message: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_error, null)
        dialogView.findViewById<TextView>(R.id.dialog_error_title).text = title
        dialogView.findViewById<TextView>(R.id.dialog_error_message).text = message

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<Button>(R.id.btn_dialog_error_ok).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}