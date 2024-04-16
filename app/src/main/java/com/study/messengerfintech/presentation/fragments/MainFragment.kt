package com.study.messengerfintech.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.MainFragmentBinding

class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.channels_page -> childFragmentManager.commit(allowStateLoss = true) {
                    replace(R.id.fragment_container, StreamsFragment())
                    addToBackStack(null)
                }

                R.id.people_page -> childFragmentManager.commit(allowStateLoss = true) {
                    replace(R.id.fragment_container, UsersFragment())
                    addToBackStack(null)
                }

                R.id.profile_page -> childFragmentManager.commit(allowStateLoss = true) {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}