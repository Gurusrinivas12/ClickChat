package com.example.clickchat.RecyclerviewFollow

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.R


class FollowViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mEmail: TextView = itemView.findViewById<TextView>(R.id.email)
    var mFollow: Button = itemView.findViewById<Button>(R.id.follow)
}