package com.mgchoi.smartportfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.snackbar.Snackbar
import com.mgchoi.smartportfolio.databinding.ActivityLoginBinding
import com.mgchoi.smartportfolio.tool.EncryptedAdminAccountTool

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
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
}