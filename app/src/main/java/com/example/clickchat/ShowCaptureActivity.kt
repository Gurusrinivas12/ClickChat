package com.example.clickchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class ShowCaptureActivity : AppCompatActivity() {

    private lateinit var rotateBitmap: Bitmap
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_capture)

        val extras: Bundle = intent.extras!!
        val b = extras.getByteArray("capture")

        if (b != null) {
            val image: ImageView = findViewById(R.id.imageCaptured)
            val decodedBitmap: Bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
            rotateBitmap = rotate(decodedBitmap)
            image.setImageBitmap(rotateBitmap)
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            uid = user.uid
            Log.d("ShowCaptureActivity", "User ID: $uid")
        } else {
            Log.e("ShowCaptureActivity", "User is not signed in")
            // Handle user not signed in
            return
        }

        val mStory: Button = findViewById(R.id.story)
        mStory.setOnClickListener { saveToStories() }
    }

    private fun saveToStories() {
        val userStoryDb: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("story")
        val key: String = userStoryDb.push().key!!
        Log.d("ShowCaptureActivity", "Database Key: $key")

        val filePath: StorageReference =
            FirebaseStorage.getInstance().getReference().child("captures").child(key)
        Log.d("ShowCaptureActivity", "Storage Path: ${filePath.path}")

        val baos = ByteArrayOutputStream()
        rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val dataToUpload = baos.toByteArray()
        val uploadTask: UploadTask = filePath.putBytes(dataToUpload)

        uploadTask.addOnSuccessListener {
            Log.d("ShowCaptureActivity", "Upload Successful")
            filePath.downloadUrl.addOnSuccessListener { imageUrl ->
                Log.d("ShowCaptureActivity", "Download URL: $imageUrl")

                val currentTimestamp = System.currentTimeMillis()
                val endTimestamp = currentTimestamp + (24 * 60 * 60 * 1000)

                val mapToUpload: MutableMap<String, Any> = HashMap()
                mapToUpload["imageUrl"] = imageUrl.toString()
                mapToUpload["timestampBeg"] = currentTimestamp
                mapToUpload["timestampEnd"] = endTimestamp

                userStoryDb.child(key).setValue(mapToUpload)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("ShowCaptureActivity", "Story Saved Successfully")
                            finish()
                        } else {
                            Log.e("ShowCaptureActivity", "Story Save Failed: ${task.exception}")
                        }
                    }
            }.addOnFailureListener { e ->
                Log.e("ShowCaptureActivity", "Download URL Failed: ${e.message}")
                finish()
            }
        }.addOnFailureListener { e ->
            Log.e("ShowCaptureActivity", "Upload Failed: ${e.message}")
            e.printStackTrace()
            finish()
        }
    }

    private fun rotate(decodedBitmap: Bitmap): Bitmap {
        val w = decodedBitmap.width
        val h = decodedBitmap.height

        val matrix = Matrix()
        matrix.setRotate(90f)

        return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true)
    }
}
