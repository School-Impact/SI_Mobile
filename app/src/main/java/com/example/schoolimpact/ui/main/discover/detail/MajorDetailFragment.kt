package com.example.schoolimpact.ui.main.discover.detail

import android.annotation.SuppressLint
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolimpact.R
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

    @SuppressLint("InlinedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val majorId = arguments?.getInt("major_id") ?: return
        viewModel.getMajorDetail(majorId)

        setupAdapter()
        observeStates()


        val toolbar = binding.toolbarLayout.toolbar
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                title = getString(R.string.detail_major)
            }
        }
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun setupAdapter() {
        programAdapter = ProgramAdapter()
        binding.rvPrograms.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = programAdapter
        }
        binding.tvMajorDescription.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            }
            textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        }
    }

    private fun observeStates() {
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
                tvMajorName.text = major.name.trim()
                tvMajorDescription.text = major.description
                programAdapter.submitList(major.programs)
            }
        } else {
            showSnackBar("No details available")
        }
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