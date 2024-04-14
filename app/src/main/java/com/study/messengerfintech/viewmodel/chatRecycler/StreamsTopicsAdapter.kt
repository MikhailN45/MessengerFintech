package com.study.messengerfintech.viewmodel.chatRecycler

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.indices
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.databinding.StreamItemBinding
import com.study.messengerfintech.databinding.TopicItemBinding
import com.study.messengerfintech.model.data.StreamItem
import com.study.messengerfintech.model.data.StreamTopicItem
import com.study.messengerfintech.model.data.TopicItem
import com.study.messengerfintech.utils.Utils.colors

const val VIEW_TYPE_STREAM = 0
const val VIEW_TYPE_TOPIC = 1

class StreamsTopicsAdapter(private val onClick: (position: Int) -> Unit) : //todo position -> item
    ListAdapter<StreamTopicItem, StreamTopicsBaseViewHolder>(StreamTopicDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamTopicsBaseViewHolder {
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

    override fun onBindViewHolder(holder: StreamTopicsBaseViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is StreamViewHolder -> {
                holder.bind(item as StreamItem)
                holder.setOnClickListener {
                    if (position >= itemCount) return@setOnClickListener
                    onClick(position)
                    holder.binding.streamExpandButton.let { button ->
                        ObjectAnimator.ofFloat(
                            button, "rotation",
                            if (item.isExpanded) 0f else 180f,
                            if (item.isExpanded) 180f else 0f
                        ).apply {
                            duration = 400
                        }.start()
                    }
                }
            }

            is TopicViewHolder -> {
                holder.bind(item as TopicItem)
                holder.setOnClickListener {
                    if (position >= itemCount) return@setOnClickListener
                    onClick(position)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position) is StreamItem) VIEW_TYPE_STREAM
        else VIEW_TYPE_TOPIC
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

sealed class StreamTopicsBaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

private class StreamViewHolder(val binding: StreamItemBinding) :
    StreamTopicsBaseViewHolder(binding.root) {
    fun bind(item: StreamItem) {
        with(binding) {
            streamItem.visibility = VISIBLE
            streamName.text = item.title
            streamExpandButton.rotation = if (item.isExpanded) 180f else 0f
            progressBar.isVisible = item.isLoading
            streamExpandButton.isVisible = !item.isLoading
        }
    }

    fun setOnClickListener(i: OnClickListener) {
        binding.root.setOnClickListener(i)
    }
}

private class TopicViewHolder(val binding: TopicItemBinding) :
    StreamTopicsBaseViewHolder(binding.root) {
    fun bind(item: TopicItem) {
        with(binding) {
            chatItem.visibility = VISIBLE
            chatTitle.text = item.title
            messagesCount.text =
                item.messageCount.let { if (it > 0) it.toString() else "" }
            for (i in chatItem.indices) {
                chatItem.setBackgroundColor(colors.random())
            }
        }
    }

    fun setOnClickListener(i: OnClickListener) {
        binding.root.setOnClickListener(i)
    }
}

