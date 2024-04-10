package com.study.messengerfintech.viewmodel.chatRecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.R
import com.study.messengerfintech.model.data.User

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var oldUsersList = emptyList<User>()

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nickName: TextView = view.findViewById(R.id.nickname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nickName.text = oldUsersList[position].nickName
    }

    override fun getItemCount(): Int = oldUsersList.size

    fun setData(newUsersList: List<User>){
        val diffUtil = UsersDiffUtil(oldUsersList, newUsersList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        oldUsersList = newUsersList
        diffResults.dispatchUpdatesTo(this)

    }
}

private class UsersDiffUtil(
    private val oldList: List<User>,
    private val newList: List<User>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> false
            oldList[oldItemPosition].nickName != newList[newItemPosition].nickName -> false
            else -> true
        }
    }
}
