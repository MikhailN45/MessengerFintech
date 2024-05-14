package com.study.messengerfintech.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ProfileFragmentBinding
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.model.UserStatus
import com.study.messengerfintech.presentation.events.Event
import com.study.messengerfintech.presentation.state.State
import com.study.messengerfintech.presentation.viewmodel.MainViewModel

class ProfileFragment : FragmentMVI<State.Profile>(R.layout.profile_fragment) {
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val currentUser = User.ME

    override fun render(state: State.Profile) {
        updateStatusTextColor(state.user)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userName.text = currentUser.name
        viewModel.processEvent(Event.SetUserStatus(currentUser))
        viewModel.userStatus.observe(viewLifecycleOwner) {
            render(it)
        }

        binding.profileAvatar.apply {
            Glide.with(context)
                .load(currentUser.avatarUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_avatar_placeholder)
                .into(this)
        }
    }

    private fun updateStatusTextColor(user: User) {
        binding.userStatus.apply {
            val color = ContextCompat.getColor(
                context,
                when (user.status) {
                    UserStatus.Online -> R.color.green
                    UserStatus.Idle -> R.color.yellow
                    UserStatus.Offline -> R.color.red
                }
            )
            setTextColor(color)
            text = user.status.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}