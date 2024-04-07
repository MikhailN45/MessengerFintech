package com.study.messengerfintech.view.fragments

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
import com.study.messengerfintech.viewmodel.MainViewModel
import com.study.messengerfintech.viewmodel.PagerAdapter
import com.study.messengerfintech.viewmodel.Streams

class StreamsFragment : Fragment() {
    private lateinit var binding: StreamsFragmentBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = StreamsFragmentBinding.inflate(inflater, container, false).also {
        binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null)
            viewModel.searchStreams("")

        binding.searchUsersEditText.doAfterTextChanged {
            viewModel.searchStreams(it.toString())
        }

        val tabLayout = binding.streamsTabLayout
        val streamsPager = binding.streamsPager
        val viewPager = PagerAdapter(
            pages = listOf(Streams.SubscribedStreams, Streams.AllStreams),
            childFragmentManager, lifecycle
        )
        streamsPager.adapter = viewPager

        val tabs: List<String> =
            listOf(getString(R.string.subscribed), getString(R.string.all_streams))

        tabLayout.setTabTextColors(
            Color.parseColor("#70FAFAFA"),
            Color.parseColor("#FAFAFA")
        )

        TabLayoutMediator(tabLayout, streamsPager)
        { tab, position ->
            tab.text = tabs[position]
        }.attach()

    }
}