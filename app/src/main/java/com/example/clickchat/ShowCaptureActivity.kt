package com.example.clickchat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class ShowCaptureActivity : AppCompatActivity() {
    private var uid: String? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_capture)

        uid = FirebaseAuth.getInstance().uid

        imagePath = intent.getStringExtra("imagePath")
        if (imagePath == null || imagePath!!.isEmpty()) {
            finish()
            return
        }

        val imageFile = File(imagePath!!)
        if (!imageFile.exists()) {
            finish()
            return
        }

        val bitmap = BitmapFactory.decodeFile(imagePath)
        val mImage = findViewById<ImageView>(R.id.imageCaptured)
        mImage.setImageBitmap(bitmap)

        val mSend: Button = findViewById(R.id.send)
        mSend.setOnClickListener {
            navigateToChooseReceiverActivity(imageFile)
        }
    }

    private fun navigateToChooseReceiverActivity(imageFile: File) {
        val imageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile
        )

        val intent = Intent(this, ChooseReceiverActivity::class.java)
        intent.putExtra("imageUri", imageUri.toString())

        // Add error handling or logging if necessary
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Handle exception, e.g., log error, show error message
            e.printStackTrace()
        }
    }

}
