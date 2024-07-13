package com.example.clickchat.RecyclerViewStory
import android.content.Intent
import android.os.Bundle
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
        b.putString("userId", mEmail.tag.toString())
        b.putString("chatOrStory", mLayout.tag.toString())
        intent.putExtras(b)
        view.context.startActivity(intent)
    }
}
