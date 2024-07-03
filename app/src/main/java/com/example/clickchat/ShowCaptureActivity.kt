package com.example.clickchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class ShowCaptureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_capture)

        val extras: Bundle = checkNotNull(getIntent().getExtras())
        val b = extras.getByteArray("capture")

        if (b != null) {
            val image: ImageView = findViewById(R.id.imageCaptured)

            val decodedBitmap: Bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)

            val rotateBitmap = rotate(decodedBitmap)


            image.setImageBitmap(rotateBitmap)
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