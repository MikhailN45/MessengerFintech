package com.study.messengerfintech.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.UsersFragmentBinding
import com.study.messengerfintech.getComponent
import com.study.messengerfintech.presentation.adapters.UsersAdapter
import com.study.messengerfintech.presentation.events.StreamsEvent
import com.study.messengerfintech.presentation.events.UsersEvent
import com.study.messengerfintech.presentation.state.State
import com.study.messengerfintech.presentation.viewmodel.StreamsViewModel
import com.study.messengerfintech.presentation.viewmodel.UsersViewModel
import javax.inject.Inject

class UsersFragment : FragmentMVI<State.Users>(R.layout.streams_and_chats_fragment) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: StreamsViewModel by activityViewModels { viewModelFactory }
    private val usersViewModel: UsersViewModel by activityViewModels { viewModelFactory }
    private var _binding: UsersFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter = UsersAdapter(
        onClick = { user -> viewModel.processEvent(StreamsEvent.OpenChat.Private(user)) }
    )

    override fun render(state: State.Users) {
        adapter.submitList(state.users) {
            binding.usersRecycler.scrollToPosition(0)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().userComponent().create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UsersFragmentBinding.inflate(layoutInflater)

        usersViewModel.usersScreenState.observe(viewLifecycleOwner) {
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

        usersViewModel.users.observe(viewLifecycleOwner) { state -> render(state) }

        if (savedInstanceState == null) {
            usersViewModel.processEvent(UsersEvent.SearchForUsers())
        }

        binding.searchUsersEditText.doAfterTextChanged {
            usersViewModel.processEvent(UsersEvent.SearchForUsers(query = it.toString()))
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