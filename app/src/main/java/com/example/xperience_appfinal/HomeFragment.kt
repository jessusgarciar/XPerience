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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, "Permiso concedido. Intenta hacer Check-in de nuevo.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Se requiere permiso de ubicación para validar el Check-in.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUserData()
        loadPlacesList()
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
            val placeView = inflater.inflate(R.layout.item_place, binding.placesContainer, false)

            placeView.findViewById<ImageView>(R.id.item_place_image).setImageResource(place.imageResId)
            placeView.findViewById<TextView>(R.id.item_place_title).text = place.title
            placeView.findViewById<TextView>(R.id.item_place_desc).text = place.description
            placeView.findViewById<TextView>(R.id.item_place_points).text = "+ ${place.pointsAwarded} pts"
            placeView.findViewById<TextView>(R.id.item_place_category).text = place.category

            val btnCheckin = placeView.findViewById<Button>(R.id.item_btn_checkin)
            val btnMap = placeView.findViewById<Button>(R.id.item_btn_map)

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
            val distanceInMeters = calculateDistance(currentLocation.latitude, currentLocation.longitude, place.latitude, place.longitude)
            if (distanceInMeters <= 150) {
                verifySuccess(place, button)
            } else {
                Toast.makeText(context, "Estás a ${distanceInMeters.toInt()}m. Acércate más a ${place.title}.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "No se pudo obtener tu ubicación. Enciende el GPS.", Toast.LENGTH_LONG).show()
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun verifySuccess(place: Place, button: Button) {
        MockRepository.addPoints(place.pointsAwarded)
        MockRepository.markPlaceAsVisited(place.id)
        showSuccessDialog("¡Check-in Exitoso!", "Has ganado ${place.pointsAwarded} puntos por visitar ${place.title}")
        updateUserData()
        button.text = "Visitado"
        button.isEnabled = false
        button.alpha = 0.5f
    }

    private fun showSuccessDialog(title: String, message: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_success, null)
        dialogView.findViewById<TextView>(R.id.dialog_title).text = title
        dialogView.findViewById<TextView>(R.id.dialog_message).text = message
        val dialog = AlertDialog.Builder(context).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.findViewById<Button>(R.id.btn_dialog_ok).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}