package com.example.schoolimpact.ui.auth.login

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.schoolimpact.R
import com.example.schoolimpact.ViewModelFactory
import com.example.schoolimpact.databinding.FragmentLoginBinding
import com.example.schoolimpact.ui.auth.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Initialize real-time validation
        setupRealTimeValidation()

        // Set up button click listeners
        binding.btnLogin.setOnClickListener {
            when (val result = validateForm()) {
                is ValidationResult.Success -> performLogin()
                is ValidationResult.Error -> shakeError(binding.cardLoginForm, result.message)
            }
        }

        binding.btnLogin.setOnClickListener { validateForm() }

        binding.btnToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_registration)
        }

    }

    private fun setupRealTimeValidation() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                when {
                    email.isEmpty() -> binding.emailInputLayout.error = "Email cannot be empty"
                    !isValidEmail(email) -> binding.emailInputLayout.error = "Invalid email format"
                    else -> binding.emailInputLayout.error = null
                }
            }

        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                when {
                    password.isEmpty() -> binding.passwordInputLayout.error =
                        "Password cannot be empty"

                    password.length < 8 -> binding.passwordInputLayout.error =
                        "Password must be at least 8 characters"

                    else -> binding.passwordInputLayout.error = null
                }
            }

        })
    }

    private fun validateForm(): ValidationResult {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        return when {
            email.isEmpty() -> {
                shakeError(binding.etEmail, "Email cannot be empty")
                ValidationResult.Error("Email cannot be empty")
            }

            !isValidEmail(email) -> {
                shakeError(binding.emailInputLayout, "Invalid email format")
                ValidationResult.Error("Invalid email format")
            }

            password.isEmpty() -> {
                shakeError(binding.etPassword, "Password cannot be empty")
                ValidationResult.Error("Password cannot be empty")
            }

            !validatePassword(password) -> {
                shakeError(binding.etPassword, "Password requirements not met")
                ValidationResult.Error("Password requirements not met")
            }

            else -> ValidationResult.Success
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun validatePassword(password: String): Boolean {
        return when {
            password.isEmpty() -> false
            password.length < 8 -> false
            else -> true
        }
    }


    private fun shakeError(view: View, message: String) {
        view.requestFocus()
        val shake = ObjectAnimator.ofFloat(
            view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f
        )
        shake.duration = 500
        shake.start()
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun performLogin() {
        // Perform login action here
        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
    }


    sealed class ValidationResult {
        data object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }

}