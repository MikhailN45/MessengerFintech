package com.study.messengerfintech.viewmodel

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragmentList: MutableList<Fragment> = mutableListOf()

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    @SuppressLint("NotifyDataSetChanged")
    fun update(fragments: List<Fragment>) {
        this.fragmentList.clear()
        this.fragmentList.addAll(fragments)
        notifyDataSetChanged()
    }

}