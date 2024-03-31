package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.databinding.PeopleFragmentBinding
import com.study.messengerfintech.model.data.User
import com.study.messengerfintech.viewmodel.chatRecycler.UserAdapter

class PeopleFragment : Fragment() {
    private lateinit var binding: PeopleFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PeopleFragmentBinding.inflate(layoutInflater)

        binding.usersRecycler.apply {
            adapter = UserAdapter(MutableList(4) {
                User(0, "Darrell Steward")
            })
            layoutManager = LinearLayoutManager(context)
        }
        return binding.root
    }
}