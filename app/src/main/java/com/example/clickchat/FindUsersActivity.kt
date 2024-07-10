package com.example.clickchat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.RecyclerviewFollow.UsersObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.example.clickchat.RecyclerviewFollow.RCAdapter

class FindUsersActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RCAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private lateinit var mInput: EditText

    private val results = ArrayList<UsersObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_users)

        mInput = findViewById(R.id.input)
        val mSearch = findViewById<Button>(R.id.search)

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.setNestedScrollingEnabled(false)
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = RCAdapter(results, this)
        mRecyclerView.adapter = mAdapter

        mSearch.setOnClickListener {
            clearResults()
            listenForData()
        }
    }

    private fun listenForData() {
        val usersDb = FirebaseDatabase.getInstance().reference.child("users")
        val query = usersDb.orderByChild("email").startAt(
            mInput.text.toString()
        ).endAt(mInput.text.toString() + "\uf8ff")
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                var email = ""
                val uid = dataSnapshot.ref.key
                if (dataSnapshot.child("email").value != null) {
                    email = dataSnapshot.child("email").value.toString()
                }
                if (email != FirebaseAuth.getInstance().currentUser!!.email) {
                    val obj = UsersObject(email, uid!!)
                    results.add(obj)
                    mAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun clearResults() {
        results.clear()
        mAdapter.notifyDataSetChanged()
    }
}