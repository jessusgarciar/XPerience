package com.example.xperience_appfinal

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.xperience_appfinal.databinding.FragmentProfileBinding
import com.example.xperience_appfinal.model.MockRepository

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // 1. Crear el lanzador para seleccionar contenido (Imágenes)
    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Este bloque se ejecuta cuando el usuario selecciona una foto
        if (uri != null) {
            // Mostrar la imagen en el ImageView
            binding.imgProfilePic.setImageURI(uri)

            // Quitar el tinte gris para que se vean los colores de la foto
            binding.imgProfilePic.imageTintList = null

            // Guardar la URI en el repositorio (temporalmente en memoria)
            MockRepository.currentUser.profileImageUri = uri.toString()
            
            // Ocultar el icono de la cámara y el overlay
            binding.imgCameraIcon.visibility = View.GONE
            binding.viewProfileOverlay.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. Configurar el Click Listener en la foto de perfil
        binding.cardProfilePic.setOnClickListener {
            // Lanzar el selector de imágenes (tipo MIME "image/*")
            pickMedia.launch("image/*")
        }

        updateProfileUI()
    }

    private fun updateProfileUI() {
        val user = MockRepository.currentUser

        // Cargar foto de perfil si existe
        if (user.profileImageUri != null) {
            binding.imgProfilePic.setImageURI(Uri.parse(user.profileImageUri))
            binding.imgProfilePic.imageTintList = null // Quitar tinte si hay foto
            binding.imgCameraIcon.visibility = View.GONE
            binding.viewProfileOverlay.visibility = View.GONE
        } else {
            binding.imgProfilePic.setImageResource(R.drawable.ic_profile)
            // Opcional: Restaurar tinte si quieres que el placeholder se vea gris
            // binding.imgProfilePic.setColorFilter(Color.LTGRAY)
            binding.imgCameraIcon.visibility = View.VISIBLE
            binding.viewProfileOverlay.visibility = View.VISIBLE
        }

        binding.profileName.text = user.name
        binding.profileLevel.text = "Nivel ${user.level}"
        binding.textPointsHeader.text = "${user.points} pts"
        binding.textStatTotalPoints.text = "${user.points}"

        val progress = user.points % 1000
        binding.levelProgress.progress = progress
        binding.textPlacesVisitedCount.text = "${user.visitedPlacesCount}/50"
        binding.progressPlaces.progress = user.visitedPlacesCount

        loadVisitHistory()
    }

    private fun loadVisitHistory() {
        binding.historyContainer.removeAllViews()
        val visitedPlaces = MockRepository.placesList.filter { it.isVisited }
        val inflater = LayoutInflater.from(context)

        if (visitedPlaces.isEmpty()) {
            val emptyView = TextView(context)
            emptyView.text = "Aún no has visitado ningún lugar."
            emptyView.setTextColor(resources.getColor(R.color.text_inactive, null))
            binding.historyContainer.addView(emptyView)
        } else {
            for (place in visitedPlaces) {
                val historyView = inflater.inflate(R.layout.item_reward, binding.historyContainer, false)
                historyView.findViewById<android.widget.ImageView>(R.id.img_reward_icon).setImageResource(place.imageResId)
                historyView.findViewById<TextView>(R.id.txt_reward_title).text = place.title
                historyView.findViewById<TextView>(R.id.txt_reward_cost).text = "Visitado"
                historyView.findViewById<TextView>(R.id.txt_reward_cost).setTextColor(resources.getColor(R.color.nav_inactive, null))
                historyView.findViewById<View>(R.id.btn_redeem).visibility = View.GONE
                binding.historyContainer.addView(historyView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfileUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}