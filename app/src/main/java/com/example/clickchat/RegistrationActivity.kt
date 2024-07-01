package com.example.clickchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.NonNull
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
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

        firebaseAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(@NonNull firebaseAuth: FirebaseAuth) {
                val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
                if (user != null) {
                    val intent = Intent(getApplication(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                    return
                }
            }
        }

        mAuth = FirebaseAuth.getInstance()

        mRegistration = findViewById(R.id.registration)
        mName = findViewById(R.id.name)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)


        mRegistration!!.setOnClickListener {
            val name: String = mName.getText().toString()
            val email: String = mEmail.getText().toString()
            val password: String = mPassword.getText().toString()
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                this@RegistrationActivity,
                object : OnCompleteListener<AuthResult?> {
                    override fun onComplete(@NonNull task: Task<AuthResult>) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplication(), "Sign in ERROR", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val userId: String = mAuth.getCurrentUser().getUid()
                            val currentUserDb: DatabaseReference =
                                FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(userId)

                            val userInfo: MutableMap<*, *> = HashMap<Any, Any>()
                            userInfo["email"] = email
                            userInfo["name"] = name
                            userInfo["profileImageUrl"] = "default"

                            currentUserDb.updateChildren(userInfo)
                        }
                    }
                })
        }
    }

    protected fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    protected fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
}