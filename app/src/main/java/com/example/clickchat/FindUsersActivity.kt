package com.example.clickchat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.RecyclerviewFollow.RCAdpater
import com.example.clickchat.RecyclerviewFollow.UsersObject

class FindUsersActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private lateinit var mInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_users)

        mInput = findViewById(R.id.input)
        val mSearch: Button = findViewById(R.id.search)

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(false)
        mLayoutManager = LinearLayoutManager(applicationContext)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = RCAdpater(dataSet, applicationContext)
        mRecyclerView.adapter = mAdapter
    }

    private val results: ArrayList<UsersObject> = ArrayList()
    private val dataSet: ArrayList<UsersObject>
        get() = results
}