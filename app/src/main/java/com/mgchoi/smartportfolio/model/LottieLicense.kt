package com.mgchoi.smartportfolio.model

import android.content.res.AssetManager
import com.google.gson.Gson

class LottieLicense(
    var name: String,
    var url: String,
    var designer: String
) {

    companion object {
        private const val FILE_JSON = "lottie.json"

        fun fromJson(assets: AssetManager): Array<LottieLicense> {
            val istream = assets.open(FILE_JSON)
            val buffer = ByteArray(istream.available()) { 0 }
            istream.read(buffer)
            istream.close()

            val json = String(buffer)
            return Gson().fromJson(json, Array<LottieLicense>::class.java)
        }
    }

}