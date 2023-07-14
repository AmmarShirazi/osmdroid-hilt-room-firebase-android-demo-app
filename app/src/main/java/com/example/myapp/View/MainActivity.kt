package com.example.myapp.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

class MainActivity : AppCompatActivity() {


     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startup_screen)


        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseAuth.getInstance().currentUser !=  null) {
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            }
            else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }

        }, 1000)

    }
}