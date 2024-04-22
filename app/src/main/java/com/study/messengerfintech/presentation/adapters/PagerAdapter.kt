package com.study.messengerfintech.presentation.adapters

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.study.messengerfintech.presentation.fragments.StreamsTopicsListFragment
import kotlinx.parcelize.Parcelize

class PagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment = StreamsTopicsListFragment.newInstance(
        streamType = StreamType.entries[position]
    )

    override fun getItemCount(): Int = StreamType.entries.size
}

@Parcelize
enum class StreamType: Parcelable {
    SubscribedStreams, AllStreams
}