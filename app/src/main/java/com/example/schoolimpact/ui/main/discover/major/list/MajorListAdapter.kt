package com.example.schoolimpact.ui.main.discover.major.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolimpact.data.model.ListMajorItem
import com.example.schoolimpact.databinding.ItemMajorBinding


class MajorListAdapter(
    private val onClick: (ListMajorItem) -> Unit
) : RecyclerView.Adapter<MajorListAdapter.MajorViewHolder>() {

    private var majors = listOf<ListMajorItem>()

    fun submitList(newList: List<ListMajorItem>) {
        majors = newList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MajorViewHolder {
        val binding = ItemMajorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MajorViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: MajorViewHolder, position: Int) {
        holder.bind(majors[position])
    }

    override fun getItemCount() = majors.size

    class MajorViewHolder(
        private val binding: ItemMajorBinding,
        private val onClick: (ListMajorItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(majorItem: ListMajorItem) {
            binding.apply {
                tvName.text = majorItem.name
                itemView.setOnClickListener { onClick(majorItem) }
            }
        }
    }

}