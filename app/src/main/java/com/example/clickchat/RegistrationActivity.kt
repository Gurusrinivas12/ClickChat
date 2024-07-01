package com.example.clickchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
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

        firebaseAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user: FirebaseUser? = firebaseAuth.currentUser
            if (user != null) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }

        mAuth = FirebaseAuth.getInstance()

        mRegistration = findViewById(R.id.registration)
        mName = findViewById(R.id.name)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)

        mRegistration?.setOnClickListener {
            val name = mName?.text.toString()
            val email = mEmail?.text.toString()
            val password = mPassword?.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(applicationContext, "Sign in ERROR", Toast.LENGTH_SHORT).show()
                    } else {
                        val userId = mAuth?.currentUser?.uid ?: ""
                        val currentUserDb: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId)

                        val userInfo: MutableMap<String, Any> = HashMap()
                        userInfo["email"] = email
                        userInfo["name"] = name
                        userInfo["profileImageUrl"] = "default"

                        currentUserDb.updateChildren(userInfo)
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuthStateListener?.let { mAuth?.addAuthStateListener(it) }
    }

    override fun onStop() {
        super.onStop()
        firebaseAuthStateListener?.let { mAuth?.removeAuthStateListener(it) }
    }
}