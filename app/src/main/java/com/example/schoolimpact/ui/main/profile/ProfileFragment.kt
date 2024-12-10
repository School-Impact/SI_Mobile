package com.example.schoolimpact.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import coil.transform.CircleCropTransformation
import com.example.schoolimpact.R
import com.example.schoolimpact.databinding.FragmentProfileBinding
import com.example.schoolimpact.ui.onboarding.OnboardingActivity
import com.example.schoolimpact.utils.Result
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val contextThemeWrapper = ContextThemeWrapper(requireContext(), R.style.Theme_SchoolImpact)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        _binding = FragmentProfileBinding.inflate(localInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        viewModel.getUserProfile()


        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }

        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfile.collect { result ->
                    when (result) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            showLoading(false)
                            binding.apply {
                                val user = result.data
                                tvUsername.text = user.name
                                tvEmail.text = user.email


                                ivProfile.load(user.image) {
                                    placeholder(R.drawable.ic_profile_placeholder)
                                    error(R.drawable.ic_profile_placeholder)
                                    transformations(CircleCropTransformation())
                                }
                            }
                        }

                        is Result.Error -> {
                            showLoading(false)
                            showSnackBar(result.error)
                        }

                        else -> Unit
                    }
                }
            }
        }
        // Switch theme
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getThemeSettings().collect { isDarkModeActive ->
                    if (isDarkModeActive) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        binding.switchTheme.isChecked = true
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        binding.switchTheme.isChecked = false
                    }
                }
            }
        }
    }

    private fun logout() {
        viewModel.logout()

        val intent = Intent(requireContext(), OnboardingActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
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

    private fun showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}