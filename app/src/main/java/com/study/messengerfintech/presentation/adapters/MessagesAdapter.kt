package com.study.messengerfintech.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.databinding.ItemMessageBinding
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.utils.EmojiAdd
import com.study.messengerfintech.utils.EmojiDelete
import com.study.messengerfintech.utils.Utils.getDate
import com.study.messengerfintech.utils.Utils.getDayCount

class MessagesAdapter(
    val onEmojiAddClick: (messageId: Int, emojiName: String) -> Unit,
    val onEmojiDeleteClick: (messageId: Int, emojiName: String) -> Unit,
    val onMessageLongClick: (position: Int) -> Unit
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
            viewHolder.binding.message.setOnEmojiClickListener { event ->
                when (event) {
                    is EmojiAdd -> onEmojiAddClick(event.messageId, event.emojiName)
                    is EmojiDelete -> onEmojiDeleteClick(event.messageId, event.emojiName)
                }
            }
            viewHolder.binding.message.setMessageOnLongClick { onMessageLongClick(position) }
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