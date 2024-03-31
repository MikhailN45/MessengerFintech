package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.R
import com.study.messengerfintech.model.data.ChatItem
import com.study.messengerfintech.model.data.StreamAndChatItem
import com.study.messengerfintech.model.data.StreamItem
import com.study.messengerfintech.viewmodel.MainViewModel
import com.study.messengerfintech.viewmodel.chatRecycler.StreamsAndChatsAdapter

class StreamsRecyclerFragment : Fragment(R.layout.streams_and_chats_fragment) {

    private val viewModel: MainViewModel by activityViewModels()
    private val items: MutableList<StreamAndChatItem> = mutableListOf()

    private val adapter = StreamsAndChatsAdapter(items) { position ->
        when (val item = items[position]) {
            is ChatItem -> item.also {
                viewModel.openChat(item.streamId, item.chatId)
            }

            is StreamItem -> {
                if (item.isExpanded)
                    deleteItems(position)
                else
                    addItems(item.id, position)
                item.isExpanded = !item.isExpanded
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.streams_and_chats_recycler)
        val subscribed = arguments?.getBoolean(SUBSCRIBED) ?: false
        items.addAll(viewModel.getStreams(subscribed).map {
            StreamItem(id = it.first, title = it.second)
        })
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun addItems(streamCount: Int, listPosition: Int) {
        val chats = viewModel.getChatsFromStream(streamCount)
        if (chats.isEmpty()) return

        items.addAll(listPosition + 1, chats)
        adapter.notifyItemRangeInserted(listPosition + 1, chats.size)
        adapter.notifyItemRangeChanged(listPosition + 1 + chats.size, adapter.itemCount)

    }

    private fun deleteItems(listPosition: Int) {
        var counter = 0
        while (items[listPosition + 1] is ChatItem && items.size > listPosition + 1) {
            items.removeAt(listPosition + 1)
            counter += 1
        }
        adapter.notifyItemRangeRemoved(listPosition + 1, counter)
        adapter.notifyItemRangeChanged(listPosition + 1, adapter.itemCount)

    }

    companion object {
        private const val SUBSCRIBED = "SUBSCRIBED"
        fun newInstance(subscribed: Boolean) =
            StreamsRecyclerFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(SUBSCRIBED, subscribed)
                }
            }
    }
}