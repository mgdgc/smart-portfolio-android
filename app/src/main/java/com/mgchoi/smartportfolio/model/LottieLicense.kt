package com.mgchoi.smartportfolio.model

import android.content.res.AssetManager
import com.google.gson.Gson
import com.mgchoi.smartportfolio.tool.JsonAssetReader

class LottieLicense(
    var name: String,
    var url: String,
    var designer: String
) {

    companion object {
        private const val FILE_JSON = "lottie.json"

        fun fromJson(assets: AssetManager): Array<LottieLicense> {
            val json = JsonAssetReader.readStringFromJson(assets, FILE_JSON)
            return Gson().fromJson(json, Array<LottieLicense>::class.java)
        }
    }

}