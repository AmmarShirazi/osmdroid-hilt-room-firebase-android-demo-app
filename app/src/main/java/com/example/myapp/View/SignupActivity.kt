package com.example.myapp.View

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapp.R
import com.example.myapp.Utils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signup: Button
    private lateinit var loginHyperlink: TextView
    private lateinit var locationPickerImage: ImageView
    private lateinit var addLocation: TextView
    private lateinit var nameField: EditText
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private var address: String = "nil res"
    private val getAddress = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            addLocation.text = "Address Updated"
            address = result.data?.getStringExtra("selectedAddress").toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        auth = Firebase.auth
        addLocation = findViewById(R.id.add_address_hyperlink)
        signup = findViewById(R.id.signup_button)
        loginHyperlink = findViewById(R.id.login_hyperlink)
        locationPickerImage = findViewById(R.id.location_picker_image)
        nameField = findViewById(R.id.name_field)
        emailField = findViewById(R.id.email_field)
        passwordField = findViewById(R.id.password_field)


        addLocation.setOnClickListener {
            getAddress.launch(Intent(this@SignupActivity, MapActivity::class.java))
        }

        locationPickerImage.setOnClickListener {
            getAddress.launch(Intent(this@SignupActivity, MapActivity::class.java))
        }

        signup.setOnClickListener{
            registerUser()
        }

        loginHyperlink.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }

    }

    private fun registerUser() {

        if (Utils.isEmpty(nameField) || Utils.isEmpty(emailField) || Utils.isEmpty(passwordField) || address == "nil res") {
            return
        }

        auth.createUserWithEmailAndPassword(emailField.text.toString(), passwordField.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nameField.text.toString())
                        .build()

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("INFO", "User profile updated.")
                            }
                        }
                    addUserToFirestore(user.uid, nameField.text.toString(), address)

                    updateUI(user)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Sign up failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                    Log.w("ERROR", "createUserWithEmail:failure", task.exception)
                }
            }
    }

    private fun addUserToFirestore(uid: String, displayName: String, address: String) {
        val user = hashMapOf(
            "uid" to uid,
            "displayName" to displayName,
            "address" to address
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("INFO", "User information added to Firestore.")
            }
            .addOnFailureListener { e ->
                Log.w("INFO", "Error adding user information", e)
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }
        passwordField.setText("");
    }





}