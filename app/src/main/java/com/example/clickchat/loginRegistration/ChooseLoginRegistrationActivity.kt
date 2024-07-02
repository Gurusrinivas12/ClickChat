package com.example.clickchat.loginRegistration

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.clickchat.R

class ChooseLoginRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login_registration)

        val mLogin: Button = findViewById(R.id.login)
        val mRegistration: Button = findViewById(R.id.registration)

        mLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(getApplication(), LoginActivity::class.java)
            startActivity(intent)
            return@OnClickListener

        })
        mRegistration.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(getApplication(), RegistrationActivity::class.java)
            startActivity(intent)
            return@OnClickListener
        })
    }
}