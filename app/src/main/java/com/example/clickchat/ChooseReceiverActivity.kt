package com.example.clickchat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.RecyclerViewReceiver.ReceiverAdapter
import com.example.clickchat.RecyclerViewReceiver.ReceiverObject
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ChooseReceiverActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private var Uid: String? = null
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_receiver)

        Uid = FirebaseAuth.getInstance().uid

        val imageUriString = intent.getStringExtra("imageUri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream)
            } else {
                showToast("Failed to open image input stream")
                finish()
                return
            }
        } else {
            showToast("Image URI is null")
            finish()
            return
        }

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = ReceiverAdapter(dataSet, this)
        mRecyclerView.adapter = mAdapter

        val mFab = findViewById<FloatingActionButton>(R.id.fab)
        mFab.setOnClickListener { saveToStories() }
    }

    private val results = ArrayList<ReceiverObject>()

    private val dataSet: ArrayList<ReceiverObject>
        get() {
            listenForData()
            return results
        }

    private fun listenForData() {
        for (i in UserInformation.listFollowing.indices) {
            val usersDb = FirebaseDatabase.getInstance().reference.child("users").child(UserInformation.listFollowing[i])
            usersDb.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val email = dataSnapshot.child("email").value?.toString() ?: ""
                    val uid = dataSnapshot.ref.key ?: ""
                    val obj = ReceiverObject(email, uid, false)
                    if (!results.contains(obj)) {
                        results.add(obj)
                        mAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    private fun saveToStories() {
        val userStoryDb = FirebaseDatabase.getInstance().reference.child("users").child(Uid!!).child("story")
        val key = userStoryDb.push().key ?: return

        val filePath = FirebaseStorage.getInstance().reference.child("captures").child(key)

        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val dataToUpload = baos.toByteArray()
        val uploadTask = filePath.putBytes(dataToUpload)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            filePath.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                val currentTimestamp = System.currentTimeMillis()
                val endTimestamp = currentTimestamp + (24 * 60 * 60 * 1000)

                val mStory = findViewById<CheckBox>(R.id.story)
                if (mStory.isChecked) {
                    val mapToUpload: MutableMap<String, Any> = HashMap()
                    mapToUpload["imageUrl"] = imageUrl
                    mapToUpload["timestampBeg"] = currentTimestamp
                    mapToUpload["timestampEnd"] = endTimestamp
                    userStoryDb.child(key).setValue(mapToUpload)
                }
                for (result in results) {
                    if (result.receive) {
                        val userDb = FirebaseDatabase.getInstance().reference.child("users").child(result.uid)
                            .child("received").child(Uid!!)
                        val mapToUpload: MutableMap<String, Any> = HashMap()
                        mapToUpload["imageUrl"] = imageUrl
                        mapToUpload["timestampBeg"] = currentTimestamp
                        mapToUpload["timestampEnd"] = endTimestamp
                        userDb.child(key).setValue(mapToUpload)
                    }
                }

                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            showToast("Failed to upload image")
            finish()
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}
