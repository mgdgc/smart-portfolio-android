package com.mgchoi.smartportfolio.tool

import android.content.res.AssetManager
import com.mgchoi.smartportfolio.model.LottieLicense

class JsonAssetReader {

    companion object {
        fun readStringFromJson(assets: AssetManager, filename: String): String {
            val istream = assets.open(filename)
            val buffer = ByteArray(istream.available()) { 0 }
            istream.read(buffer)
            istream.close()

            return String(buffer)
        }
    }

}