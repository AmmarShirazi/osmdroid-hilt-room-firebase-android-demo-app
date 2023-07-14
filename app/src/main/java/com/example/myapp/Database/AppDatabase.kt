package com.example.myapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapp.Dao.ImageModelDAO
import com.example.myapp.Model.DisplayImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [DisplayImageModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun displayImageModelDao(): ImageModelDAO



}