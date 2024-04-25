package com.study.messengerfintech.presentation.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.StreamsFragmentBinding
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.presentation.adapters.PagerAdapter
import com.study.messengerfintech.presentation.events.Event
import com.study.messengerfintech.presentation.viewmodel.StreamsViewModel

class StreamsFragment : Fragment() {
    private val viewModel: StreamsViewModel by activityViewModels()
    private var _binding: StreamsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StreamsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.processEvent(Event.SearchForStreams())
        }

        searchUsersEditText.doAfterTextChanged {
            viewModel.processEvent(Event.SearchForStreams(it.toString()))
        }

        searchUsersButton.setOnLongClickListener {
            viewModel.processEvent(Event.OpenChat.Private(User.ME))
            false
        }

        val pagerAdapter =
            PagerAdapter(fragmentManager = childFragmentManager, lifecycle = lifecycle)

        streamsPager.adapter = pagerAdapter

        val tabTitles: List<String> =
            listOf(getString(R.string.subscribed), getString(R.string.all_streams))

        streamsTabLayout.setTabTextColors(
            Color.parseColor("#70FAFAFA"),
            Color.parseColor("#FAFAFA")
        )

        TabLayoutMediator(streamsTabLayout, streamsPager)
        { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}