package com.example.schoolimpact.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.schoolimpact.R
import com.example.schoolimpact.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRecommendation.setOnClickListener {
            navigateToRecommendation()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userFirstName.collectLatest { firstName ->
                binding.tvGreeting.text = "${getString(R.string.app_greeting)} $firstName"
            }
        }
    }

    private fun navigateToRecommendation() {
        findNavController().navigate(R.id.action_navigation_home_to_navigation_recommendation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}