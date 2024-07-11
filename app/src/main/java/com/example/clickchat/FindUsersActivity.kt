package com.example.clickchat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.RecyclerviewFollow.FollowObject
import com.example.clickchat.RecyclerviewFollow.FollowAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FindUsersActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FollowAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private lateinit var mInput: EditText

    private val results = ArrayList<FollowObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_users)

        mInput = findViewById(R.id.input)
        val mSearch = findViewById<Button>(R.id.search)

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = FollowAdapter(results, this)
        mRecyclerView.adapter = mAdapter

        mSearch.setOnClickListener {
            clearResults()
            listenForData()
        }
    }

    private fun listenForData() {
        val inputText = mInput.text.toString()
        if (inputText.isBlank()) {
            Log.d("FindUsersActivity", "Input is blank")
            return
        }

        val usersDb = FirebaseDatabase.getInstance().reference.child("users")
        val query = usersDb.orderByChild("email")
            .startAt(inputText)
            .endAt("$inputText\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.children.forEach { snapshot ->
                        val email = snapshot.child("email").value?.toString() ?: ""
                        val uid = snapshot.key

                        if (uid == null) {
                            Log.d("FindUsersActivity", "User ID is null")
                            return
                        }

                        Log.d("FindUsersActivity", "User found: $email with UID: $uid")

                        if (email != FirebaseAuth.getInstance().currentUser?.email) {
                            val obj = FollowObject(email, uid)
                            results.add(obj)
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Log.d("FindUsersActivity", "No user found with the given email")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("FindUsersActivity", "Database error: ${databaseError.message}")
            }
        })
    }

    private fun clearResults() {
        results.clear()
        mAdapter.notifyDataSetChanged()
    }
}
