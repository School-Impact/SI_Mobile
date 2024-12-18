package com.example.schoolimpact.ui.main.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.schoolimpact.R
import com.example.schoolimpact.data.model.ListMajorItem
import com.example.schoolimpact.databinding.FragmentRecommendationBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendationFragment : Fragment() {

    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!

    private lateinit var resultAdapter: ResultAdapter

    private val viewModel: RecommendationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        with(binding) {
            btnResult.setOnClickListener {
                val interest = etEssay.text.toString().trim()
                if (interest.isBlank()) {
                    essayInputLayout.error = "Interest data is required!"
                } else {
                    essayInputLayout.error = null
                    viewModel.postInterest(interest)
                }

            }
        }
    }


    private fun setupToolbar() {
        val toolbar = binding.toolbarLayout.toolbar
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                title = getString(R.string.school_recommendation)
                toolbar.navigationIcon?.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }
        }
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        resultAdapter = ResultAdapter { majorItem ->
            navigateToMajorDetail(majorItem.id ?: 0)
        }
        binding.rvListMajor.apply {
            adapter = resultAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.interestResult.collectLatest { result ->
                    when (result) {
                        MlResult.Initial -> Unit
                        is MlResult.Loading -> showLoading(true)
                        is MlResult.Success -> {
                            showLoading(false)
                            showSnackBar(result.message)
                        }

                        is MlResult.Error -> {
                            showLoading(false)
                            showSnackBar(result.error)
                        }

                    }
                }
            }
            launch {
                viewModel.recommendationHistory.collectLatest { history ->
                    showLoading(true)
                    val items = history.map { entity ->
                        ListMajorItem(
                            id = entity.majorId,
                            name = entity.majors
                        )
                    }
                    resultAdapter.submitList(items)
                    showLoading(false)
                }
            }
        }
    }

    private fun navigateToMajorDetail(majorId: Int) {
        val bundle = Bundle().apply {
            putInt("major_id", majorId)
        }
        findNavController().navigate(
            R.id.action_navigation_recommendation_to_navigation_detail_major,
            bundle
        )
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
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}