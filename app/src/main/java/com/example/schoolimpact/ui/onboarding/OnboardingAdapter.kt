package com.example.schoolimpact.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolimpact.data.model.OnboardingItem
import com.example.schoolimpact.databinding.ItemOnboardingBinding

class OnboardingAdapter :
    ListAdapter<OnboardingItem, OnboardingAdapter.OnboardingViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OnboardingViewHolder(
        private val binding: ItemOnboardingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onboardingItem: OnboardingItem) {
            binding.apply {
//                imageOnboarding.setImageResource(onboardingItem.image)
                textTitle.text = onboardingItem.title
                textDescription.text = onboardingItem.description
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<OnboardingItem>() {
        override fun areItemsTheSame(oldItem: OnboardingItem, newItem: OnboardingItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: OnboardingItem, newItem: OnboardingItem): Boolean {
            return oldItem == newItem
        }
    }
}