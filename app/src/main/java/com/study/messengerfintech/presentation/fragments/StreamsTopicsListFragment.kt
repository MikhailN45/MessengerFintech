package com.study.messengerfintech.presentation.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.StreamsAndChatsFragmentBinding
import com.study.messengerfintech.domain.model.StreamItem
import com.study.messengerfintech.domain.model.StreamTopicItem
import com.study.messengerfintech.domain.model.TopicItem
import com.study.messengerfintech.presentation.adapters.StreamType
import com.study.messengerfintech.presentation.adapters.StreamsTopicsAdapter
import com.study.messengerfintech.presentation.events.Event
import com.study.messengerfintech.presentation.state.State
import com.study.messengerfintech.presentation.viewmodel.MainViewModel

class StreamsTopicsListFragment : FragmentMVI<State.Streams>(R.layout.streams_and_chats_fragment) {
    private var _binding: StreamsAndChatsFragmentBinding? = null
    private val binding get() = _binding!!
    private var items: MutableList<StreamTopicItem> = mutableListOf()
    private val viewModel: MainViewModel by activityViewModels()
    private var streams = State.Streams(listOf())

    private val adapter = StreamsTopicsAdapter { onClickedItem ->
        when (onClickedItem) {
            is TopicItem -> {
                onClickedItem.also {
                    viewModel.processEvent(
                        Event.OpenChat.Topic(
                            onClickedItem.streamId,
                            onClickedItem.title
                        )
                    )
                    collapseStreams()
                }
            }

            is StreamItem -> {
                if (onClickedItem.isExpanded) {
                    deleteItemsFromAdapter(onClickedItem)
                } else {
                    viewModel.processEvent(Event.ExpandStream(onClickedItem))
                    Handler(Looper.getMainLooper()).postDelayed({
                        addItemsToAdapter(onClickedItem)

                    }, 500)
                }
                onClickedItem.isExpanded = !onClickedItem.isExpanded
            }
        }
    }

    override fun render(state: State.Streams) {
        this.streams = state
        items = state.items.toMutableList()
        adapter.submitList(items) {
            binding.streamsAndChatsRecycler.scrollToPosition(0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StreamsAndChatsFragmentBinding.inflate(layoutInflater)


        viewModel.screenState.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.streamsShimmer.visibility = View.VISIBLE
                    binding.streamsAndChatsRecycler.visibility = View.GONE
                }

                is State.Error -> {
                    binding.streamsShimmer.visibility = View.GONE
                    binding.streamsAndChatsRecycler.visibility = View.VISIBLE
                }

                is State.Success -> {
                    binding.streamsShimmer.visibility = View.GONE
                    binding.streamsAndChatsRecycler.visibility = View.VISIBLE
                }

                else -> State.Error
            }
        }

        val streamType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(STREAM_TYPE, StreamType::class.java)
        } else {
            arguments?.getParcelable(STREAM_TYPE)
        } ?: StreamType.AllStreams
        if (streamType == StreamType.SubscribedStreams) {
            viewModel.streamsSubscribed.observe(viewLifecycleOwner) { state ->
                render(state)
            }
        } else {
            viewModel.streamsAll.observe(viewLifecycleOwner) { state ->
                render(state)
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
        collapseStreams()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        items = items.filterIsInstance<StreamItem>().toMutableList()
        items.clear()
        _binding = null
    }

    private fun collapseStreams() {
        for (item in items) {
            if (item is StreamItem) {
                item.isExpanded = false
            }
        }
        items = items.filterIsInstance<StreamItem>().toMutableList()
    }

    private fun addItemsToAdapter(stream: StreamItem) {
        val position = items.indexOf(stream)
        items.addAll(position + 1, stream.topics)
        adapter.notifyItemRangeInserted(position + 1, stream.topics.size)
        adapter.notifyItemRangeChanged(position, items.size)
    }

    private fun deleteItemsFromAdapter(item: StreamItem) {
        val position = items.indexOf(item)
        var counter = 0
        while (position + 1 < items.size && items[position + 1] is TopicItem) {
            items.removeAt(position + 1)
            counter += 1
        }
        adapter.notifyItemRangeRemoved(position + 1, counter)
        adapter.notifyItemRangeChanged(position + 1, adapter.itemCount)
    }

    companion object {
        private const val STREAM_TYPE = "STREAM_TYPE"
        fun newInstance(streamType: StreamType) =
            StreamsTopicsListFragment().apply {
                arguments = Bundle().apply { putParcelable(STREAM_TYPE, streamType) }
            }
    }
}