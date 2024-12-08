package com.example.schoolimpact.ui.main.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolimpact.R
import com.example.schoolimpact.data.model.ListMajorItem
import com.example.schoolimpact.data.model.MlResultData
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

    private fun setupRecyclerView() {
        resultAdapter = ResultAdapter { majorItem ->
            navigateToMajorDetail(majorItem)
        }
        binding.rvListMajor.apply {
            adapter = resultAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showRecommendationResult(result: MlResultData) {
        val majorItem = ListMajorItem(
            id = result.userId,
            name = result.majors,
        )
        resultAdapter.submitList(listOf(majorItem))
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.interestResult.collectLatest { result ->
                when (result) {
                    MlResult.Initial -> Unit
                    is MlResult.Loading -> showLoading(true)
                    is MlResult.Success -> {
                        showLoading(false)
                        showSnackBar(result.message)
                        showRecommendationResult(result.data.mlResult)
                    }

                    is MlResult.Error -> {
                        showLoading(false)
                        showSnackBar(result.error)
                    }

                }
            }
        }
    }

//    private val majorId: Int by lazy {
//        arguments?.getInt("major_id", 0) ?: 0
//    }

    private fun navigateToMajorDetail(majorItem: ListMajorItem) {
        val bundle = Bundle().apply {
            putInt("major_id", majorItem.id ?: 0)
        }
        findNavController().navigate(
            R.id.action_navigation_recommendation_to_navigation_detail_major,
            bundle
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}