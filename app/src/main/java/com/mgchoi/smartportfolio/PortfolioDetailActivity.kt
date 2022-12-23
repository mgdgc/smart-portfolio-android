package com.mgchoi.smartportfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import com.mgchoi.smartportfolio.databinding.ActivityPortfolioDetailBinding
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.tool.CoverImageHelper
import com.mgchoi.smartportfolio.tool.OGTagImageGetter
import com.mgchoi.smartportfolio.value.SharedPreferenceKeys
import kotlinx.coroutines.*

class PortfolioDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PORTFOLIO_ID = "portfolioId"
    }

    private lateinit var binding: ActivityPortfolioDetailBinding
    private lateinit var portfolio: Portfolio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        // Toolbar home 이미지 설정
        initToolbar()

        // Intent data 가져오기
        initIntentData()
        // Toolbar Image 설정
        setToolbarData()
        // Data를 View에 설정
        setData()
        initView()
    }

    private fun initToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_black_24)
    }

    private fun initIntentData() {
        // Intent에서 portfolioId 가져오기
        val portfolioId = intent.getIntExtra(EXTRA_PORTFOLIO_ID, -1)
        // portfolioId가 넘어오지 않았다면 Activity 종료
        if (portfolioId < 0) {
            this.finish()
            return
        }

        // DAO에서 Portfolio 가져오기
        val dao = PortfolioDAO(this)
        val portfolio = dao.select(portfolioId)
        // portfolio가 없으면 Activity 종료
        if (portfolio == null) {
            this.finish()
            return
        }

        this.portfolio = portfolio
    }

    private fun setToolbarData() {
        // 이미지 가져오기
        if (portfolio.image != null) {
            val coverImageHelper = CoverImageHelper(this)
            val bitmap = coverImageHelper.read(portfolio.image!!)
            binding.imgPortfolioDetailCover.setImageBitmap(bitmap)
        } else {
            // OGTag Image
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                val manager = OGTagImageGetter()
                val url = manager.getOGImageUrl(portfolio.url)

                if (url != null) {
                    val image = manager.getImageFromUrl(url)
                    if (image != null) {
                        withContext(Dispatchers.Main) {
                            binding.imgPortfolioDetailCover.setImageBitmap(image)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.imgPortfolioDetailCover.visibility = View.GONE
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.imgPortfolioDetailCover.visibility = View.GONE
                    }
                }
            }
        }

        // 타이틀 설정
        supportActionBar?.title = ""
    }

    private fun setData() {
        binding.txtPortfolioDetailTitle.text = portfolio.title
        binding.txtPortfolioDetailContent.text = portfolio.content
        binding.txtPortfolioDetailLink.text = portfolio.url
    }

    private fun initView() {
        // 링크 열기 버튼
        binding.layoutPortfolioDetailLink.setOnClickListener {
            openLink(portfolio.url)
        }
    }

    private fun openLink(url: String) {
        val pref = getSharedPreferences(SharedPreferenceKeys.PREF_APP, 0)
        when (pref.getInt(SharedPreferenceKeys.INT_BROWSER, 0)) {
            0 -> {
                // Checkbox for remember decision
                val checkbox = CheckBox(this).apply {
                    this.setText(R.string.portfolio_browser_remember)
                }

                // Alert dialog for choosing browser
                val alert = AlertDialog.Builder(this)
                alert.setTitle(R.string.portfolio_browser_title)
                    .setMessage(R.string.portfolio_browser_content)
                    .setView(checkbox)
                    .setPositiveButton(R.string.portfolio_browser_chrome) { d, _ ->
                        pref.edit().putInt(
                            SharedPreferenceKeys.INT_BROWSER,
                            if (checkbox.isChecked) 2 else 0
                        ).apply()
                        d.dismiss()
                        openLinkAsChromeCustomTab(url)
                    }
                    .setNegativeButton(R.string.portfolio_browser_internal) { d, _ ->
                        pref.edit().putInt(
                            SharedPreferenceKeys.INT_BROWSER,
                            if (checkbox.isChecked) 1 else 0
                        ).apply()
                        d.dismiss()
                        openLinkAsWebViewActivity(url)
                    }
                    .show()
            }
            1 -> openLinkAsWebViewActivity(url)
            2 -> openLinkAsChromeCustomTab(url)
        }
    }

    private fun openLinkAsWebViewActivity(url: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_URL, url)
        startActivity(intent)
    }

    private fun openLinkAsChromeCustomTab(url: String) {
        WebViewActivity.openAsCustomTab(this, url)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}