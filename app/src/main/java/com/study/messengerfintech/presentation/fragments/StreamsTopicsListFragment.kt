package com.study.messengerfintech.presentation.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.StreamsAndChatsFragmentBinding
import com.study.messengerfintech.domain.model.StreamItem
import com.study.messengerfintech.domain.model.StreamTopicItem
import com.study.messengerfintech.domain.model.TopicItem
import com.study.messengerfintech.getComponent
import com.study.messengerfintech.presentation.adapters.StreamType
import com.study.messengerfintech.presentation.adapters.StreamsTopicsAdapter
import com.study.messengerfintech.presentation.events.StreamsEvent
import com.study.messengerfintech.presentation.state.State
import com.study.messengerfintech.presentation.viewmodel.StreamsViewModel
import javax.inject.Inject

class StreamsTopicsListFragment : FragmentMvi<State.Streams>(R.layout.streams_and_chats_fragment) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: StreamsViewModel by activityViewModels { viewModelFactory }
    private var _binding: StreamsAndChatsFragmentBinding? = null
    private val binding get() = _binding!!
    private val items: MutableList<StreamTopicItem> = mutableListOf()

    private val adapter = StreamsTopicsAdapter { onClickedItem ->
        when (onClickedItem) {
            is TopicItem -> {
                onClickedItem.also {
                    viewModel.processEvent(
                        StreamsEvent.OpenChat.Topic(
                            onClickedItem.streamId,
                            onClickedItem.title
                        )
                    )
                }
            }

            is StreamItem -> {
                if (onClickedItem.isExpanded) {
                    deleteItemsFromAdapter(onClickedItem)
                } else {
                    try {
                        addItemsToAdapter(onClickedItem)
                    } catch (error: Throwable) {
                        Log.e("streamItemClicked", "${error.message}")
                    }

                }
                onClickedItem.isExpanded = !onClickedItem.isExpanded
            }
        }
    }

    override fun render(state: State.Streams) {
        items.addAll(state.items.toMutableList())
        adapter.submitList(items) {
            binding.streamsAndChatsRecycler.scrollToPosition(0)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().streamsComponent().create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StreamsAndChatsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.screenState.observe(viewLifecycleOwner) {
            with(binding) {
                streamsShimmer.isVisible = it is State.Loading
                streamsAndChatsRecycler.isVisible = it !is State.Loading
            }
        }

        val streamType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(STREAM_TYPE, StreamType::class.java)
        } else {
            arguments?.getParcelable(STREAM_TYPE)
        } ?: StreamType.AllStreams

        if (streamType == StreamType.SubscribedStreams) {
            viewModel.streamsSubscribed.observe(viewLifecycleOwner, ::render)
        } else {
            viewModel.streamsAll.observe(viewLifecycleOwner, ::render)
        }

        binding.streamsAndChatsRecycler.apply {
            adapter = this@StreamsTopicsListFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onStop() {
        super.onStop()
        collapseStreams()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collapseStreams() {
        for (item in items) {
            if (item is StreamItem) {
                item.isExpanded = false
            }
        }
        val streamItems = items.filterIsInstance<StreamItem>().toMutableList()
        items.clear()
        items.addAll(streamItems)
    }

    private fun addItemsToAdapter(item: StreamItem) {
        val position = items.indexOf(item)
        items.addAll(position + 1, item.topics)
        adapter.notifyItemRangeInserted(position + 1, item.topics.size)
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