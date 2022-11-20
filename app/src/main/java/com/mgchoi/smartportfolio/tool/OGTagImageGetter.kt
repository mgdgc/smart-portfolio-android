package com.mgchoi.smartportfolio.tool

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.jsoup.Jsoup
import java.net.URL

class OGTagImageGetter {

    fun getOGImageUrl(url: String): String? {
        try {
            val conn = Jsoup.connect(url)
            val doc = conn.get()
            val element = doc.select("meta[property^=og:image]")
            if (element.size <= 0) {
                return null
            }

            return element[0].attr("content")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getImageFromUrl(urlString: String): Bitmap? {
        try {
            return BitmapFactory.decodeStream(URL(urlString).openStream())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}