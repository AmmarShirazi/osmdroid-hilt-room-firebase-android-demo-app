package com.example.myapp

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.util.DisplayMetrics
import android.widget.EditText
import android.widget.ImageView

object Utils {
    fun isEmpty(etText: EditText): Boolean {
        if (etText.text.toString().trim { it <= ' ' }.isEmpty()) {
            etText.error = "Cannot be empty"
            return true
        }



        return false
    }


    fun placeRoundedImage(imgUri : Uri, activity: Activity, imageView: ImageView) {
        val imageStream = activity?.contentResolver?.openInputStream(imgUri)
        val selectedImage = BitmapFactory.decodeStream(imageStream)

        // You can call your method to create a rounded bitmap if you want
        val roundedImage = Utils.getRoundedCroppedBitmap(selectedImage)
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        imageView.minimumHeight = dm.heightPixels
        imageView.minimumWidth = dm.widthPixels
        imageView.setImageBitmap(roundedImage)
    }

    fun getRoundedCroppedBitmap(bitmap: Bitmap): Bitmap {
        val widthLight = bitmap.width
        val heightLight = bitmap.height
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height,
            Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paintColor = Paint()
        paintColor.flags = Paint.ANTI_ALIAS_FLAG
        val rectF = RectF(Rect(0, 0, widthLight, heightLight))
        canvas.drawRoundRect(rectF, (widthLight / 2).toFloat(), (heightLight / 2).toFloat(), paintColor)
        val paintImage = Paint()
        paintImage.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(bitmap, 0f, 0f, paintImage)
        return output
    }
}