package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ProfileFragmentBinding
import com.study.messengerfintech.model.data.User
import com.study.messengerfintech.model.data.UserStatus
import com.study.messengerfintech.model.source.RepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class ProfileFragment : Fragment(R.layout.profile_fragment) {
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable = CompositeDisposable()

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
        binding.userName.text = User.ME.name
        updateStatus(User.ME)

        RepositoryImpl.loadStatus(User.ME)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { updateStatus(User.ME) },
                onError = { error ->
                    Log.e("loadStatus", error.toString()) }
            ).addTo(compositeDisposable)

        binding.profileAvatar.apply {
            Glide.with(context)
                .load(User.ME.avatarUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_avatar_placeholder)
                .into(this)
        }
    }

    private fun updateStatus(user: User) {
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
        compositeDisposable.dispose()
    }
}