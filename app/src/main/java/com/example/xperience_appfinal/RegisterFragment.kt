package com.example.xperience_appfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.xperience_appfinal.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {
    // Declara ViewBinding
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla el layout para este fragmento
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- AQUÍ VA TU LÓGICA FUTURA ---
        // Por ejemplo, configurar el click listener para el botón de registro
        // binding.btnRegister.setOnClickListener { ... }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}