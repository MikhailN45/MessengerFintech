package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.databinding.UsersFragmentBinding
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

        binding.searchUsersEditText.doAfterTextChanged {
            viewModel.searchUsers(it.toString())
        }

        //viewModel.state.observe(this)

        if (savedInstanceState == null)
            viewModel.searchUsers("")
        viewModel.users.observe(viewLifecycleOwner) {
            adapter.submitList(it) {
                binding.usersRecycler.scrollToPosition(0)
            }
        }

        binding.usersRecycler.apply {
            adapter = this@UsersFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}