package com.example.clickchat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class UserInformation {
    fun startFetching() {
        listFollowing.clear()
        userFollowing
    }

    private val userFollowing: Unit
        get() {
            val userFollowingDB = FirebaseDatabase.getInstance().reference.child("users").child(
                FirebaseAuth.getInstance().uid!!
            ).child("following")
            userFollowingDB.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.exists()) {
                        val uid = dataSnapshot.ref.key
                        if (uid != null && !listFollowing.contains(uid)) {
                            listFollowing.add(uid)
                        }
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val uid = dataSnapshot.ref.key
                        if (uid != null) {
                            listFollowing.remove(uid)
                        }
                    }
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

    companion object {
        var listFollowing: ArrayList<String> = ArrayList()
    }
}