package com.example.clickchat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.clickchat.R
import com.example.clickchat.RecyclerViewStory.StoryAdapter
import com.example.clickchat.RecyclerViewStory.StoryObject

class ChatFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private var uid: String? = null

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        uid = FirebaseAuth.getInstance().uid

        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = StoryAdapter(getDataSet(), context!!)
        mRecyclerView.adapter = mAdapter

        val mRefresh: Button = view.findViewById(R.id.refresh)
        mRefresh.setOnClickListener {
            clear()
            listenForData()
        }

        return view
    }

    private fun clear() {
        val size = results.size
        results.clear()
        mAdapter.notifyItemRangeChanged(0, size)
    }

    private val results = ArrayList<StoryObject>()
    private fun getDataSet(): ArrayList<StoryObject> {
        listenForData()
        return results
    }

    private fun listenForData() {
        val receivedDb = FirebaseDatabase.getInstance().getReference("users").child(uid!!).child("received")
        receivedDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        getUserInfo(snapshot.key!!)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getUserInfo(chatUid: String) {
        val chatUserDb = FirebaseDatabase.getInstance().getReference("users").child(chatUid)
        chatUserDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val email = dataSnapshot.child("email").value.toString()
                    val uid = dataSnapshot.ref.key.toString()

                    val obj = StoryObject(email, uid, "chat")
                    if (!results.contains(obj)) {
                        results.add(obj)
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
