package com.mgchoi.smartportfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.mgchoi.smartportfolio.tool.DBManager
import com.mgchoi.smartportfolio.value.SharedPreferenceKeys

class LaunchActivity : AppCompatActivity() {

    private lateinit var initializer: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializer = DBManager(this)

        initializer.initDB()

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (pref.getBoolean(SharedPreferenceKeys.BOOL_AUTO_LOGIN, false)) {
            launchMainActivity()
        } else {
            launchLoginActivity()
        }
    }

    private fun launchMainActivity() {
        val intent = Intent(this@LaunchActivity, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun launchLoginActivity() {
        val intent = Intent(this@LaunchActivity, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }

}