package com.example.schoolimpact.ui.auth.emailVerification

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.schoolimpact.ViewModelFactory
import com.example.schoolimpact.databinding.FragmentEmailVerificationBinding
import com.example.schoolimpact.ui.auth.ValidationState
import com.example.schoolimpact.ui.auth.register.VerificationState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EmailVerificationFragment : Fragment() {

    private var _binding: FragmentEmailVerificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EmailVerificationViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
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
        viewModel = ViewModelProvider(this, factory)[EmailVerificationViewModel::class.java]
    }

    private fun setupListeners() {
        with(binding) {
            etEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    viewModel.updateEmail(s.toString())
                }

            })
                btnSendVerification.setOnClickListener {
                    viewModel.verifyEmail()
                }
        }
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.emailState.collectLatest { state ->
                binding.emailInputLayout.error = when (state) {
                    is ValidationState.Invalid -> state.message
                    else -> null
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.verificationState.collectLatest { state ->
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
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }


}