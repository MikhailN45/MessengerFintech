package com.study.messengerfintech.viewmodel.chatRecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.model.data.Message
import com.study.messengerfintech.databinding.ItemMessageBinding

class MessagesAdapter(
    private val dataSet: MutableList<Message>,
    val longClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(viewGroup.context)))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        dataSet[position].let {
            viewHolder.binding.message.setMessage(it)
            viewHolder.binding.message.setMessageOnLongClick {
                longClickListener(position)
            }
        }
    }

    override fun getItemCount() = dataSet.size
}