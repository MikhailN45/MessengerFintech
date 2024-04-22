package com.study.messengerfintech.presentation.fragments

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
import com.study.messengerfintech.domain.model.StreamItem
import com.study.messengerfintech.domain.model.StreamTopicItem
import com.study.messengerfintech.domain.model.TopicItem
import com.study.messengerfintech.presentation.state.StreamsTopicsState
import com.study.messengerfintech.presentation.viewmodel.MainViewModel
import com.study.messengerfintech.presentation.adapters.StreamType
import com.study.messengerfintech.presentation.adapters.StreamsTopicsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class StreamsTopicsListFragment : Fragment(R.layout.streams_and_chats_fragment) {
    private var _binding: StreamsAndChatsFragmentBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable = CompositeDisposable()
    private var items: MutableList<StreamTopicItem> = mutableListOf()
    private val viewModel: MainViewModel by activityViewModels()

    private val adapter = StreamsTopicsAdapter { item ->
        when (item) {
            is TopicItem -> {
                viewModel.openTopicChat(item.streamId, item.title)
                closeStreams()
            }

            is StreamItem -> {
                if (item.isExpanded) deleteItemsFromAdapter(item)
                else addItemsToAdapter(item)
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
        compositeDisposable.clear()
    }

    private fun closeStreams() {
        for (item in items)
            if (item is StreamItem) item.isExpanded = false
    }

    private fun addItemsToAdapter(stream: StreamItem) {
        stream.isLoading = true
        val position = items.indexOf(stream)
        adapter.notifyItemChanged(position)

        val disposable =
            viewModel.parseTopicsAndMessageCountFromStream(stream, position)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { (topics, position) ->
                    items.addAll(position + 1, topics)
                    adapter.notifyItemChanged(position)
                    adapter.notifyItemRangeInserted(position + 1, topics.size)
                    adapter.notifyItemRangeChanged(position + topics.size + 1, adapter.itemCount)
                },
                onError = { error ->
                    viewModel.streamTopicScreenError(error)
                })
        compositeDisposable.add(disposable)
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
        private const val TAG = "SUBSCRIBED_KEY"
        fun newInstance(streamType: StreamType) =
            StreamsTopicsListFragment().apply {
                arguments = Bundle().apply { putParcelable(TAG, streamType) }
            }
    }
}