package com.example.schoolimpact.ui.main.discover.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolimpact.data.model.MajorDetailItem
import com.example.schoolimpact.databinding.FragmentMajorDetailBinding
import com.example.schoolimpact.utils.Result
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MajorDetailFragment : Fragment() {

    private var _binding: FragmentMajorDetailBinding? = null
    private val binding get() = _binding!!


    private val viewModel: MajorDetailViewModel by viewModels()
    private lateinit var programAdapter: ProgramAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMajorDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val majorId = arguments?.getInt("major_id") ?: return
        viewModel.getMajorDetail(majorId)

        programAdapter = ProgramAdapter()
        binding.rvPrograms.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = programAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.majorDetail.collect { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        showMajorDetail(result.data)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showSnackBar(result.error)
                    }

                }
            }
        }

    }


    private fun showMajorDetail(majors: List<MajorDetailItem>) {
        if (majors.isNotEmpty()) {
            val major = majors.first()

            binding.apply {
                tvMajorName.text = major.name
                tvMajorDescription.text = major.description
                programAdapter.submitList(major.programs)
            }
        } else {
            showSnackBar("No details available")
        }
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