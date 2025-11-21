package com.example.xperience_appfinal

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.xperience_appfinal.databinding.FragmentExploreBinding
import com.example.xperience_appfinal.model.Repository
import kotlinx.coroutines.launch

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: Repository
    private var currentCategoryFilter: String = "ALL"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        repository = (requireActivity().application as XperienceApp).repository
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFilters()
        loadPlaces()
    }

    private fun setupFilters() {
        binding.filterAll.setOnClickListener { updateFilter("ALL", binding.filterAll) }
        binding.filterFood.setOnClickListener { updateFilter("RESTAURANTE", binding.filterFood) }
        binding.filterCulture.setOnClickListener { updateFilter("MUSEO", binding.filterCulture) }
        binding.filterNature.setOnClickListener { updateFilter("NATURALEZA", binding.filterNature) }
    }

    private fun updateFilter(category: String, selectedView: TextView) {
        currentCategoryFilter = category
        val unselectedDrawable = R.drawable.filter_chip_unselected
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.nav_active)

        listOf(binding.filterAll, binding.filterFood, binding.filterCulture, binding.filterNature).forEach {
            it.setBackgroundResource(unselectedDrawable)
            it.setTextColor(unselectedColor)
        }
        selectedView.setBackgroundResource(R.drawable.filter_chip_selected)
        selectedView.setTextColor(Color.WHITE)
        loadPlaces()
    }

    private fun loadPlaces() {
        lifecycleScope.launch {
            binding.exploreContainer.removeAllViews()
            val inflater = LayoutInflater.from(context)
            val allPlaces = repository.getAllPlaces()

            val filteredList = if (currentCategoryFilter == "ALL") {
                allPlaces
            } else {
                allPlaces.filter {
                    if (currentCategoryFilter == "NATURALEZA") {
                        it.category == "NATURALEZA" || it.category == "PARQUE" || it.category == "AVENTURA"
                    } else {
                        it.category == currentCategoryFilter
                    }
                }
            }

            for (place in filteredList) {
                val placeView = inflater.inflate(R.layout.item_place, binding.exploreContainer, false)
                placeView.findViewById<ImageView>(R.id.item_place_image).setImageResource(place.imageResId)
                placeView.findViewById<TextView>(R.id.item_place_title).text = place.title
                placeView.findViewById<TextView>(R.id.item_place_desc).text = place.description
                placeView.findViewById<TextView>(R.id.item_place_points).text = "+ ${place.pointsAwarded} pts"
                placeView.findViewById<TextView>(R.id.item_place_category).text = place.category

                val btnMap = placeView.findViewById<Button>(R.id.item_btn_map)
                val btnCheckin = placeView.findViewById<Button>(R.id.item_btn_checkin)

                btnMap.setOnClickListener {
                    val gmmIntentUri = Uri.parse("geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}(${place.title})")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    try { startActivity(mapIntent) } catch (e: Exception) { }
                }
                btnCheckin.visibility = View.GONE
                binding.exploreContainer.addView(placeView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}