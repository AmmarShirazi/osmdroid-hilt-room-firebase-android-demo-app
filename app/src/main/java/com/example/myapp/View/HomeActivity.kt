package com.example.myapp.View

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.MyApplication
import com.example.myapp.ViewModel.ImageViewModel
import com.example.myapp.Model.DisplayImageModel
import com.example.myapp.R
import com.example.myapp.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var tvDisplayName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvAddress: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var logout: Button
    private lateinit var userImage: ImageView

    private val imageViewModel: ImageViewModel by viewModels()


    private val fetchImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

        if (uri != null) {
            val uid: String = auth.uid.toString()
            val bit: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            val bytes = ByteArrayOutputStream()
            bit.compress(Bitmap.CompressFormat.JPEG,100,bytes)
            val path = MediaStore.Images.Media.insertImage(contentResolver, bit, uid,null)

            imageViewModel.insert(DisplayImageModel(auth.uid.toString(), path.toString()))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)



        auth = Firebase.auth
        tvDisplayName = findViewById(R.id.name_text)
        tvEmail = findViewById(R.id.email_text)
        tvAddress = findViewById(R.id.address_text)
        logout = findViewById(R.id.logout_button)
        userImage = findViewById(R.id.user_image)
        var currUid = auth.uid.toString()
        Log.d("WoW", "1- $currUid")



        imageViewModel.allImages.observe(this) { images ->
            images.let {
                Log.d("MSG", "Image Updated" + images.count().toString())

                for (image in it) {
                    Log.d("WoW", "2- " + image.uid)
                    if (image.uid == currUid) {
                        Log.d("MATCH", image.uid)
                        Utils.placeRoundedImage(Uri.parse(image.imgRef), this, userImage)
                    }
                }
            }

        }

        val user = auth.currentUser
        user?.let {
            tvDisplayName.text = "Display Name: " + user.displayName
            tvEmail.text = "Email: " + user.email
            getAddressFromFirestore(user.uid)
        }

        userImage.setOnClickListener {
            fetchImage.launch("image/*")
        }

        logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun getAddressFromFirestore(uid: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("INFO", "DocumentSnapshot data: ${document.data}")
                    val address = document.getString("address")
                    tvAddress.text = "Address: $address"
                } else {
                    Log.d("INFO", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("INFO", "get failed with ", exception)
            }
    }
}