package com.example.clickchat.RecyclerviewFollow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.R

class RCAdpater(private val usersList: List<UsersObject>, private val context: Context) :
    RecyclerView.Adapter<RCViewHolders>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCViewHolders {
        val layoutView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_followers_item, parent, false)
        return RCViewHolders(layoutView)
    }

    override fun onBindViewHolder(holder: RCViewHolders, position: Int) {
        holder.mEmail.text = usersList[position].email
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}