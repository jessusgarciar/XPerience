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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.xperience_appfinal.databinding.FragmentMissionsBinding
import com.example.xperience_appfinal.model.MockRepository
import com.example.xperience_appfinal.model.Reward


class MissionsFragment : Fragment() {
    private var _binding: FragmentMissionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMissionsBinding.inflate(inflater, container, false)
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
        val points = MockRepository.currentUser.points
        binding.textTotalPuntos.text = String.format("%,d", points)
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
        binding.missionsListContainer.removeAllViews()
        val inflater = LayoutInflater.from(context)
        for (mission in MockRepository.missionsList) {
            val view = inflater.inflate(R.layout.item_mission, binding.missionsListContainer, false)
            view.findViewById<TextView>(R.id.txt_mission_title).text = mission.title
            view.findViewById<TextView>(R.id.txt_mission_desc).text = mission.description
            view.findViewById<TextView>(R.id.txt_mission_points).text = "+ ${mission.pointsReward}"
            view.findViewById<ImageView>(R.id.img_mission_icon).setImageResource(mission.iconResId)
            binding.missionsListContainer.addView(view)
        }
    }

    private fun loadRewards() {
        binding.rewardsListContainer.removeAllViews()
        val inflater = LayoutInflater.from(context)
        for (reward in MockRepository.rewardsList) {
            val view = inflater.inflate(R.layout.item_reward, binding.rewardsListContainer, false)
            view.findViewById<TextView>(R.id.txt_reward_title).text = reward.title
            view.findViewById<TextView>(R.id.txt_reward_cost).text = "${reward.cost} pts"
            view.findViewById<ImageView>(R.id.img_reward_icon).setImageResource(reward.iconResId)
            val btnRedeem = view.findViewById<Button>(R.id.btn_redeem)
            btnRedeem.setOnClickListener { attemptRedeem(reward) }
            binding.rewardsListContainer.addView(view)
        }
    }

    private fun attemptRedeem(reward: Reward) {
        if (MockRepository.redeemPoints(reward.cost)) {
            showSuccessDialog("¡Canje Exitoso!", "Has canjeado: ${reward.title}. Muestra esta pantalla.")
            updatePointsDisplay()
        } else {
            val missing = reward.cost - MockRepository.currentUser.points
            Toast.makeText(context, "Te faltan $missing pts para ${reward.title}", Toast.LENGTH_SHORT).show()
        }
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

    override fun onResume() {
        super.onResume()
        updatePointsDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}