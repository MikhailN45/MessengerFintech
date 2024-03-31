package com.study.messengerfintech.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.study.messengerfintech.databinding.StreamsFragmentBinding
import com.study.messengerfintech.viewmodel.PagerAdapter

class StreamsFragment : Fragment() {
    private lateinit var binding: StreamsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StreamsFragmentBinding.inflate(layoutInflater)
        val tabLayout = binding.streamsTabLayout
        val streamsPager = binding.streamsPager
        val viewPager = PagerAdapter(childFragmentManager, lifecycle)
        val tabs: List<String> = listOf("Subscribed", "All streams")
        streamsPager.adapter = viewPager

        tabLayout.setTabTextColors(
            Color.parseColor("#70FAFAFA"),
            Color.parseColor("#FAFAFA")
        )

        viewPager.update(
            listOf(
                StreamsRecyclerFragment.newInstance(true),
                StreamsRecyclerFragment.newInstance(false)
            )
        )

        TabLayoutMediator(tabLayout, streamsPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        return binding.root
    }
}