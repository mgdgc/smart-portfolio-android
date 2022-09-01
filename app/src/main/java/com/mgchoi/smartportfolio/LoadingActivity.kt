package com.mgchoi.smartportfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieDrawable
import com.mgchoi.smartportfolio.databinding.ActivityLoadingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class LoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingBinding
    private lateinit var animations: Array<String>

    private var splashTime = Random.nextInt(1000, 3000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializations
        animations = resources.getStringArray(R.array.lottie_url)

        initAnimation()
        initProgressBar()

        // Works
        startLoading()
    }

    private fun initAnimation() {
        val animation = animations[Random.nextInt(animations.size)]
        binding.lottieLoading.setAnimationFromUrl(animation)
        binding.lottieLoading.repeatCount = LottieDrawable.INFINITE
        binding.lottieLoading.playAnimation()
    }

    private fun initProgressBar() {
        binding.progressLoading.max = splashTime
    }

    private fun startLoading() {
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until splashTime) {
                binding.progressLoading.progress = i
                delay(1)
            }
            val intent = Intent(this@LoadingActivity, MainActivity::class.java)
            startActivity(intent)
            this@LoadingActivity.finish()
        }
    }

}