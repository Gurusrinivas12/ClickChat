package com.example.clickchat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.R
import com.example.clickchat.RecyclerViewStory.StoryAdapter
import com.example.clickchat.RecyclerViewStory.StoryObject
import com.example.clickchat.UserInformation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoryFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: StoryAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private val results = ArrayList<StoryObject>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_story, container, false)

        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.setNestedScrollingEnabled(false)
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = StoryAdapter(results, requireContext())
        mRecyclerView.adapter = mAdapter

        val mRefresh = view.findViewById<Button>(R.id.refresh)
        mRefresh.setOnClickListener {
            clear()
            listenForData()
        }

        listenForData()
        return view
    }

    private fun clear() {
        val size = results.size
        results.clear()
        mAdapter.notifyItemRangeRemoved(0, size)
    }

    private fun listenForData() {
        for (userId in UserInformation.listFollowing) {
            val followingStoryDb = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            followingStoryDb.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val email = dataSnapshot.child("email").value?.toString() ?: return
                    val uid = dataSnapshot.ref.key ?: return
                    val timestampCurrent = System.currentTimeMillis()

                    for (storySnapshot in dataSnapshot.child("story").children) {
                        val timestampBeg = storySnapshot.child("timestampBeg").value?.toString()?.toLong() ?: continue
                        val timestampEnd = storySnapshot.child("timestampEnd").value?.toString()?.toLong() ?: continue

                        if (timestampCurrent in timestampBeg..timestampEnd) {
                            val storyObject = StoryObject(email, uid)
                            if (!results.contains(storyObject)) {
                                results.add(storyObject)
                                mAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error if needed
                }
            })
        }
    }

    companion object {
        fun newInstance(): StoryFragment {
            return StoryFragment()
        }
    }
}
