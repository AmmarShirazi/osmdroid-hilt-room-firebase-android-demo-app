package com.example.myapp.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapp.Model.DisplayImageModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageModelDAO {
    @Query("SELECT * FROM images")
    fun getAll(): Flow<List<DisplayImageModel>>

    @Query("SELECT * FROM images WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: List<String>): List<DisplayImageModel>

    @Query("SELECT * FROM images WHERE uid LIKE :uid LIMIT 1")
    fun findById(uid: String): DisplayImageModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: DisplayImageModel)

    @Delete
    fun delete(image: DisplayImageModel)
}