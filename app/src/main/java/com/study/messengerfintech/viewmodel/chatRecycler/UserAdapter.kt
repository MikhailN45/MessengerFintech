package com.study.messengerfintech.viewmodel.chatRecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.study.messengerfintech.R
import com.study.messengerfintech.model.data.User

class UserAdapter(
    private val dataSet: MutableList<User>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

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
        holder.nickName.text = dataSet[position].nickName
    }

    override fun getItemCount(): Int = dataSet.size
}