package com.example.clickchat.RecyclerViewStory

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.DisplayImageActivity
import com.example.clickchat.R

class StoryViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var mEmail: TextView

    init {
        itemView.setOnClickListener(this)
        mEmail = itemView.findViewById(R.id.email)
    }


    override fun onClick(view: View) {
        val intent = Intent(
            view.context,
            DisplayImageActivity::class.java
        )
        val b = Bundle()
        b.putString("userId", mEmail.tag.toString())
        intent.putExtras(b)
        view.context.startActivity(intent)
    }
}