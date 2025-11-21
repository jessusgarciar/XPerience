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
import androidx.lifecycle.lifecycleScope
import com.example.xperience_appfinal.databinding.FragmentHomeBinding
import com.example.xperience_appfinal.model.Place
import com.example.xperience_appfinal.model.Repository
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: Repository

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
        repository = (requireActivity().application as XperienceApp).repository
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
            lifecycleScope.launch {
                val user = repository.getCurrentUser()
                if (user != null) {
                    user.points += 50
                    repository.updateUser(user)
                    updateUserData()
                    Toast.makeText(context, "+50 Puntos (Debug)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserData() {
        lifecycleScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                binding.textHomePoints.text = String.format("%,d", user.points)
                binding.textStatVisited.text = user.visitedPlacesCount.toString()
                binding.textStatMissions.text = user.completedMissionsCount.toString()
            }
        }
    }

    private fun loadPlacesList() {
        lifecycleScope.launch {
            binding.placesContainer.removeAllViews()
            val places = repository.getAllPlaces()
            val inflater = LayoutInflater.from(context)

            for (place in places) {
                val placeView = inflater.inflate(R.layout.item_place, binding.placesContainer, false)
                val imageView = placeView.findViewById<ImageView>(R.id.item_place_image)
                val titleView = placeView.findViewById<TextView>(R.id.item_place_title)
                val descView = placeView.findViewById<TextView>(R.id.item_place_desc)
                val pointsView = placeView.findViewById<TextView>(R.id.item_place_points)
                val categoryView = placeView.findViewById<TextView>(R.id.item_place_category)
                val btnCheckin = placeView.findViewById<Button>(R.id.item_btn_checkin)
                val btnMap = placeView.findViewById<Button>(R.id.item_btn_map)

                imageView.setImageResource(place.imageResId)
                titleView.text = place.title
                descView.text = place.description
                pointsView.text = "+ ${place.pointsAwarded} pts"
                categoryView.text = place.category

                btnMap.setOnClickListener {
                    openGoogleMaps(place.latitude, place.longitude, place.title)
                }

                if (place.isVisited) {
                    btnCheckin.text = "Visitado"
                    btnCheckin.isEnabled = false
                    btnCheckin.alpha = 0.5f
                } else {
                    btnCheckin.setOnClickListener {
                        checkInWithGPS(place, btnCheckin)
                    }
                }
                binding.placesContainer.addView(placeView)
            }
        }
    }

    private fun openGoogleMaps(lat: Double, lon: Double, label: String) {
        val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon($label)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: Exception) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon"))
            startActivity(browserIntent)
        }
    }

    private fun checkInWithGPS(place: Place, button: Button) {
        val context = requireContext()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val currentLocation = lastKnownLocationGPS ?: lastKnownLocationNetwork

        if (currentLocation != null) {
            val distanceInMeters = calculateDistance(
                currentLocation.latitude, currentLocation.longitude,
                place.latitude, place.longitude
            )

            if (distanceInMeters <= 200) {
                verifySuccess(place, button)
            } else {
                showErrorDialog(
                    "Estás un poco lejos",
                    "El GPS indica que estás a ${distanceInMeters.toInt()}m de distancia.\n\nAcércate más a ${place.title} para hacer check-in."
                )
            }
        } else {
            showErrorDialog("GPS no encontrado", "No pudimos obtener tu ubicación. Asegúrate de tener el GPS encendido.")
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun verifySuccess(place: Place, button: Button) {
        lifecycleScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                user.points += place.pointsAwarded
                user.visitedPlacesCount++
                repository.updateUser(user)
            }

            place.isVisited = true
            repository.updatePlace(place)

            showSuccessDialog("¡Check-in Exitoso!", "Has ganado ${place.pointsAwarded} puntos por visitar ${place.title}")

            updateUserData()
            button.text = "Visitado"
            button.isEnabled = false
            button.alpha = 0.5f
        }
    }

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