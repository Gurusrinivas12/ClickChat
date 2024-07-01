package com.example.clickchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
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

        mLogin = findViewById(R.id.login)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)

        mLogin!!.setOnClickListener {
            val email: String = mEmail.getText().toString()
            val password: String = mPassword.getText().toString()
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                this@LoginActivity,
                object : OnCompleteListener<AuthResult?> {
                    override fun onComplete(@NonNull task: Task<AuthResult>) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(this@LoginActivity, "Sign in ERROR", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(firebaseAuthStateListener)
    }

    protected fun onStop() {
        super.onStop()
        mAuth?.removeAuthStateListener(firebaseAuthStateListener)
    }
}