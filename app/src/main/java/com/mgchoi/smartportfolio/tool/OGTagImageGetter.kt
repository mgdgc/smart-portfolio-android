package com.mgchoi.smartportfolio.tool

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.jsoup.Jsoup
import java.net.HttpURLConnection
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
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.doInput = true
            conn.doOutput = true
            conn.useCaches = true

            conn.connect()

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val istream = conn.inputStream
                return BitmapFactory.decodeStream(istream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}