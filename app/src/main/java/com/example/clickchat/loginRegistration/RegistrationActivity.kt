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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {
    private var mRegistration: Button? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mName: EditText? = null

    private var mAuth: FirebaseAuth? = null
    private var firebaseAuthStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

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

        mRegistration = findViewById(R.id.registration)
        mName = findViewById(R.id.name)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)

        mRegistration?.setOnClickListener {
            val name = mName?.text.toString().trim()
            val email = mEmail?.text.toString().trim()
            val password = mPassword?.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userId = mAuth?.currentUser?.uid ?: ""
                        val currentUserDb: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)

                        val userInfo: MutableMap<String, Any> = HashMap()
                        userInfo["email"] = email
                        userInfo["name"] = name
                        userInfo["profileImageUrl"] = "default"

                        currentUserDb.updateChildren(userInfo)
                        Toast.makeText(applicationContext, "Registration successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
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
