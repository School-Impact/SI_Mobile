package com.example.schoolimpact.ui.main.discover.major.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.schoolimpact.ViewModelFactory
import com.example.schoolimpact.databinding.FragmentMajorListBinding

class MajorListFragment : Fragment() {

    private var _binding: FragmentMajorListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MajorViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMajorListBinding.inflate(inflater, container, false)
        return binding.root

        // Set the adapter
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                adapter = MajorListAdapter(PlaceholderContent.ITEMS)
//            }
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()


        arguments?.getString(ARG_CATEGORY)?.let { category ->
            viewModel.fetchMajors(category)
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[MajorViewModel::class.java]
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