package com.example.schoolimpact.ui.main.discover.major.list

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MajorPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val categories = listOf("SMA", "SMK")

    override fun getItemCount(): Int = categories.size

    override fun createFragment(position: Int): Fragment {
        return MajorListFragment.newInstance(categories[position])
    }
}