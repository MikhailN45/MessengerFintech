package com.study.messengerfintech.presentation.adapters

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.indices
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.databinding.StreamItemBinding
import com.study.messengerfintech.databinding.TopicItemBinding
import com.study.messengerfintech.domain.model.StreamItem
import com.study.messengerfintech.domain.model.StreamTopicItem
import com.study.messengerfintech.domain.model.TopicItem
import com.study.messengerfintech.utils.Utils.colors

class StreamsTopicsAdapter(private val onClick: (item: StreamTopicItem) -> Unit) :
    ListAdapter<StreamTopicItem, RecyclerView.ViewHolder>(StreamTopicDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_STREAM -> {
                val binding = StreamItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                StreamViewHolder(binding)
            }

            else -> {
                val binding = TopicItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                TopicViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is StreamViewHolder -> {
                holder.bind(item as StreamItem)
                holder.setOnClickListener {
                    if (position >= itemCount) return@setOnClickListener
                    onClick(item)
                    holder.binding.streamExpandButton.let { button ->
                        ObjectAnimator.ofFloat(
                            button, "rotation",
                            if (item.isExpanded) 0f
                            else 180f,
                            if (item.isExpanded) 180f
                            else 0f
                        ).apply {
                            duration = 500
                        }.start()
                    }
                }
            }

            is TopicViewHolder -> {
                holder.bind(item as TopicItem)
                holder.setOnClickListener {
                    if (position >= itemCount) return@setOnClickListener
                    onClick(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is StreamItem -> VIEW_TYPE_STREAM
            is TopicItem -> VIEW_TYPE_TOPIC
        }

    inner class StreamViewHolder(val binding: StreamItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StreamItem) {
            with(binding) {
                streamItem.visibility = VISIBLE
                streamName.text = item.title
                streamExpandButton.rotation =
                    if (item.isExpanded) 180f
                    else 0f
            }
        }

        fun setOnClickListener(i: View.OnClickListener) {
            binding.root.setOnClickListener(i)
        }
    }

    inner class TopicViewHolder(val binding: TopicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TopicItem) {
            with(binding) {
                chatItem.visibility = VISIBLE
                chatTitle.text = item.title
                messagesCount.text =
                    item.messageCount.let {
                        if (it > 0) it.toString()
                        else ""
                    }
                for (i in chatItem.indices) {
                    chatItem.setBackgroundColor(colors.random())
                }
            }
        }

        fun setOnClickListener(i: View.OnClickListener) {
            binding.root.setOnClickListener(i)
        }
    }

    companion object {
        const val VIEW_TYPE_STREAM = 0
        const val VIEW_TYPE_TOPIC = 1
    }
}

class StreamTopicDiffUtilCallback : DiffUtil.ItemCallback<StreamTopicItem>() {
    override fun areItemsTheSame(oldItem: StreamTopicItem, newItem: StreamTopicItem): Boolean {
        if (oldItem is StreamItem && newItem is StreamItem) return oldItem.streamId == newItem.streamId
        if (oldItem is TopicItem && newItem is TopicItem) return oldItem.topicId == newItem.topicId
        return false
    }

    override fun areContentsTheSame(oldItem: StreamTopicItem, newItem: StreamTopicItem): Boolean {
        return oldItem == newItem
    }
}