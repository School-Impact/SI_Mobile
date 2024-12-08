package com.example.schoolimpact.ui.main.recommendation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolimpact.data.model.ListMajorItem
import com.example.schoolimpact.databinding.ItemResultBinding


class ResultAdapter(
    private val onClick: (ListMajorItem) -> Unit
) : ListAdapter<ListMajorItem, ResultAdapter.MajorViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MajorViewHolder {
        val binding = ItemResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MajorViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: MajorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MajorViewHolder(
        private val binding: ItemResultBinding,
        private val onClick: (ListMajorItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(majorItem: ListMajorItem) {
            binding.apply {
                tvName.text = majorItem.name
                itemView.setOnClickListener { onClick(majorItem) }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListMajorItem>() {
            override fun areItemsTheSame(oldItem: ListMajorItem, newItem: ListMajorItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListMajorItem,
                newItem: ListMajorItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}