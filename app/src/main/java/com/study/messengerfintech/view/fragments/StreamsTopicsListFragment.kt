package com.study.messengerfintech.view.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.StreamsAndChatsFragmentBinding
import com.study.messengerfintech.domain.mapper.TopicToItemMapper
import com.study.messengerfintech.model.data.StreamItem
import com.study.messengerfintech.model.data.StreamTopicItem
import com.study.messengerfintech.model.data.TopicItem
import com.study.messengerfintech.view.state.StreamsTopicsState
import com.study.messengerfintech.viewmodel.MainViewModel
import com.study.messengerfintech.viewmodel.StreamType
import com.study.messengerfintech.viewmodel.chatRecycler.StreamsTopicsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy

class StreamsTopicsListFragment : Fragment(R.layout.streams_and_chats_fragment) {
    private var _binding: StreamsAndChatsFragmentBinding? = null
    private val binding get() = _binding!!
    private val topicToItemMapper: TopicToItemMapper = TopicToItemMapper()
    private var items: MutableList<StreamTopicItem> = mutableListOf()
    private val viewModel: MainViewModel by activityViewModels()

    private val adapter = StreamsTopicsAdapter { position ->
        when (val item = items[position]) { //todo remove this (position)
            is TopicItem -> item.also {
                viewModel.openTopicChat(item.streamId, item.title)
                closeStreams()
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
    ): View {
        _binding = StreamsAndChatsFragmentBinding.inflate(layoutInflater)

        viewModel.streamTopicsState.observe(viewLifecycleOwner) {
            when (it) {
                is StreamsTopicsState.Loading -> {
                    binding.streamsShimmer.visibility = View.VISIBLE
                    binding.streamsAndChatsRecycler.visibility = View.GONE
                }

                is StreamsTopicsState.Error -> {
                    Log.e("StreamListError", it.error.message.toString())
                    binding.streamsShimmer.visibility = View.GONE
                    binding.streamsAndChatsRecycler.visibility = View.VISIBLE
                }

                is StreamsTopicsState.Success -> {
                    binding.streamsShimmer.visibility = View.GONE
                    binding.streamsAndChatsRecycler.visibility = View.VISIBLE
                }
            }
        }

        val streamType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(TAG, StreamType::class.java)
        } else {
            arguments?.getParcelable(TAG)
        } ?: StreamType.AllStreams

        (if (streamType == StreamType.SubscribedStreams)
            viewModel.subscribedStreams else viewModel.streams)
            .observe(viewLifecycleOwner) {
                items = it.toMutableList()
                adapter.submitList(items) {
                    binding.streamsAndChatsRecycler.scrollToPosition(0)
                }
            }

        binding.streamsAndChatsRecycler.apply {
            adapter = this@StreamsTopicsListFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        closeStreams()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun closeStreams() {
        for (item in items)
            if (item is StreamItem)
                item.isExpanded = false
    }

    @SuppressLint("CheckResult")
    private fun addItems(stream: StreamItem, position: Int) { //todo extract logic to viewmodel
        stream.isLoading = true
        adapter.notifyItemChanged(position)

        viewModel.getTopics(stream.streamId)
            .map { topicToItemMapper(it, stream.streamId) }
            .doOnSuccess { topics ->
                stream.topics = topics
                topics.onEachIndexed { index, topic ->
                    viewModel.getMessagesCount(stream.streamId, topic.title)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ messageNum ->
                            topic.messageCount = messageNum
                            adapter.notifyItemChanged(position + index + 1)
                        }, { error -> viewModel.streamTopicScreenError(error) }
                        )
                }
            }
            .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onSuccess = { chats ->
                    stream.isLoading = false
                    items.addAll(position + 1, chats)
                    adapter.notifyItemChanged(position)
                    adapter.notifyItemRangeInserted(position + 1, chats.size)
                    adapter.notifyItemRangeChanged(position + chats.size + 1, adapter.itemCount)
                },
                onError = { error ->
                    stream.isLoading = false
                    viewModel.streamTopicScreenError(error)
                })
    }

    private fun deleteItems(position: Int) {
        var counter = 0
        while (position + 1 < items.size && items[position + 1] is TopicItem) {
            items.removeAt(position + 1)
            counter += 1
        }
        adapter.notifyItemRangeRemoved(position + 1, counter)
        adapter.notifyItemRangeChanged(position + 1, adapter.itemCount)

    }

    companion object {
        private const val TAG = "SUBSCRIBED_KEY"
        fun newInstance(streamType: StreamType) =
            StreamsTopicsListFragment().apply {
                arguments = Bundle().apply { putParcelable(TAG, streamType) }
            }
    }
}