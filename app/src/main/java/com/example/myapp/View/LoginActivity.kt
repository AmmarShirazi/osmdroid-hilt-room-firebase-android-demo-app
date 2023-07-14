package com.example.myapp.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.R
import com.example.myapp.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val tag: String = "LOG"
    private lateinit var auth: FirebaseAuth
    private lateinit var loginButton: Button
    private lateinit var signupHyperlink: TextView
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity);

        loginButton = findViewById(R.id.login_button)
        signupHyperlink = findViewById(R.id.signup_hyperlink)
        emailField = findViewById(R.id.email_field)
        passwordField = findViewById(R.id.password_field)

        auth = Firebase.auth

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            signIn(email, password)
        }

        signupHyperlink.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            finish()
        }


    }

    private fun signIn(email: String, password: String) {

        if (Utils.isEmpty(emailField) || Utils.isEmpty(passwordField)) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(tag, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)

                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }
        passwordField.setText("");
    }

}