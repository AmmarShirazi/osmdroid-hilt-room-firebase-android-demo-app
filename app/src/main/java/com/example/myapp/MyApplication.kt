package com.example.myapp

import android.app.Application
import com.example.myapp.Database.AppDatabase
import com.example.myapp.Repository.ImageRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class MyApplication : Application() {

}