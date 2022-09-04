package com.mgchoi.smartportfolio.model

import android.content.res.AssetManager
import com.google.gson.Gson
import com.mgchoi.smartportfolio.tool.JsonAssetReader

class License(
    var name: String,
    var user: String,
    var url: String,
    var license: String
) {

    companion object {
        private const val FILE_JSON = "licenses.json"

        fun fromJson(assets: AssetManager): Array<License> {
            val json = JsonAssetReader.readStringFromJson(assets, FILE_JSON)
            return Gson().fromJson(json, Array<License>::class.java)
        }
    }

}