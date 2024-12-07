package com.example.schoolimpact.ui.main.discover.major

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolimpact.R
import com.example.schoolimpact.ViewModelFactory
import com.example.schoolimpact.data.model.ListMajorItem
import com.example.schoolimpact.databinding.FragmentMajorListBinding
import com.example.schoolimpact.ui.main.discover.DiscoverViewModel
import com.example.schoolimpact.utils.Result
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MajorListFragment : Fragment() {

    private var _binding: FragmentMajorListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DiscoverViewModel
    private lateinit var majorListAdapter: MajorListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMajorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()
        observeStates()

        arguments?.getString(ARG_CATEGORY)?.let { category ->
            viewModel.fetchMajors(category)
        }

    }

    private fun setupRecyclerView() {
        binding.rvListMajor.apply {
            majorListAdapter = MajorListAdapter { majorItem ->
                navigateToMajorDetail(majorItem)
            }
            layoutManager = LinearLayoutManager(requireContext())
            adapter = majorListAdapter
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[DiscoverViewModel::class.java]
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.majors.collectLatest { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        majorListAdapter.submitList(result.data)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showSnackBar(result.error)
                    }
                }
            }
        }
    }

    private fun navigateToMajorDetail(majorItem: ListMajorItem) {
        val bundle = Bundle().apply {
            putInt("major_id", majorItem.id ?: 0)
        }
        findNavController().navigate(
            R.id.action_navigation_discover_to_navigation_detail_major,
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

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String) = MajorListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY, category)
            }
        }
    }
}