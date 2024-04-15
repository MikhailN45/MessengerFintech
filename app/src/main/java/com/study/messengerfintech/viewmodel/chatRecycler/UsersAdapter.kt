package com.study.messengerfintech.viewmodel.chatRecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.databinding.UserItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.study.messengerfintech.R
import com.study.messengerfintech.domain.data.User
import com.study.messengerfintech.domain.data.UserStatus

class UsersAdapter(val onClick: (User) -> Unit) :
    ListAdapter<User, UsersAdapter.ViewHolder>(UsersDiffUtil()) {
    inner class ViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.binding.nickname.text = user.name
        holder.binding.contactEmail.text = user.email
        holder.binding.contactEmail.isSelected = true
        holder.binding.onlineStatus.setImageResource(
            when (user.status) {
                UserStatus.Online -> R.drawable.ic_online
                UserStatus.Idle -> R.drawable.ic_idle
                UserStatus.Offline -> R.drawable.ic_offline
            }
        )

        holder.binding.avatar.apply {
            Glide.with(context)
                .load(user.avatarUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_avatar_placeholder)
                .into(this)
        }

        holder.binding.root.setOnClickListener { onClick(user) }
    }
}

class UsersDiffUtil : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}