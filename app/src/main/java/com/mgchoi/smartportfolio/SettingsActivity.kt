package com.mgchoi.smartportfolio

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mgchoi.smartportfolio.databinding.ActivitySettingsBinding
import com.mgchoi.smartportfolio.frament.SettingsFragment

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

}