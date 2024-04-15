package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.databinding.UsersFragmentBinding
import com.study.messengerfintech.view.states.UsersState
import com.study.messengerfintech.viewmodel.MainViewModel
import com.study.messengerfintech.viewmodel.chatRecycler.UsersAdapter

class UsersFragment : Fragment() {
    private lateinit var binding: UsersFragmentBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val adapter = UsersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = UsersFragmentBinding.inflate(inflater, container, false).also {
        binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) viewModel.onUsersFragmentViewCreated()

        binding.searchUsersEditText.doAfterTextChanged {
            viewModel.onUsersFragmentSearchUsersTextChanged(it.toString())
        }

        viewModel.usersState.observe(viewLifecycleOwner) {
            binding.usersShimmer.isVisible = it is UsersState.Loading
            when (it) {
                is UsersState.Success -> {
                    adapter.submitList(it.users) {
                        binding.usersRecycler.scrollToPosition(0)
                    }
                }

                is UsersState.Loading -> {}
                is UsersState.Error -> {}
            }
        }

        binding.usersRecycler.apply {
            adapter = this@UsersFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}