package com.study.messengerfintech.viewmodel.chatRecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.utils.OnEmojiClick
import com.study.messengerfintech.databinding.ItemMessageBinding
import com.study.messengerfintech.domain.data.Message
import com.study.messengerfintech.utils.Utils.getDate

class MessagesAdapter(
    private val dataSet: MutableList<Message>, //todo remove and use diffUtils
    val emojiClickListener: (OnEmojiClick) -> Unit,
    val longClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

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
        dataSet[position].apply {
            viewHolder.binding.message.setMessage(this)
            viewHolder.binding.message.setOnEmojiClickListener(emojiClickListener)            
            viewHolder.binding.date.text = getDate(dataSet[position].timestamp)
            viewHolder.binding.date.isVisible = isDateNeeded(position)
            viewHolder.binding.message.setMessageOnLongClick {
                longClickListener(position)
            }
        }
    }

    private fun isDateNeeded(position: Int): Boolean {
        if (position == 0) return true
        val today = dataSet[position].timestamp / SECONDS_IN_DAY
        val yesterday = dataSet[position - 1].timestamp / SECONDS_IN_DAY
        return yesterday < today
    }

    override fun getItemCount() = dataSet.size

    companion object {
        const val SECONDS_IN_DAY = 86400
    }
}