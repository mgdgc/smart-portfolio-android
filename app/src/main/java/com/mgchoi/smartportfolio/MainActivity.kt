package com.mgchoi.smartportfolio

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.mgchoi.smartportfolio.adapter.MainAdapter
import com.mgchoi.smartportfolio.databinding.ActivityMainBinding
import com.mgchoi.smartportfolio.databinding.HeaderNavMainBinding
import com.mgchoi.smartportfolio.frament.IndexFragment
import com.mgchoi.smartportfolio.frament.PortfolioFragment
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.ViewStyle

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ACTION_INDEX = 2000
        private const val ACTION_PORTFOLIO = 1000
        private const val ACTION_LICENSE = 2001
        private const val ACTION_INFO = 2002
        private const val ACTION_SETTINGS = 2003
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: HeaderNavMainBinding

    private lateinit var adapter: MainAdapter

    private var data: ArrayList<Member> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        headerBinding = HeaderNavMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)

        // Initializations
        initNavigationDrawer()
        initView()
        initHeaderView()
        initData()
        initFragments()
    }

    private fun initNavigationDrawer() {
        // Display hamburger menu
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        supportActionBar?.title = ""
    }

    private fun initView() {
        // Set listeners
        binding.imgBtnMainPrev.setOnClickListener {
            if (binding.pagerMain.currentItem > 0) {
                binding.pagerMain.currentItem--
            }
        }

        binding.imgBtnMainNext.setOnClickListener {
            if (binding.pagerMain.currentItem < adapter.itemCount) {
                binding.pagerMain.currentItem++
            }
        }

        binding.navMain.setNavigationItemSelectedListener { item ->
            binding.drawerMain.closeDrawer(GravityCompat.START)
            onNavigationItemSelected(item)
        }

    }

    private val onNavigationItemSelected: ((MenuItem) -> Boolean) = { item ->
        when (item.itemId) {
            in ACTION_PORTFOLIO until ACTION_INDEX -> {
                val memberId = item.itemId - ACTION_PORTFOLIO
                for (i in 0 until this.data.size) {
                    if (this.data[i].id == memberId) {
                        binding.pagerMain.currentItem = i + 1
                        break
                    }
                }
            }
            ACTION_INDEX -> {
                binding.pagerMain.currentItem = 0
            }
            ACTION_SETTINGS -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
            ACTION_LICENSE -> {
                startActivity(Intent(this@MainActivity, LicenseActivity::class.java))
            }
            ACTION_INFO -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra(
                    SettingsActivity.EXTRA_SCROLL,
                    SettingsActivity.SettingsFragment.KEY_VERSION
                )
                startActivity(intent)
            }
        }

        true
    }

    private fun initHeaderView() {
        // Attach header view
        binding.navMain.addHeaderView(headerBinding.root)

        // Set listeners
        headerBinding.imgBtnNavClose.setOnClickListener {
            binding.drawerMain.closeDrawer(GravityCompat.START)
        }

        headerBinding.imgBtnNavLogout.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            this@MainActivity.finish()
        }

    }

    private fun initData() {
        // TODO: replace with real code
        // Data initialization
//        val dao = MemberDAO(this)
//        data.addAll(dao.selectAll())
        // TODO: remove after
        data.addAll(
            arrayListOf(
                Member(1, "1", null, "1", ViewStyle.TIMELINE, true),
                Member(2, "2", null, "2", ViewStyle.MESSAGE, true),
                Member(3, "3", null, "3", ViewStyle.TIMELINE, true),
                Member(4, "4", null, "4", ViewStyle.CARD, true),
                Member(5, "5", null, "5", ViewStyle.MESSAGE, true)
            )
        )

        // Add menu to navigation drawer
        // Index menu
        binding.navMain.menu.addSubMenu(R.string.main_nav_submenu_title)
            .add(0, ACTION_INDEX, 0, R.string.main_nav_menu_index)
            .setIcon(R.drawable.ic_round_home_24)

        // Portfolio menu
        val portfolioGroup = binding.navMain.menu.addSubMenu(R.string.main_nav_submenu_portfolio)
        for (i in this.data.indices) {
            portfolioGroup.add(
                0,
                (ACTION_PORTFOLIO + this.data[i].id),
                0,
                "${i + 1}. ${data[i].name}"
            )
        }

        // Application menu
        val appGroup = binding.navMain.menu.addSubMenu(R.string.main_nav_submenu_app)
        appGroup.add(0, ACTION_SETTINGS, 0, R.string.main_nav_menu_settings)
            .setIcon(R.drawable.ic_baseline_settings_24)
        appGroup.add(0, ACTION_LICENSE, 0, R.string.main_nav_menu_license)
            .setIcon(R.drawable.ic_round_folder_open_24)
        appGroup.add(0, ACTION_INFO, 0, R.string.main_nav_menu_info)
            .setIcon(R.drawable.ic_round_info_24)
    }

    private fun initFragments() {
        // Initialize adapter
        adapter = MainAdapter(this)
        binding.pagerMain.adapter = adapter

        // Add fragments to adapter
        adapter.addFragments(IndexFragment.newInstance())
        for (member in data) {
            adapter.addFragments(PortfolioFragment(member))
        }

        // Attach indicator
        binding.indicatorMain.attachTo(binding.pagerMain)

        setPage(0)
    }

    private fun setPage(page: Int) {
        binding.pagerMain.currentItem = page % adapter.itemCount
    }

    fun setToolbarImage(member: Member? = null, image: Bitmap? = null) {
        if (member == null || image == null) {
            binding.imgToolbarMain.visibility = View.GONE
        } else {
            binding.imgToolbarMain.setImageBitmap(image)
            binding.imgToolbarMain.visibility = View.VISIBLE
            binding.imgToolbarMain.setOnClickListener {

            }
        }
    }

    fun setToolbarText(text: String) {
        binding.txtToolbarMain.text = text
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerMain.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // Close drawer if opened
        if (binding.drawerMain.isDrawerOpen(GravityCompat.START)) {
            binding.drawerMain.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}