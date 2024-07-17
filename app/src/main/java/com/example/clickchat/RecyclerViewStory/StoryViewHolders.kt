package com.example.clickchat.RecyclerViewStory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.DisplayImageActivity
import com.example.clickchat.R

class StoryViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var mEmail: TextView = itemView.findViewById(R.id.email)
    var mLayout: LinearLayout = itemView.findViewById(R.id.layout)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent = Intent(view.context, DisplayImageActivity::class.java)
        val b = Bundle()

        // Check if mEmail.tag and mLayout.tag are null and handle appropriately
        val userId = mEmail.tag?.toString() ?: ""
        val chatOrStory = mLayout.tag?.toString() ?: ""

        Log.d("StoryViewHolders", "UserId: $userId, ChatOrStory: $chatOrStory")

        b.putString("userId", userId)
        b.putString("chatOrStory", chatOrStory)

        intent.putExtras(b)
        view.context.startActivity(intent)
    }
}
