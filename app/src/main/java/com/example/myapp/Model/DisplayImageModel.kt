package com.example.myapp.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
class DisplayImageModel(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "imgRef") val imgRef: String?
)