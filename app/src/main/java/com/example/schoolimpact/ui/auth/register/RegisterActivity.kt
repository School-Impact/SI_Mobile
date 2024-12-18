package com.example.schoolimpact.ui.auth.register

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.schoolimpact.R
import com.example.schoolimpact.databinding.FragmentRegisterBinding
import com.example.schoolimpact.ui.auth.AuthState
import com.example.schoolimpact.ui.auth.ValidationState
import com.example.schoolimpact.ui.auth.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        observeStates()

        val educationLevels = resources.getStringArray(R.array.education_levels)
        val educationLevelAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, educationLevels
        )
        binding.educationLevelDropdown.setAdapter(educationLevelAdapter)

        binding.educationLevelDropdown.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateEducationLevel(educationLevels[position])
        }
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbarLayout.toolbar
        (this as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                title = getString(R.string.placeholder_register)
                toolbar.navigationIcon?.setTint(
                    ContextCompat.getColor(
                        this@RegisterActivity,
                        R.color.white
                    )
                )
            }
        }
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
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
        lifecycleScope.launch {
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
                navigateToLogin()
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


    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
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
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}