package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.MainFragmentBinding

class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)

        binding.bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.channels_page -> childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, StreamsFragment())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()

                R.id.people_page -> childFragmentManager.commit {
                    replace(R.id.fragment_container, PeopleFragment())
                    addToBackStack(null)
                }

                R.id.profile_page -> childFragmentManager.commit {
                    replace(R.id.fragment_container, ProfileFragment())
                    addToBackStack(null)
                }
            }
            true
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StreamsFragment())
            .addToBackStack(null)
            .commitAllowingStateLoss()

        return binding.root
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}