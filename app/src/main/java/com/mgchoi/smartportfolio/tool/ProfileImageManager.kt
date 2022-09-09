package com.mgchoi.smartportfolio.tool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.mgchoi.smartportfolio.R
import java.io.File
import java.io.FileOutputStream

class ProfileImageManager(private val context: Context) {

    fun save(image: Bitmap): String? {
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val absolutePath = externalDir?.absolutePath
        val path = "$absolutePath/profile"
        val dir = File(path)

        if (!dir.exists()) {
            dir.mkdirs()
        }

        try {
            val timeMillis = System.currentTimeMillis()
            val file = File("$dir/$timeMillis.jpg")
            file.createNewFile()

            val fos = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            return file.name
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun read(filename: String): Bitmap? {
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val absolutePath = externalDir?.absolutePath
        val path = "$absolutePath/profile"
        val dir = File(path)

        if (!dir.exists()) {
            dir.mkdirs()
        }

        try {
            val file = File("$dir/$filename")

            if (!file.exists()) {
                return null
            }

            return BitmapFactory.decodeFile(file.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun defaultProfileImage(memberName: String? = null): Bitmap {
        val imageRes = intArrayOf(
            R.drawable.ic_avatar_01,
            R.drawable.ic_avatar_02,
            R.drawable.ic_avatar_03,
            R.drawable.ic_avatar_04
        )
        val index = (memberName ?: 0).hashCode() % imageRes.size

        return ContextCompat.getDrawable(context, imageRes[index])!!.toBitmap()
    }

}