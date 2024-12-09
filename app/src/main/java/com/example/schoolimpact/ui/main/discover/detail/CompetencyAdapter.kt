package com.example.schoolimpact.ui.main.discover.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolimpact.data.model.Competency
import com.example.schoolimpact.databinding.ItemCompetencyBinding

class CompetencyAdapter :
    ListAdapter<Competency, CompetencyAdapter.CompetencyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetencyViewHolder {
        val binding = ItemCompetencyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CompetencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompetencyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CompetencyViewHolder(
        private val binding: ItemCompetencyBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(competency: Competency) {
            binding.tvCompetencyName.text = competency.name
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Competency>() {
            override fun areItemsTheSame(oldItem: Competency, newItem: Competency): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Competency, newItem: Competency): Boolean {
                return oldItem == newItem
            }
        }
    }
}


