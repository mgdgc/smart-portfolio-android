package com.mgchoi.smartportfolio.model

import android.content.res.AssetManager
import com.google.gson.Gson

class License(
    var name: String,
    var user: String,
    var url: String,
    var license: String
) {

    companion object {
        private const val FILE_JSON = "licenses.json"

        fun fromJson(assets: AssetManager): Array<License> {
            val istream = assets.open(FILE_JSON)
            val buffer = ByteArray(istream.available()) { 0 }
            istream.read(buffer)
            istream.close()

            val json = String(buffer)
            return Gson().fromJson(json, Array<License>::class.java)
        }
    }

}