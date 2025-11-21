package com.example.xperience_appfinal

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.example.xperience_appfinal.databinding.FragmentMissionsBinding
import com.example.xperience_appfinal.model.Repository
import com.example.xperience_appfinal.model.Reward
import kotlinx.coroutines.launch

class MissionsFragment : Fragment() {

    private var _binding: FragmentMissionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: Repository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionsBinding.inflate(inflater, container, false)
        repository = (requireActivity().application as XperienceApp).repository
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePointsDisplay()
        setupTabs()
        loadMissions()
        loadRewards()
    }

    private fun updatePointsDisplay() {
        lifecycleScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                binding.textTotalPuntos.text = String.format("%,d", user.points)
            }
        }
    }

    private fun setupTabs() {
        binding.tabMissions.setOnClickListener { showMissionsTab(true) }
        binding.tabRewards.setOnClickListener { showMissionsTab(false) }
    }

    private fun showMissionsTab(showMissions: Boolean) {
        val activeColor = Color.WHITE
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.text_inactive)

        if (showMissions) {
            binding.layoutMissionsContent.visibility = View.VISIBLE
            binding.layoutRewardsContent.visibility = View.GONE
            binding.tabMissions.setBackgroundResource(R.drawable.tab_selected_bg)
            binding.tabMissions.setTextColor(activeColor)
            binding.tabRewards.setBackgroundResource(android.R.color.transparent)
            binding.tabRewards.setTextColor(inactiveColor)
        } else {
            binding.layoutMissionsContent.visibility = View.GONE
            binding.layoutRewardsContent.visibility = View.VISIBLE
            binding.tabMissions.setBackgroundResource(android.R.color.transparent)
            binding.tabMissions.setTextColor(inactiveColor)
            binding.tabRewards.setBackgroundResource(R.drawable.tab_selected_bg)
            binding.tabRewards.setTextColor(activeColor)
        }
    }

    private fun loadMissions() {
        lifecycleScope.launch {
            binding.missionsListContainer.removeAllViews()
            val inflater = LayoutInflater.from(context)
            val missions = repository.getAllMissions()

            for (mission in missions) {
                val view = inflater.inflate(R.layout.item_mission, binding.missionsListContainer, false)

                view.findViewById<TextView>(R.id.txt_mission_title).text = mission.title
                view.findViewById<TextView>(R.id.txt_mission_desc).text = mission.description
                view.findViewById<TextView>(R.id.txt_mission_points).text = "+ ${mission.pointsReward}"
                view.findViewById<ImageView>(R.id.img_mission_icon).setImageResource(mission.iconResId)

                binding.missionsListContainer.addView(view)
            }
        }
    }

    private fun loadRewards() {
        lifecycleScope.launch {
            binding.rewardsListContainer.removeAllViews()
            val inflater = LayoutInflater.from(context)
            val rewards = repository.getAllRewards()

            for (reward in rewards) {
                val view = inflater.inflate(R.layout.item_reward, binding.rewardsListContainer, false)

                view.findViewById<TextView>(R.id.txt_reward_title).text = reward.title
                view.findViewById<TextView>(R.id.txt_reward_cost).text = "${reward.cost} pts"
                view.findViewById<ImageView>(R.id.img_reward_icon).setImageResource(reward.iconResId)

                val btnRedeem = view.findViewById<Button>(R.id.btn_redeem)
                btnRedeem.setOnClickListener { attemptRedeem(reward) }

                binding.rewardsListContainer.addView(view)
            }
        }
    }

    private fun attemptRedeem(reward: Reward) {
        lifecycleScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                if (user.points >= reward.cost) {
                    user.points -= reward.cost
                    repository.updateUser(user)

                    showSuccessDialog(
                        "¡Canje Exitoso!",
                        "Has canjeado: ${reward.title}.\n\nMuestra esta pantalla al personal para recibir tu recompensa."
                    )
                    updatePointsDisplay() 
                } else {
                    val missing = reward.cost - user.points
                    showErrorDialog(
                        "Puntos Insuficientes",
                        "Te faltan $missing pts para canjear ${reward.title}.\n\n¡Sigue explorando para ganar más!"
                    )
                }
            }
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

    override fun onResume() {
        super.onResume()
        updatePointsDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}