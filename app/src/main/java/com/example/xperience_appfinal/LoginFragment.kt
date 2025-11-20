package com.example.xperience_appfinal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.xperience_appfinal.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pre-fill the email and password fields
        binding.editEmailLogin.setText("test@test.com")
        binding.editPasswordLogin.setText("123456")

        // Set the click listener for the login button
        binding.btnLogin.setOnClickListener {
            val email = binding.editEmailLogin.text.toString()
            val password = binding.editPasswordLogin.text.toString()

            if (email == "test@test.com" && password == "123456") {
                val intent = Intent(requireActivity(), HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}