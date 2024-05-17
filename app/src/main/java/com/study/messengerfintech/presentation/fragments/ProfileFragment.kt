package com.study.messengerfintech.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ProfileFragmentBinding
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.model.UserStatus
import com.study.messengerfintech.getComponent
import com.study.messengerfintech.presentation.events.ProfileEvent
import com.study.messengerfintech.presentation.state.ProfileState
import com.study.messengerfintech.presentation.viewmodel.ProfileViewModel
import javax.inject.Inject

class ProfileFragment : FragmentMvi<ProfileState>(R.layout.profile_fragment) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profileViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private val currentUser = User.ME

    override fun render(state: ProfileState) {
        updateStatusTextColor(state.user)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().profileComponent().create().inject(this)
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
        profileViewModel.processEvent(ProfileEvent.SetUserStatus(currentUser))
        profileViewModel.userStatus.observe(viewLifecycleOwner) {
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