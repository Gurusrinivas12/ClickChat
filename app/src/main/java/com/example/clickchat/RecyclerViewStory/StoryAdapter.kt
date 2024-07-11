package com.example.clickchat.RecyclerViewStory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.R

class StoryAdapter(
    private val usersList: List<StoryObject>,
    private val context: Context
) : RecyclerView.Adapter<StoryViewHolders>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolders {
        val layoutView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_story_item, parent, false)
        return StoryViewHolders(layoutView)
    }

    override fun onBindViewHolder(holder: StoryViewHolders, position: Int) {
        holder.mEmail.text = usersList[position].email
        holder.mEmail.tag = usersList[position].uid
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}
