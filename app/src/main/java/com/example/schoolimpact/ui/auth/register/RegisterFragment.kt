package com.example.schoolimpact.ui.auth.register

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.schoolimpact.R
import com.example.schoolimpact.databinding.FragmentRegisterBinding
import com.example.schoolimpact.ui.auth.AuthState
import com.example.schoolimpact.ui.auth.ValidationState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeStates()

        val educationLevels = resources.getStringArray(R.array.education_levels)
        val educationLevelAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, educationLevels
        )
        binding.educationLevelDropdown.setAdapter(educationLevelAdapter)

        binding.educationLevelDropdown.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateEducationLevel(educationLevels[position])
        }

    }

    private fun setupListeners() {
        with(binding) {
            setupTextWatcher(etName) { viewModel.updateName(it) }
            setupTextWatcher(etEmail) { viewModel.updateEmail(it) }
            setupTextWatcher(etPhoneNumber) { viewModel.updatePhoneNumber(it) }
            setupTextWatcher(etPassword) { viewModel.updatePassword(it) }

            educationLevelDropdown.setOnItemClickListener { _, _, position, _ ->
                viewModel.updateEducationLevel(
                    educationLevelDropdown.adapter.getItem(
                        position
                    ) as String
                )
            }

            btnSendVerification.setOnClickListener {
                viewModel.verifyEmail()
            }

            btnRegister.setOnClickListener {
                viewModel.register()
            }
        }
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch { viewModel.nameState.collectLatest { handleNameState(it) } }
            launch { viewModel.emailState.collectLatest { handleEmailState(it) } }
            launch { viewModel.educationLevelState.collectLatest { handleEducationLevelState(it) } }
            launch { viewModel.phoneNumberState.collectLatest { handlePhoneNumberState(it) } }
            launch { viewModel.passwordState.collectLatest { handlePasswordState(it) } }
            launch { viewModel.registerState.collectLatest { handleRegisterState(it) } }
            launch { viewModel.verificationState.collectLatest { handleVerificationState(it) } }
            launch { viewModel.showErrorAnimation.collectLatest { handleErrorAnimation(it) } }
        }
    }

    private fun setupTextWatcher(editText: EditText, updateFunction: (String) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateFunction(s.toString())
            }
        })
    }

    private fun handleNameState(state: ValidationState) {
        binding.nameInputLayout.error =
            if (state is ValidationState.Invalid) state.message else null
    }

    private fun handleEmailState(state: ValidationState) {
        binding.emailInputLayout.error =
            if (state is ValidationState.Invalid) state.message else null
    }

    private fun handleEducationLevelState(state: ValidationState) {
        binding.educationLevelDropdownLayout.error =
            if (state is ValidationState.Invalid) state.message else null
    }

    private fun handlePhoneNumberState(state: ValidationState) {
        binding.phoneNumberInputLayout.error =
            if (state is ValidationState.Invalid) state.message else null
    }

    private fun handlePasswordState(state: ValidationState) {
        binding.passwordInputLayout.error =
            if (state is ValidationState.Invalid) state.message else null
    }

    private fun handleRegisterState(state: AuthState<*>) {
        when (state) {
            is AuthState.Initial -> Unit
            is AuthState.Loading -> showLoading(true)
            is AuthState.Success<*> -> {
                showLoading(false)
                showSnackBar(state.message)
                navigateBackToLogin()
            }

            is AuthState.Error -> {
                showLoading(false)
                showSnackBar(state.error)
                showErrorAnimations(binding.cardRegisterForm, state.error)
            }
        }
    }

    private fun handleVerificationState(state: VerificationState) {
        when (state) {
            is VerificationState.Idle -> Unit
            is VerificationState.Loading -> showLoading(true)
            is VerificationState.Success -> {
                showLoading(false)
                showSnackBar(state.message)
            }

            is VerificationState.Error -> {
                showLoading(false)
                showSnackBar(state.error)
            }
        }
    }

    private fun handleErrorAnimation(error: String) {
        showErrorAnimations(binding.cardRegisterForm, error)
    }


    private fun navigateBackToLogin() {
        findNavController().popBackStack()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE

            if (isLoading) {
                loadingAnimation.playAnimation()
                loadingAnimation.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            } else {
                loadingAnimation.pauseAnimation()
            }
        }
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
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetStates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}