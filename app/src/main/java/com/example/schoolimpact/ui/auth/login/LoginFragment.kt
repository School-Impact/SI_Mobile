package com.example.schoolimpact.ui.auth.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.schoolimpact.MainActivity
import com.example.schoolimpact.R
import com.example.schoolimpact.ViewModelFactory
import com.example.schoolimpact.databinding.FragmentLoginBinding
import com.example.schoolimpact.ui.auth.AuthViewModel
import com.example.schoolimpact.ui.auth.ValidationResult
import com.example.schoolimpact.utils.Result
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

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

        setupFieldValidation()

        val userEmail = binding.etEmail.text.toString()
        val userPassword = binding.etPassword.text.toString()

        binding.btnLogin.setOnClickListener {
            when (val result = validateForm(userEmail, userPassword)) {
                is ValidationResult.Success -> login(userEmail, userPassword)
                is ValidationResult.Error -> showValidationErrorWithAnimation(
                    binding.cardLoginForm, result.message
                )
            }
        }

        binding.btnLogin.setOnClickListener { validateForm(userEmail, userPassword) }

        binding.btnToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_registration)
        }

    }

    override fun onResume() {
        super.onResume()
        binding.etEmail.text.clear()
        binding.etPassword.text.clear()
        clearValidationErrors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupFieldValidation() {
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

    private fun validateForm(email: String, password: String): ValidationResult {

        return when {
            email.isEmpty() -> {
                showValidationErrorWithAnimation(binding.etEmail, "Email cannot be empty")
                ValidationResult.Error("Email cannot be empty")
            }

            !isValidEmail(email) -> {
                showValidationErrorWithAnimation(binding.emailInputLayout, "Invalid email format")
                ValidationResult.Error("Invalid email format")
            }

            password.isEmpty() -> {
                showValidationErrorWithAnimation(binding.etPassword, "Password cannot be empty")
                ValidationResult.Error("Password cannot be empty")
            }

            !validatePassword(password) -> {
                showValidationErrorWithAnimation(
                    binding.etPassword, "Password requirements not met"
                )
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

    // Shake Animations
    private fun showValidationErrorWithAnimation(view: View, message: String) {
        view.requestFocus()
        val shake = ObjectAnimator.ofFloat(
            view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f
        )
        shake.duration = 500
        shake.start()
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun login(email: String, password: String) {
        authViewModel.login(email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val user = result.data

                        showSnackBar(getString(R.string.success_login))
                        lifecycleScope.launch {
                            authViewModel.saveUser(user)
                        }

                        // Navigate to MainActivity
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    }

                    is Result.Error -> {
                        showLoading(false)
                        showSnackBar("Error: ${result.error}")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun clearValidationErrors() {
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
    }

}