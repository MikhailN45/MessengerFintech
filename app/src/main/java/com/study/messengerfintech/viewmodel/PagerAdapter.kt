package com.study.messengerfintech.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.study.messengerfintech.view.fragments.StreamsRecyclerFragment

class PagerAdapter(
    private val pages: List<Streams>,
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment =
        StreamsRecyclerFragment.newInstance(pages[position])

    override fun getItemCount(): Int = pages.size
}

enum class Streams {
    SubscribedStreams, AllStreams
}