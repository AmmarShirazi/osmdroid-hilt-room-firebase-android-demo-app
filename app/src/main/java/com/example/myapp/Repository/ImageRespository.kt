package com.example.myapp.Repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.myapp.Dao.ImageModelDAO
import com.example.myapp.Model.DisplayImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageRepository @Inject constructor (private val dao: ImageModelDAO) {

    val allImages: Flow<List<DisplayImageModel>> = dao.getAll()
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(imageInstance: DisplayImageModel) = withContext(Dispatchers.IO) {
        dao.insert(imageInstance)
        Log.d("DB", "Image Inserted")
    }

    suspend fun delete(imageInstance: DisplayImageModel) = withContext(Dispatchers.IO) {
        dao.delete(imageInstance)
    }

}