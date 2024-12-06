package com.example.schoolimpact.ui.main.discover.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.schoolimpact.R
import com.example.schoolimpact.databinding.FragmentMajorDetailBinding

class MajorDetailFragment : Fragment() {

    private var _binding: FragmentMajorDetailBinding? = null
    private val binding get() = _binding


    private lateinit var viewModel: MajorDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMajorDetailBinding.inflate(layoutInflater)
        return inflater.inflate(R.layout.fragment_major_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}