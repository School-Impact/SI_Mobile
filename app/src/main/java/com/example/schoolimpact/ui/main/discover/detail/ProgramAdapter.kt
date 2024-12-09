package com.example.schoolimpact.ui.main.discover.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolimpact.data.model.Program
import com.example.schoolimpact.databinding.ItemProgramsBinding

// Create Program Adapter
class ProgramAdapter : ListAdapter<Program, ProgramAdapter.ProgramViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val binding = ItemProgramsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProgramViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProgramViewHolder(
        private val binding: ItemProgramsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(program: Program) {
            binding.apply {
                tvProgramName.text = program.name

                val competencyAdapter = CompetencyAdapter()
                rvCompetency.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = competencyAdapter
                    competencyAdapter.submitList(program.competencies.take(3))
                }


            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Program>() {
            override fun areItemsTheSame(oldItem: Program, newItem: Program): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Program, newItem: Program): Boolean {
                return oldItem == newItem
            }
        }
    }
}


