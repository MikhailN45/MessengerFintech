package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.databinding.UsersFragmentBinding
import com.study.messengerfintech.model.FakeDataSource
import com.study.messengerfintech.viewmodel.chatRecycler.UsersAdapter

class UsersFragment : Fragment() {
    private lateinit var binding: UsersFragmentBinding
    private val usersAdapter by lazy { UsersAdapter() }
    private val users = FakeDataSource.getUsers()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UsersFragmentBinding.inflate(layoutInflater)

        binding.usersRecycler.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(context)
        }
        usersAdapter.setData(users)
        return binding.root
    }
}