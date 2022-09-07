package com.mgchoi.smartportfolio

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.mgchoi.smartportfolio.databinding.ActivitySettingsBinding
import com.mgchoi.smartportfolio.tool.DBManager

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SCROLL = "scroll"
    }

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbarSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Intent data
        val scrollTo = intent?.getStringExtra(EXTRA_SCROLL)

        // Settings fragment
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_settings, SettingsFragment(scrollTo))
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment(private val scrollToPreference: String? = null) :
        PreferenceFragmentCompat() {

        companion object {
            const val KEY_AUTO_LOGIN = "auto_login"
            const val KEY_LOGOUT = "logout"
            const val KEY_RESET = "reset_db"
            const val KEY_INIT = "init_db"
            const val KEY_VERSION = "version"
            const val KEY_BUILD = "build"
            const val KEY_LICENSE = "license"
            const val KEY_DEVELOPER = "developer"
            const val KEY_GITHUB = "github"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // Logout
            findPreference<Preference>(KEY_LOGOUT)?.setOnPreferenceClickListener {
                findPreference<SwitchPreferenceCompat>(KEY_AUTO_LOGIN)?.isChecked = false
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                true
            }

            // DB reset
            findPreference<Preference>(KEY_RESET)?.setOnPreferenceClickListener {
                val manager = DBManager(requireContext())
                manager.resetDB()
                true
            }

            // DB init
            findPreference<Preference>(KEY_INIT)?.setOnPreferenceClickListener {
                val manager = DBManager(requireContext())
                manager.initDB()
                true
            }

            // Version and build
            val pm = requireContext().packageManager
            val info = pm.getPackageInfo(requireContext().packageName, 0)
            val version = info.versionName
            val build = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode.toString()
            } else {
                info.versionCode.toString()
            }
            findPreference<Preference>(KEY_VERSION)?.summary = version
            findPreference<Preference>(KEY_BUILD)?.summary = build

            // License
            findPreference<Preference>(KEY_LICENSE)?.setOnPreferenceClickListener {
                val intent = Intent(requireContext(), LicenseActivity::class.java)
                startActivity(intent)
                true
            }

            // Developer
            findPreference<Preference>(KEY_DEVELOPER)?.setOnPreferenceClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(R.string.settings_developer)
                    .setMessage(R.string.settings_developer_detail)
                    .setPositiveButton(R.string.settings_developer_github) { d, _ ->
                        WebViewActivity.openAsCustomTab(
                            requireContext(),
                            "https://github.com/soc06212"
                        )
                        d.dismiss()
                    }
                    .setNegativeButton(R.string.settings_developer_portfolio) { d, _ ->
                        WebViewActivity.openAsCustomTab(
                            requireContext(),
                            "https://mgchoi.com"
                        )
                        d.dismiss()
                    }
                    .setNeutralButton(R.string.close) { d, _ -> d.dismiss() }
                    .show()
                true
            }

            // Github
            findPreference<Preference>(KEY_GITHUB)?.setOnPreferenceClickListener {
                WebViewActivity.openAsCustomTab(
                    requireContext(),
                    "https://github.com/soc06212/smart-portfolio-android"
                )
                true
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            scrollToPreference?.let {
                scrollToPreference(it)
            }
        }
    }

}