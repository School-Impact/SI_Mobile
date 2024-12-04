package com.example.schoolimpact.ui.auth.register

import android.animation.ObjectAnimator
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
import com.example.schoolimpact.R
import com.example.schoolimpact.ViewModelFactory
import com.example.schoolimpact.databinding.FragmentRegisterBinding
import com.example.schoolimpact.ui.auth.AuthState
import com.example.schoolimpact.ui.auth.ValidationState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
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
                    registerViewModel.updateEmail(s.toString())
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
                    registerViewModel.updatePassword(s.toString())
                }

            })

            btnRegister.setOnClickListener {
                registerViewModel.register()
            }

        }
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.nameState.collectLatest { state ->
                binding.nameInputLayout.error = when (state) {
                    is ValidationState.Invalid -> state.message
                    else -> null
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.emailState.collectLatest { state ->
                binding.emailInputLayout.error = when (state) {
                    is ValidationState.Invalid -> state.message
                    else -> null
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.passwordState.collectLatest { state ->
                binding.passwordInputLayout.error = when (state) {
                    is ValidationState.Invalid -> state.message
                    else -> null
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.registerState.collectLatest { state ->
                when (state) {
                    is AuthState.Initial -> Unit
                    is AuthState.Loading -> showLoading(true)
                    is AuthState.Success -> {
                        showLoading(false)
                        showSnackBar(getString(R.string.success_login))
                        navigateBackToLogin()
                    }
                    is AuthState.Error -> {
                        showLoading(false)
                        showErrorAnimations(
                            binding.cardRegisterForm, state.error
                        )
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.showErrorAnimation.collectLatest { error ->
                showErrorAnimations(binding.cardRegisterForm, error)
            }
        }
    }

    private fun navigateBackToLogin() {
        findNavController().popBackStack()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
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
        registerViewModel.resetStates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}