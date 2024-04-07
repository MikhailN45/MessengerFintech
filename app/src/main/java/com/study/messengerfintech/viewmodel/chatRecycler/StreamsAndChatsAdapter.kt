package com.study.messengerfintech.viewmodel.chatRecycler

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.indices
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.databinding.StreamsAndChatsItemBinding
import com.study.messengerfintech.model.data.ChatItem
import com.study.messengerfintech.model.data.StreamAndChatItem
import com.study.messengerfintech.model.data.StreamItem
import com.study.messengerfintech.utils.Utils.colors

class StreamsAndChatsAdapter(private val onClick: (position: Int) -> Unit) :
    ListAdapter<StreamAndChatItem, StreamsAndChatsAdapter.ViewHolder>(StreamAndChatDiffUtilCallback()) {

    inner class ViewHolder(private val binding: StreamsAndChatsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val streamExpandButton by lazy { binding.streamExpandButton }

        fun bind(item: StreamAndChatItem) {
            when (item) {
                is StreamItem -> item.apply {
                    with(binding) {
                        chatItem.visibility = GONE
                        streamItem.visibility = VISIBLE
                        streamName.text = item.title
                        streamExpandButton.rotation = if (item.isExpanded) 180f else 0f
                        progressBar.isVisible = item.isLoading
                        streamExpandButton.isVisible = !item.isLoading
                    }
                }

                is ChatItem -> item.apply {
                    with(binding) {
                        chatItem.visibility = VISIBLE
                        streamItem.visibility = GONE
                        chatTitle.text = item.title
                        messagesCount.text = item.messageCount.toString()
                        for (i in chatItem.indices) {
                            chatItem.setBackgroundColor(colors.random())
                        }
                    }
                }
            }
        }

        fun setOnClickListener(i: OnClickListener) {
            binding.root.setOnClickListener(i)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            StreamsAndChatsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.setOnClickListener {
            if (position >= itemCount) return@setOnClickListener
            onClick(position)
            if (item is StreamItem) holder.streamExpandButton.let { button ->
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
}

class StreamAndChatDiffUtilCallback : DiffUtil.ItemCallback<StreamAndChatItem>() {
    override fun areItemsTheSame(oldItem: StreamAndChatItem, newItem: StreamAndChatItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: StreamAndChatItem, newItem: StreamAndChatItem): Boolean {
        return oldItem == newItem
    }
}
