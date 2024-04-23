package com.study.messengerfintech.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.databinding.ItemMessageBinding
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.utils.OnEmojiClick
import com.study.messengerfintech.utils.Utils.getDate
import com.study.messengerfintech.utils.Utils.getDayCount

class MessagesAdapter(
    val onEmojiClick: (OnEmojiClick) -> Unit,
    val onLongClick: (position: Int) -> Unit
) : ListAdapter<Message, MessagesAdapter.ViewHolder>(MessageDiffUtilCallback()) {

    inner class ViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMessageBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getItem(position).apply {
            viewHolder.binding.message.setMessage(this)
            viewHolder.binding.message.setOnEmojiClickListener(onEmojiClick)
            viewHolder.binding.message.setMessageOnLongClick { onLongClick(position) }
            viewHolder.binding.date.text = getDate(getItem(position).timestamp)
            viewHolder.binding.date.isVisible = isDateNeeded(position)
        }
    }

    private fun isDateNeeded(position: Int): Boolean {
        if (position + 1 == itemCount) return true
        val yesterday = getItem(position + 1).timestamp
        val today = getItem(position).timestamp
        return getDayCount(yesterday) < getDayCount(today)
    }
}

class MessageDiffUtilCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem && (oldItem.emojiCodeReactionMap == newItem.emojiCodeReactionMap)
    }
}