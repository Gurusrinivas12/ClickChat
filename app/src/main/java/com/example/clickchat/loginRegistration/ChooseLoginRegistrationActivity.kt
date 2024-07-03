package com.example.clickchat.loginRegistration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.clickchat.R

class ChooseLoginRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login_registration)

        val mLogin: Button = findViewById(R.id.login)
        val mRegistration: Button = findViewById(R.id.registration)

        mLogin.setOnClickListener {
            Log.d("ChooseLoginReg", "Login button clicked")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        mRegistration.setOnClickListener {
            Log.d("ChooseLoginReg", "Registration button clicked")
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}
