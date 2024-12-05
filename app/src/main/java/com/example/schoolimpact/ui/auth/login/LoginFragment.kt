package com.example.schoolimpact.ui.auth.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.schoolimpact.MainActivity
import com.example.schoolimpact.R
import com.example.schoolimpact.ViewModelFactory
import com.example.schoolimpact.databinding.FragmentLoginBinding
import com.example.schoolimpact.ui.auth.AuthState
import com.example.schoolimpact.ui.auth.ValidationState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupListeners()
        observeStates()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun setupListeners() {
        with(binding) {
            binding.etEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    loginViewModel.updateEmail(s.toString())
                }
            })

            binding.etPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    loginViewModel.updatePassword(s.toString())
                }

            })

            btnLogin.setOnClickListener {
                loginViewModel.login()
            }

            btnToRegister.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_login_to_navigation_registration)
//                findNavController().navigate(R.id.action_navigation_login_to_navigation_email_verification)
            }
        }
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.emailState.collect { state ->
                binding.emailInputLayout.error = when (state) {
                    is ValidationState.Invalid -> state.message
                    else -> null
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.passwordState.collect { state ->
                binding.passwordInputLayout.error = when (state) {
                    is ValidationState.Invalid -> state.message
                    else -> null
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is AuthState.Initial -> Unit
                    is AuthState.Loading -> showLoading(true)
                    is AuthState.Success -> {
                        showLoading(false)
                        showSnackBar(getString(R.string.success_login))
                        navigateToMain()
                    }

                    is AuthState.Error -> {
                        showLoading(false)
                        showErrorAnimations(
                            binding.cardLoginForm, state.error
                        )
                    }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.showErrorAnimation.collect { error ->
                showErrorAnimations(binding.cardLoginForm, error)
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun showErrorAnimations(view: View, message: String) {
        view.requestFocus()
        val shake = ObjectAnimator.ofFloat(
            view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f
        )
        shake.duration = 500
        shake.start()
        showSnackBar(message)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        loginViewModel.resetStates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}