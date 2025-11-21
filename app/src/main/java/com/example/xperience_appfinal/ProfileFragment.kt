package com.example.xperience_appfinal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.xperience_appfinal.databinding.FragmentProfileBinding
import com.example.xperience_appfinal.model.Repository
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: Repository

    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val contentResolver = requireActivity().contentResolver
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            lifecycleScope.launch {
                val user = repository.getCurrentUser()
                if (user != null) {
                    user.profileImageUri = uri.toString()
                    repository.updateUser(user)
                    updateProfileUI()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        repository = (requireActivity().application as XperienceApp).repository
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardProfilePic.setOnClickListener {
            pickMedia.launch("image/*")
        }
    }

    private fun updateProfileUI() {
        lifecycleScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                try {
                    if (user.profileImageUri != null) {
                        binding.imgProfilePic.setImageURI(Uri.parse(user.profileImageUri))
                        binding.imgProfilePic.imageTintList = null
                        binding.imgCameraIcon.visibility = View.GONE
                        binding.viewProfileOverlay.visibility = View.GONE
                    } else {
                        binding.imgProfilePic.setImageResource(R.drawable.ic_profile)
                        binding.imgCameraIcon.visibility = View.VISIBLE
                        binding.viewProfileOverlay.visibility = View.VISIBLE
                    }
                } catch (e: SecurityException) {
                    user.profileImageUri = null
                    repository.updateUser(user)
                    binding.imgProfilePic.setImageResource(R.drawable.ic_profile)
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
            }
            loadVisitHistory()
        }
    }

    private suspend fun loadVisitHistory() {
        val currentContext = context ?: return
        binding.historyContainer.removeAllViews()
        val visitedPlaces = repository.getAllPlaces().filter { it.isVisited }
        val inflater = LayoutInflater.from(currentContext)

        if (visitedPlaces.isEmpty()) {
            val emptyView = TextView(currentContext)
            emptyView.text = "Aún no has visitado ningún lugar."
            emptyView.setTextColor(ContextCompat.getColor(currentContext, R.color.text_inactive))
            binding.historyContainer.addView(emptyView)
        } else {
            for (place in visitedPlaces) {
                val historyView = inflater.inflate(R.layout.item_reward, binding.historyContainer, false)
                historyView.findViewById<ImageView>(R.id.img_reward_icon).setImageResource(place.imageResId)
                historyView.findViewById<TextView>(R.id.txt_reward_title).text = place.title
                historyView.findViewById<TextView>(R.id.txt_reward_cost).text = "Visitado"
                historyView.findViewById<TextView>(R.id.txt_reward_cost).setTextColor(ContextCompat.getColor(currentContext, R.color.nav_inactive))
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