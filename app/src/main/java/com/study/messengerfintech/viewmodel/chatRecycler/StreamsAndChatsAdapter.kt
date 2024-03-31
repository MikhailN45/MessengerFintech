package com.study.messengerfintech.viewmodel.chatRecycler

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.indices
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.databinding.StreamsAndChatsItemBinding
import com.study.messengerfintech.model.data.ChatItem
import com.study.messengerfintech.model.data.StreamAndChatItem
import com.study.messengerfintech.model.data.StreamItem
import com.study.messengerfintech.model.utils.Utils.colors

class StreamsAndChatsAdapter(
    private val dataSet: MutableList<StreamAndChatItem>,
    private val onClick: (position: Int) -> Unit
) : RecyclerView.Adapter<StreamsAndChatsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: StreamsAndChatsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StreamsAndChatsItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        when (item) {
            is StreamItem -> holder.apply {
                binding.chatItem.visibility = GONE
                binding.streamItem.visibility = VISIBLE
                binding.streamName.text = item.title
            }

            is ChatItem -> holder.apply {
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

        holder.binding.root.setOnClickListener {
            if (position >= itemCount) return@setOnClickListener
            onClick(position)
            if (item is StreamItem) holder.binding.streamExpandButton.let { button ->
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

    override fun getItemCount(): Int = dataSet.size
}
