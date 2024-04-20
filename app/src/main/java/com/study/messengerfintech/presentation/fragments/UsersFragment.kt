package com.study.messengerfintech.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.databinding.UsersFragmentBinding
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.presentation.state.UsersState
import com.study.messengerfintech.presentation.viewmodel.MainViewModel
import com.study.messengerfintech.presentation.adapters.UsersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class UsersFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private val compositeDisposable = CompositeDisposable()
    private var _binding: UsersFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter = UsersAdapter {
        viewModel.openPrivateChat(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UsersFragmentBinding.inflate(layoutInflater)

        viewModel.userScreenState.observe(viewLifecycleOwner) {
            when (it) {
                is UsersState.Loading -> {
                    binding.usersShimmer.visibility = View.VISIBLE
                    binding.usersRecycler.visibility = View.GONE
                }

                is UsersState.Error -> {
                    Log.e("UserListError", it.error.message.toString())
                    binding.usersShimmer.visibility = View.GONE
                    binding.usersRecycler.visibility = View.VISIBLE
                }

                is UsersState.Success -> {
                    binding.usersShimmer.visibility = View.GONE
                    binding.usersRecycler.visibility = View.VISIBLE
                    viewModel.users.observe(viewLifecycleOwner) { users ->
                        updateUsersStatus(users)
                        adapter.submitList(users) {
                            binding.usersRecycler.scrollToPosition(0)
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchUsersEditText.doAfterTextChanged {
            viewModel.searchUsers(it.toString())
        }

        if (savedInstanceState == null) viewModel.searchUsers(BLANK_STRING)

        binding.usersRecycler.apply {
            adapter = this@UsersFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateUsersStatus(users: List<User>) {
        users.forEachIndexed { index, user ->
            viewModel.loadStatus(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        adapter.notifyItemChanged(index)
                    },
                    onError = { error ->
                        viewModel.usersScreenError(error)
                    }
                ).addTo(compositeDisposable)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.dispose()
    }

    companion object {
        const val BLANK_STRING = ""
    }
}