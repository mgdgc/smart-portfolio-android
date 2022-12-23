package com.mgchoi.smartportfolio.tool

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedAdminAccountTool(private val context: Context) {

    companion object {
        const val FILE_ADMIN = "admin_account"
        const val KEY_ADMIN_ID = "admin_id"
        const val KEY_ADMIN_PW = "admin_pw"
    }

    fun getEncryptedSharedPreference(): SharedPreferences {
        val masterKey = MasterKey.Builder(
            context.applicationContext,
            MasterKey.DEFAULT_MASTER_KEY_ALIAS
        ).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        return EncryptedSharedPreferences.create(
            context.applicationContext,
            FILE_ADMIN,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun clear() {
        val pref = getEncryptedSharedPreference()
        val edit = pref.edit()
        edit.clear()
        edit.apply()
    }

    fun setAdminId(newId: String) {
        val pref = getEncryptedSharedPreference()
        val edit = pref.edit()
        edit.putString(KEY_ADMIN_ID, newId)
        edit.apply()
    }

    fun setAdminPw(newPw: String) {
        val pref = getEncryptedSharedPreference()
        val edit = pref.edit()
        edit.putString(KEY_ADMIN_PW, newPw)
        edit.apply()
    }

    fun getAdminId(): String {
        val pref = getEncryptedSharedPreference()
        return pref.getString(KEY_ADMIN_ID, "admin") ?: "admin"
    }

    fun getAdminPw(): String {
        val pref = getEncryptedSharedPreference()
        return pref.getString(KEY_ADMIN_PW, "admin") ?: "admin"
    }

}