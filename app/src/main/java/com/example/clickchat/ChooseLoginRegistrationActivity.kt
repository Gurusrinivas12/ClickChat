package com.example.clickchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ChooseLoginRegistrationActivity : AppCompatActivity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login_registration)

        val mLogin: Button = findViewById<Button>(R.id.login)
        val mRegistration: Button = findViewById<Button>(R.id.registration)

        mLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(getApplication(), LoginActivity::class.java)
            startActivity(intent)
            return@OnClickListener
        })
        mRegistration.setOnClickListener(View.OnClickListener {
            val intent = Intent(getApplication(), RegistrationActivity::class.java)
            startActivity(intent)
            return@OnClickListener
        })
    }
}