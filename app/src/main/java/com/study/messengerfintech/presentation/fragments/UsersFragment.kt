package com.study.messengerfintech.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.databinding.UsersFragmentBinding
import com.study.messengerfintech.presentation.adapters.UsersAdapter
import com.study.messengerfintech.presentation.events.Event
import com.study.messengerfintech.presentation.state.State
import com.study.messengerfintech.presentation.viewmodel.MainViewModel

class UsersFragment : FragmentMVI<State.Users>() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: UsersFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter = UsersAdapter(
        onClick = { user ->
            viewModel.processEvent(Event.OpenChat.Private(user))
        }
    )

    override fun render(state: State.Users) {
        adapter.submitList(state.users) {
            binding.usersRecycler.scrollToPosition(0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UsersFragmentBinding.inflate(layoutInflater)

        viewModel.screenState.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.usersShimmer.visibility = View.VISIBLE
                    binding.usersRecycler.visibility = View.GONE
                }

                is State.Error -> {
                    binding.usersShimmer.visibility = View.GONE
                    binding.usersRecycler.visibility = View.VISIBLE
                }

                is State.Success -> {
                    binding.usersShimmer.visibility = View.GONE
                    binding.usersRecycler.visibility = View.VISIBLE
                }
                else -> {
                    State.Error
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.users.observe(viewLifecycleOwner) { state -> render(state) }

        if (savedInstanceState == null) {
            viewModel.processEvent(Event.SearchForUsers())
        }

        binding.searchUsersEditText.doAfterTextChanged {
            viewModel.processEvent(Event.SearchForUsers(query = it.toString()))
        }

        binding.usersRecycler.apply {
            adapter = this@UsersFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}