package com.example.clickchat

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.clickchat.R

class DisplayImageActivity : AppCompatActivity() {

    private var userId: String? = null
    private var currentUid: String? = null
    private var chatOrStory: String? = null

    private lateinit var mImage: ImageView
    private val imageUrlList = ArrayList<String>()
    private var started = false
    private var imageIterator = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        currentUid = FirebaseAuth.getInstance().uid

        val b = intent.extras
        userId = b?.getString("userId")
        chatOrStory = b?.getString("chatOrStory")

        mImage = findViewById(R.id.image)

        when (chatOrStory) {
            "chat" -> listenForChat()
            "story" -> listenForStory()
        }
    }

    private fun listenForChat() {
        val chatDb = FirebaseDatabase.getInstance().getReference("users").child(currentUid!!).child("received").child(userId!!)
        chatDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (chatSnapshot in dataSnapshot.children) {
                    val imageUrl = chatSnapshot.child("imageUrl").getValue(String::class.java)
                    imageUrl?.let {
                        imageUrlList.add(it)
                        if (!started) {
                            started = true
                            initializeDisplay()
                        }
                        chatDb.child(chatSnapshot.key!!).removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun listenForStory() {
        val followingStoryDb = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
        followingStoryDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (storySnapshot in dataSnapshot.child("story").children) {
                    val timestampBeg = storySnapshot.child("timestampBeg").getValue(Long::class.java) ?: 0L
                    val timestampEnd = storySnapshot.child("timestampEnd").getValue(Long::class.java) ?: 0L
                    val imageUrl = storySnapshot.child("imageUrl").getValue(String::class.java)
                    val timestampCurrent = System.currentTimeMillis()
                    if (timestampCurrent in timestampBeg..timestampEnd) {
                        imageUrl?.let {
                            imageUrlList.add(it)
                            if (!started) {
                                started = true
                                initializeDisplay()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun initializeDisplay() {
        Glide.with(applicationContext).load(imageUrlList[imageIterator]).into(mImage)
        mImage.setOnClickListener { changeImage() }
        val handler = Handler()
        val delay = 5000
        handler.postDelayed(object : Runnable {
            override fun run() {
                changeImage()
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }

    private fun changeImage() {
        if (imageIterator == imageUrlList.size - 1) {
            finish()
            return
        }
        imageIterator++
        Glide.with(applicationContext).load(imageUrlList[imageIterator]).into(mImage)
    }
}
