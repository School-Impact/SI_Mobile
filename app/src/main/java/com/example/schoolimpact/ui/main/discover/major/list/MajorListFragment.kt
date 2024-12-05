package com.example.schoolimpact.ui.main.discover.major.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.schoolimpact.R
import com.example.schoolimpact.databinding.FragmentItemListBinding
import com.example.schoolimpact.ui.main.discover.major.list.placeholder.PlaceholderContent

class MajorListFragment : Fragment() {

    private var columnCount = 1

    private lateinit var _binding: FragmentItemListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_major_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MajorListAdapter(PlaceholderContent.ITEMS)
            }
        }
        return view
    }
}