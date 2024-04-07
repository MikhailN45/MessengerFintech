package com.study.messengerfintech.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.StreamsAndChatsFragmentBinding
import com.study.messengerfintech.model.data.ChatItem
import com.study.messengerfintech.model.data.StreamAndChatItem
import com.study.messengerfintech.model.data.StreamItem
import com.study.messengerfintech.domain.mapper.ChatMapper
import com.study.messengerfintech.viewmodel.MainViewModel
import com.study.messengerfintech.viewmodel.Streams
import com.study.messengerfintech.viewmodel.chatRecycler.StreamsAndChatsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers

class StreamsRecyclerFragment : Fragment(R.layout.streams_and_chats_fragment) {
    private lateinit var binding: StreamsAndChatsFragmentBinding
    private val viewModel: MainViewModel by activityViewModels()
    private var items: MutableList<StreamAndChatItem> = mutableListOf()
    private val chatMapper: ChatMapper = ChatMapper()

    private val adapter = StreamsAndChatsAdapter { position ->
        when (val item = items[position]) {
            is ChatItem -> item.also {
                viewModel.openChat(item.streamId, item.chatId)
            }

            is StreamItem -> {
                if (item.isExpanded)
                    deleteItems(position)
                else
                    addItems(item, position)
                item.isExpanded = !item.isExpanded
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = StreamsAndChatsFragmentBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val streamType =
            arguments?.getSerializable(TAG)?.let { it as Streams } ?: Streams.AllStreams

        (if (streamType == Streams.SubscribedStreams) viewModel.subscribedStreams else viewModel.streams)
            .observe(viewLifecycleOwner) {
                items = it.toMutableList()
                    .onEach { item ->
                        if (item is StreamItem) item.isExpanded = false
                    }
                adapter.submitList(items) {
                    binding.streamsAndChatsRecycler.scrollToPosition(0)
                }
            }

        binding.streamsAndChatsRecycler.apply {
            adapter = this@StreamsRecyclerFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    @SuppressLint("CheckResult")
    private fun addItems(item: StreamItem, position: Int) {
        item.isLoading = true
        adapter.notifyItemChanged(position)

        viewModel.getChats(item.id)
            .map { chatMapper(it, item.id) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ chats ->
                item.isLoading = false
                items.addAll(position + 1, chats)
                activity?.runOnUiThread {
                    with(adapter) {
                        notifyItemChanged(position)
                        notifyItemRangeInserted(position + 1, chats.size)
                        notifyItemRangeChanged(position + chats.size + 1, adapter.itemCount)
                    }
                }
            },
                { error ->
                    item.isLoading = false
                    viewModel.error(error)
                })
    }

    private fun deleteItems(position: Int) {
        var counter = 0
        while (position + 1 < items.size && items[position + 1] is ChatItem) {
            items.removeAt(position + 1)
            counter += 1
        }
        adapter.notifyItemRangeRemoved(position + 1, counter)
        adapter.notifyItemRangeChanged(position + 1, adapter.itemCount)

    }

    companion object {
        private const val TAG = "SUBSCRIBED_KEY"
        fun newInstance(streamType: Streams) =
            StreamsRecyclerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TAG, streamType)
                }
            }
    }
}