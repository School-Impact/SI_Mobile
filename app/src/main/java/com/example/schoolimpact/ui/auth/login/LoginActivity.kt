package com.example.schoolimpact.ui.auth.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.schoolimpact.MainActivity
import com.example.schoolimpact.data.preferences.AuthDataSource
import com.example.schoolimpact.databinding.ActivityLoginBinding
import com.example.schoolimpact.ui.auth.AuthState
import com.example.schoolimpact.ui.auth.ValidationState
import com.example.schoolimpact.ui.auth.register.RegisterActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var authDataSource: AuthDataSource


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authDataSource = AuthDataSource(this)
        setupListeners()
        observeStates()
    }

    private fun setupListeners() {
        with(binding) {
            setupTextWatcher(etEmail) { viewModel.updateEmail(it) }
            setupTextWatcher(etPassword) { viewModel.updatePassword(it) }

            btnLogin.setOnClickListener {
                viewModel.login()
            }

            btnToRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }


    private fun observeStates() {
        lifecycleScope.launch {
            launch { viewModel.emailState.collectLatest { handleEmailState(it) } }
            launch { viewModel.passwordState.collectLatest { handlePasswordState(it) } }
            launch { viewModel.loginState.collectLatest { handleLoginState(it) } }
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

    private fun handleEmailState(state: ValidationState) {
        binding.emailInputLayout.error =
            if (state is ValidationState.Invalid) state.message else null

    }

    private fun handlePasswordState(state: ValidationState) {
        binding.passwordInputLayout.error =
            if (state is ValidationState.Invalid) state.message else null
    }

    private fun handleLoginState(state: AuthState<*>) {
        when (state) {
            is AuthState.Initial -> Unit
            is AuthState.Loading -> showLoading(true)
            is AuthState.Success<*> -> {
                showLoading(false)
                lifecycleScope.launch {
                    authDataSource.saveUser(state.user)
                }
                showSnackBar(state.message)
                navigateToMain()
            }

            is AuthState.Error -> {
                showLoading(false)
                showErrorAnimations(binding.cardLoginForm, state.error)
            }
        }
    }

    private fun handleErrorAnimation(error: String) {
        showErrorAnimations(binding.cardLoginForm, error)
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE

            if (isLoading) {
                loadingAnimation.playAnimation()
                loadingAnimation.animate().alpha(1f).setDuration(200).start()
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

    override fun onResume() {
        super.onResume()
        viewModel.resetStates()
    }
}