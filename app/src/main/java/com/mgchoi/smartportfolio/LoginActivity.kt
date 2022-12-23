package com.mgchoi.smartportfolio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.mgchoi.smartportfolio.databinding.ActivityLoginBinding
import com.mgchoi.smartportfolio.tool.EncryptedAdminAccountTool
import com.mgchoi.smartportfolio.value.SharedPreferenceKeys

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        // 생체인증 로그인이 활성화 되어있으면 생체인증으로 로그인
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (pref.getBoolean(SharedPreferenceKeys.BOOL_BIOMETRIC, false)
        ) {
            biometric()
        }
    }

    private fun initView() {
        // 로그인 버튼
        binding.btnLogin.setOnClickListener {
            // 로그인 폼 정보 가져오기
            val adminId = binding.editTextLoginUsername.text.toString().trim()
            val adminPw = binding.editTextLoginPassword.text.toString().trim()

            // 입력 확인
            if (adminId.isEmpty() || adminPw.isEmpty()) {
                Snackbar.make(binding.root, R.string.login_not_valid, Snackbar.LENGTH_LONG)
                    .setAction(R.string.confirm) { }.show()
                return@setOnClickListener
            }

            // 저장된 ID 및 비밀번호 가져오기
            val accountTool = EncryptedAdminAccountTool(this)
            val sAdminId = accountTool.getAdminId()
            val sAdminPw = accountTool.getAdminPw()

            // ID 확인
            if (sAdminId != adminId) {
                Snackbar.make(binding.root, R.string.login_id_not_valid, Snackbar.LENGTH_LONG)
                    .setAction(R.string.confirm) { }.show()
                return@setOnClickListener
            }

            // 비밀번호 확인
            if (sAdminPw != adminPw) {
                Snackbar.make(binding.root, R.string.login_pw_not_valid, Snackbar.LENGTH_LONG)
                    .setAction(R.string.confirm) { }.show()
                return@setOnClickListener
            }

            // 로그인 정보 저장
            MyApplication.login = true

            // 로딩화면으로 이동
            val intent = Intent(this@LoginActivity, LoadingActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        // 방문 버튼
        binding.btnStart.setOnClickListener {
            val intent = Intent(this@LoginActivity, LoadingActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        // 도움 버튼
        binding.layoutLoginHelp.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.apply {
                setTitle(R.string.login_help)
                setMessage(R.string.login_help_dialog)
                setPositiveButton(R.string.close) { d, _ -> d.dismiss() }
                show()
            }
        }
    }

    private fun biometric() {
        val executer = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(this, executer, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@LoginActivity, errString, Toast.LENGTH_LONG).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // 로그인 정보 저장
                    MyApplication.login = true

                    // 로딩화면으로 이동
                    val intent = Intent(this@LoginActivity, LoadingActivity::class.java)
                    startActivity(intent)
                    this@LoginActivity.finish()
                }
            })

        biometricPrompt.authenticate(getPromptInfo())
    }

    private fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(getString(R.string.login_biometric_title))
            setSubtitle(getString(R.string.login_biometric_subtitle))
            // 안드로이드 11 이상의 얼굴인식 사용
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
            } else {
                setNegativeButtonText(getString(R.string.login_biometric_negative))
            }
        }.build()
    }

}