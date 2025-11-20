package com.example.xperience_appfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.xperience_appfinal.databinding.FragmentProfileBinding
import com.example.xperience_appfinal.model.MockRepository


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateProfileUI()
    }

    private fun updateProfileUI() {
        val user = MockRepository.currentUser
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