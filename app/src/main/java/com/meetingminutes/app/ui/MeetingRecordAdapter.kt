package com.meetingminutes.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meetingminutes.app.data.model.MeetingRecord
import com.meetingminutes.app.databinding.ItemMeetingRecordBinding

/**
 * 会议记录列表适配器
 */
class MeetingRecordAdapter(
    private val onItemClick: (MeetingRecord) -> Unit,
    private val onDeleteClick: (MeetingRecord) -> Unit
) : ListAdapter<MeetingRecord, MeetingRecordAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMeetingRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(
        private val binding: ItemMeetingRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(record: MeetingRecord) {
            binding.apply {
                tvTitle.text = record.title
                tvDate.text = record.getFormattedDate()
                tvDuration.text = record.getFormattedDuration()
                tvPreview.text = record.content.take(100) + 
                    if (record.content.length > 100) "..." else ""
                
                // 显示是否有 AI 总结
                if (record.hasSummary) {
                    chipSummary.visibility = android.view.View.VISIBLE
                } else {
                    chipSummary.visibility = android.view.View.GONE
                }
                
                // 点击事件
                root.setOnClickListener {
                    onItemClick(record)
                }
                
                btnDelete.setOnClickListener {
                    onDeleteClick(record)
                }
            }
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<MeetingRecord>() {
        override fun areItemsTheSame(oldItem: MeetingRecord, newItem: MeetingRecord): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: MeetingRecord, newItem: MeetingRecord): Boolean {
            return oldItem == newItem
        }
    }
}

