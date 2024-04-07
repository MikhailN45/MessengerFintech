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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) viewModel.onStreamsFragmentViewCreated()

        searchUsersEditText.doAfterTextChanged {
            viewModel.onStreamsFragmentSearchUsersTextChanged(it.toString())
        }

        val pagerAdapter = PagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle
        )
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
}