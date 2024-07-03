package com.example.clickchat.loginRegistration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clickchat.MainActivity
import com.example.clickchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private var mLogin: Button? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null

    private var mAuth: FirebaseAuth? = null
    private var firebaseAuthStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        firebaseAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user: FirebaseUser? = firebaseAuth.currentUser
            if (user != null) {
                startActivity(Intent(applicationContext, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
                finish()
            }
        }

        mLogin = findViewById(R.id.login)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)

        mLogin?.setOnClickListener {
            val email = mEmail?.text.toString().trim()
            val password = mPassword?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Sign in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(firebaseAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        mAuth?.removeAuthStateListener(firebaseAuthStateListener!!)
    }
}
