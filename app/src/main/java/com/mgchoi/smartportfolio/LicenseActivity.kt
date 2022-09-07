package com.mgchoi.smartportfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import com.mgchoi.smartportfolio.databinding.ActivityLicenseBinding
import com.mgchoi.smartportfolio.model.License

class LicenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLicenseBinding

    private var data: ArrayList<License> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbarLicense)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initializations
        initData()
        initListView()
    }

    private fun initData() {
        this.data.addAll(License.fromJson(assets))
    }

    private fun initListView() {
        val arrayList = arrayListOf<MutableMap<String, String>>()
        this.data.forEach {
            val items = mutableMapOf<String, String>()
            items["item1"] = it.name
            items["item2"] = "${it.user}, ${it.license}"
            arrayList.add(items)
        }

        val adapter = SimpleAdapter(
            this, arrayList, android.R.layout.simple_list_item_2,
            arrayOf("item1", "item2"), intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        binding.listLicense.adapter = adapter
        binding.listLicense.setOnItemClickListener { parent, view, position, id ->
            WebViewActivity.openAsCustomTab(this@LicenseActivity, data[position].url)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}