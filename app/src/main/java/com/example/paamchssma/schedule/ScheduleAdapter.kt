package com.example.paamchssma.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.paamchssma.databinding.ItemScheduleBinding

class ScheduleAdapter(
    private val onDelete: (ScheduleEntry) -> Unit
) : ListAdapter<ScheduleEntry, ScheduleAdapter.ScheduleViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScheduleBinding.inflate(inflater, parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScheduleViewHolder(
        private val binding: ItemScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: ScheduleEntry) {
            binding.courseNameTextView.text = entry.courseName
            binding.timeTextView.text = entry.time
            binding.facultyTextView.text = entry.faculty
            binding.roomFloorTextView.text = "${entry.room} - ${entry.floor}"
            binding.deleteButton.setOnClickListener {
                onDelete(entry)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ScheduleEntry>() {
        override fun areItemsTheSame(oldItem: ScheduleEntry, newItem: ScheduleEntry): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ScheduleEntry, newItem: ScheduleEntry): Boolean =
            oldItem == newItem
    }
}

