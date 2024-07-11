package com.example.clickchat.RecyclerViewStory


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.R

class StoryViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mEmail: TextView = itemView.findViewById<TextView>(R.id.email)
}