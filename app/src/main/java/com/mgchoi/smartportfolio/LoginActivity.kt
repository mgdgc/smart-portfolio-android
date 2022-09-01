package com.mgchoi.smartportfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mgchoi.smartportfolio.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@LoginActivity, LoadingActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}