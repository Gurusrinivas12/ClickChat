package com.example.clickchat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
    private val handler = Handler(Looper.getMainLooper())
    private val changeImageRunnable = object : Runnable {
        override fun run() {
            changeImage()
            handler.postDelayed(this, 5000)  // Schedule the next image change in 5 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        currentUid = FirebaseAuth.getInstance().uid

        val b = intent.extras
        userId = b?.getString("userId")
        chatOrStory = b?.getString("chatOrStory")

        Log.d("DisplayImageActivity", "Received userId: $userId, chatOrStory: $chatOrStory")

        mImage = findViewById(R.id.image)

        when (chatOrStory) {
            "chat" -> listenForChat()
            "story" -> listenForStory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(changeImageRunnable)  // Stop the handler when the activity is destroyed
    }

    private fun listenForChat() {
        val chatDb = FirebaseDatabase.getInstance().getReference("users").child(currentUid!!).child("received").child(userId!!)
        chatDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (chatSnapshot in dataSnapshot.children) {
                    val imageUrl = chatSnapshot.child("imageUrl").getValue(String::class.java)
                    imageUrl?.let {
                        Log.d("DisplayImageActivity", "Fetched chat image URL: $it")
                        imageUrlList.add(it)
                        if (!started) {
                            started = true
                            initializeDisplay()
                        }
                        chatDb.child(chatSnapshot.key!!).removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DisplayImageActivity", "Error fetching chat images: ${databaseError.message}")
            }
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
                            Log.d("DisplayImageActivity", "Fetched story image URL: $it")
                            imageUrlList.add(it)
                            if (!started) {
                                started = true
                                initializeDisplay()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DisplayImageActivity", "Error fetching stories: ${databaseError.message}")
            }
        })
    }

    private fun initializeDisplay() {
        if (imageUrlList.isNotEmpty()) {
            mImage.visibility = View.VISIBLE
            loadImage(imageUrlList[imageIterator])
            mImage.setOnClickListener { changeImage() }
            handler.postDelayed(changeImageRunnable, 5000)  // Start the handler to change the image every 5 seconds
        } else {
            Log.e("DisplayImageActivity", "Image URL list is empty.")
        }
    }

    private fun changeImage() {
        imageIterator++
        if (imageIterator < imageUrlList.size) {
            loadImage(imageUrlList[imageIterator])
        } else {
            finish()
        }
    }
    private fun loadImage(url: String) {
        Log.d("DisplayImageActivity", "Loading image URL: $url")
        Glide.with(this)
            .load(url)
            .into(mImage)
    }


}
