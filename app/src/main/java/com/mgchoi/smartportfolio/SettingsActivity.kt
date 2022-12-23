package com.mgchoi.smartportfolio

import android.content.Intent
import android.content.pm.PackageManager.PackageInfoFlags
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import com.mgchoi.smartportfolio.databinding.ActivitySettingsBinding
import com.mgchoi.smartportfolio.databinding.LayoutPwChangeBinding
import com.mgchoi.smartportfolio.tool.DBManager
import com.mgchoi.smartportfolio.tool.EncryptedAdminAccountTool

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

            // 로그인이 되어 있지 않으면 로그인 카테고리 숨김
            if (!MyApplication.login) {
                val catAccount = findPreference<PreferenceCategory>("category_account")
                catAccount?.isVisible = false
            } else {
                // 저장된 ID 및 비밀번호
                val accountTool = EncryptedAdminAccountTool(requireContext())

                // Admin ID
                val prefAdminId = findPreference<Preference>("admin_id")
                prefAdminId?.summary = accountTool.getAdminId()
                prefAdminId?.setOnPreferenceClickListener {
                    // 변경할 ID 입력칸
                    val editText = EditText(requireContext())
                    // ID 변경 다이얼로그
                    val dialog = AlertDialog.Builder(requireContext())
                    dialog.apply {
                        setTitle(R.string.settings_admin_id)
                        setMessage(R.string.settings_admin_id_dialog)
                        setView(editText)
                        setPositiveButton(R.string.confirm) { d, _ ->
                            val newId = editText.text.toString().trim()

                            // 빈칸 확인
                            if (newId.isEmpty()) {
                                Snackbar.make(
                                    requireView(),
                                    R.string.settings_admin_id_not_valid,
                                    Snackbar.LENGTH_LONG
                                ).setAction(R.string.confirm) { }.show()
                                return@setPositiveButton
                            }

                            // 새 ID 저장
                            accountTool.setAdminId(newId)
                            prefAdminId.summary = newId
                            d.dismiss()
                        }
                        setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
                        show()
                    }
                    true
                }

                // Admin PW
                val prefAdminPw = findPreference<Preference>("admin_pw")
                prefAdminPw?.setOnPreferenceClickListener {
                    // 비밀번호 변경 레이아웃
                    val formBinding = LayoutPwChangeBinding.inflate(layoutInflater)
                    // 비밀번호 변경 다이얼로그
                    val dialog = AlertDialog.Builder(requireContext())
                    dialog.apply {
                        setTitle(R.string.settings_admin_pw)
                        setView(formBinding.root)
                        setPositiveButton(R.string.confirm) { d, _ ->
                            // 입력된 비밀번호 가져오기
                            val currPw =
                                formBinding.editSettingsAdminPwCurrent.text.toString().trim()
                            val newPw = formBinding.editSettingsAdminPwNew.text.toString().trim()
                            val chkPw = formBinding.editSettingsAdminPwCheck.text.toString().trim()

                            // 현재 비밀번호 확인
                            if (accountTool.getAdminPw() != currPw) {
                                Snackbar.make(
                                    requireView(),
                                    R.string.settings_admin_pw_not_valid,
                                    Snackbar.LENGTH_LONG
                                ).setAction(R.string.confirm) { }.show()
                                return@setPositiveButton
                            }

                            // 비밀번호 확인
                            if (newPw != chkPw) {
                                Snackbar.make(
                                    requireView(),
                                    R.string.settings_admin_pw_check_not_valid,
                                    Snackbar.LENGTH_LONG
                                ).setAction(R.string.confirm) { }.show()
                                return@setPositiveButton
                            }

                            // 새 비밀번호 저장
                            accountTool.setAdminPw(newPw)

                            // 비밀번호 변경 알림
                            Snackbar.make(
                                requireView(),
                                R.string.settings_admin_pw_success,
                                Snackbar.LENGTH_LONG
                            ).setAction(R.string.confirm) { }.show()

                            d.dismiss()
                        }
                        setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
                        show()
                    }

                    true
                }

                // Biometric login
                val bioPref = findPreference<SwitchPreferenceCompat>("biometric_login")
                val bioManager = BiometricManager.from(requireContext())
                // 생체인증을 할 수 있는지 여부 확인
                val canAuth =
                    bioManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                if (canAuth != BiometricManager.BIOMETRIC_SUCCESS
                    && canAuth != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
                ) {
                    bioPref?.isVisible = false
                }
                // 생체인증을 켰을 때
                bioPref?.setOnPreferenceChangeListener { preference, newValue ->
                    // 생체인증 정보가 등록되지 않았으면 스위치 변경 무력화
                    if (canAuth == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                        Snackbar.make(
                            requireView(),
                            R.string.settings_biometric_none_enrolled,
                            Snackbar.LENGTH_LONG
                        ).setAction(R.string.confirm) { }.show()
                        return@setOnPreferenceChangeListener false
                    }
                    true
                }

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
            }

            // DB reset
            findPreference<Preference>(KEY_RESET)?.setOnPreferenceClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(R.string.settings_db_reset)
                        .setMessage(R.string.settings_db_reset_dialog)
                        .setPositiveButton(R.string.cancel) { d, _ -> d.dismiss() } // 실수 삭제 방지를 위해 Positive/ Negative 반대로 사용
                        .setNegativeButton(R.string.reset) { d, i ->
                            val manager = DBManager(requireContext())
                            manager.resetDB()
                            d.dismiss()
                        }
                        .show()
                }
                true
            }

            // DB init
            findPreference<Preference>(KEY_INIT)?.setOnPreferenceClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(R.string.settings_db_init)
                        .setMessage(R.string.settings_db_init_dialog)
                        .setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
                        .setPositiveButton(R.string.confirm) { d, i ->
                            val manager = DBManager(requireContext())
                            manager.initDB()
                            d.dismiss()
                        }
                        .show()
                }
                true
            }

            // Version and build
            val pm = requireContext().packageManager
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(requireContext().packageName, PackageInfoFlags.of(0))
            } else {
                pm.getPackageInfo(requireContext().packageName, 0)
            }
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