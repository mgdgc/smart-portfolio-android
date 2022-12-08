package com.mgchoi.smartportfolio

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.preference.PreferenceManager
import com.mgchoi.smartportfolio.tool.DBManager
import com.mgchoi.smartportfolio.value.SharedPreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LaunchActivity : AppCompatActivity() {

    private lateinit var initializer: DBManager
    private var splashAnimationDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12 Splash Screen
        installSplashScreen()
        // OnCreate
        super.onCreate(savedInstanceState)

        initializer = DBManager(this)

        initializer.initDB()

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (pref.getBoolean(SharedPreferenceKeys.BOOL_AUTO_LOGIN, false)) {
            launchMainActivity()
        } else {
            launchLoginActivity()
        }

        // 애니메이션이 끝나야 Splash가 종료되도록 viewTreeObserver 추가
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                return if (splashAnimationDone) {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        })

        // 애니메이션 500ms 동안 지속
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            splashAnimationDone = true
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