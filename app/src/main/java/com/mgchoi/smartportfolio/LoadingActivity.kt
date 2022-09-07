package com.mgchoi.smartportfolio

import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
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

    private var splashTime = Random.nextLong(1000, 3000)

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
        binding.progressLoading.max = splashTime.toInt()
    }

    private fun startLoading() {
        val animator = ValueAnimator.ofInt(0, splashTime.toInt())
        animator.duration = splashTime
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { binding.progressLoading.progress = it.animatedValue as Int }
        animator.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@LoadingActivity, MainActivity::class.java)
            startActivity(intent)
            this@LoadingActivity.finish()
        }, splashTime)
    }

}