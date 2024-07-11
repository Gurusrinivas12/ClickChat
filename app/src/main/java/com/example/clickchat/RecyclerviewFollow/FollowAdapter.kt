package com.example.clickchat.RecyclerviewFollow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.clickchat.UserInformation

class FollowAdapter(private val usersList: List<FollowObject>, private val context: Context) :
    RecyclerView.Adapter<FollowAdapter.RCViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCViewHolder {
        val layoutView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_followers_item, parent, false)
        return RCViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: RCViewHolder, position: Int) {
        val user = usersList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class RCViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mEmail: TextView = view.findViewById(R.id.email)
        private val mFollow: Button = view.findViewById(R.id.follow)

        fun bind(user: FollowObject) {
            mEmail.text = user.email

            if (UserInformation.listFollowing.contains(user.uid)) {
                mFollow.text = context.getString(R.string.following)
            } else {
                mFollow.text = context.getString(R.string.follow)
            }

            mFollow.setOnClickListener {
                handleFollowButtonClick(user, mFollow)
            }
        }

        private fun handleFollowButtonClick(user: FollowObject, mFollow: Button) {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val followingRef = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(userId)
                .child("following")
                .child(user.uid)

            if (!UserInformation.listFollowing.contains(user.uid)) {
                mFollow.text = context.getString(R.string.following)
                followingRef.setValue(true)
                UserInformation.listFollowing.add(user.uid)
            } else {
                mFollow.text = context.getString(R.string.follow)
                followingRef.removeValue()
                UserInformation.listFollowing.remove(user.uid)
            }
        }
    }
}
