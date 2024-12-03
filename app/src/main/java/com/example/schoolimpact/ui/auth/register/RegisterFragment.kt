package com.example.schoolimpact.ui.auth.register

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.schoolimpact.R
import com.example.schoolimpact.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFieldValidation()

        val educationLevelDropdown: AutoCompleteTextView = binding.educationLevelDropdown

        val educationLevels = resources.getStringArray(R.array.education_levels)

        val educationLevelAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, educationLevels
        )

        educationLevelDropdown.setAdapter(educationLevelAdapter)

        binding.btnRegister.setOnClickListener {
            when (val result = validateForm()) {
                is ValidationResult.Success -> performLogin()
                is ValidationResult.Error -> shakeError(
                    binding.cardRegisterForm, result.message
                )
            }
        }

    }

    private fun setupFieldValidation() {

        // Name validation
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()
                if (name.isEmpty()) {
                    binding.nameInputLayout.error = "Name cannot be empty"
                } else {
                    binding.nameInputLayout.error = null
                }
            }

        })

        // Email validation
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

        // Password validation
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

        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        return when {
            name.isEmpty() -> {
                shakeError(binding.etName, "Name cannot be empty")
                ValidationResult.Error("Name cannot be empty")
            }

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
        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
    }


    sealed class ValidationResult {
        data object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }

    override fun onResume() {
        super.onResume()
        binding.etEmail.text.clear()
        binding.etPassword.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}