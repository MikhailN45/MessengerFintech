package com.study.messengerfintech.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import com.study.messengerfintech.presentation.state.UsersState
import com.study.messengerfintech.presentation.viewmodel.StreamsViewModel
import com.study.messengerfintech.presentation.viewmodel.UsersViewModel
import javax.inject.Inject

class UsersFragment : FragmentMVI<UsersState>(R.layout.streams_and_chats_fragment) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val streamsViewModel: StreamsViewModel by activityViewModels { viewModelFactory }
    private val usersViewModel: UsersViewModel by activityViewModels { viewModelFactory }
    private var _binding: UsersFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter = UsersAdapter(
        onUserClick = { user ->
            streamsViewModel.processEvent(StreamsEvent.OpenChat.Private(user))
        }
    )

    override fun render(state: UsersState) = with(binding) {
        adapter.submitList(state.users) {
            usersRecycler.scrollToPosition(0)
        }
            usersShimmer.isVisible = state.isLoading
            usersRecycler.isVisible = !state.isLoading
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersViewModel.state.observe(viewLifecycleOwner) { state -> render(state) }

        if (savedInstanceState == null) {
            usersViewModel.processEvent(UsersEvent.SearchForUsers())
        }

        binding.topbarSearchEditText.doAfterTextChanged {
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