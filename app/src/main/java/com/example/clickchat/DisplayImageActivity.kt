package com.example.clickchat

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DisplayImageActivity : AppCompatActivity() {
    private var userId: String? = null
    private lateinit var mImage: ImageView
    private lateinit var progressBar: ProgressBar
    private var imageUrlList: ArrayList<String> = ArrayList()
    private var imageIterator = 0
    private var started = false
    private val handler = Handler(Looper.getMainLooper())
    private val imageDisplayRunnable = object : Runnable {
        override fun run() {
            changeImage()
            handler.postDelayed(this, IMAGE_DISPLAY_DURATION)
        }
    }

    companion object {
        private const val IMAGE_DISPLAY_DURATION = 5000L // 5 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        userId = intent.extras?.getString("userId")
        if (userId == null) {
            finish()
            return
        }

        mImage = findViewById(R.id.image)
        progressBar = findViewById(R.id.progress_bar)

        listenForData()
    }

    private fun listenForData() {
        val followingStoryDb = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
        followingStoryDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (storySnapshot in dataSnapshot.child("story").children) {
                    val timestampBeg = storySnapshot.child("timestampBeg").value?.toString()?.toLong() ?: continue
                    val timestampEnd = storySnapshot.child("timestampEnd").value?.toString()?.toLong() ?: continue
                    val imageUrl = storySnapshot.child("imageUrl").value?.toString() ?: continue

                    val timestampCurrent = System.currentTimeMillis()
                    if (timestampCurrent in timestampBeg..timestampEnd) {
                        imageUrlList.add(imageUrl)
                    }
                }
                if (!started && imageUrlList.isNotEmpty()) {
                    started = true
                    initializeDisplay()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun initializeDisplay() {
        displayImage()
        mImage.setOnClickListener { changeImage() }
        handler.postDelayed(imageDisplayRunnable, IMAGE_DISPLAY_DURATION)
    }

    private fun displayImage() {
        if (imageUrlList.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            Glide.with(this)
                .load(imageUrlList[imageIterator])
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(mImage)
        }
    }

    private fun changeImage() {
        imageIterator = (imageIterator + 1) % imageUrlList.size
        displayImage()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(imageDisplayRunnable)
    }
}
