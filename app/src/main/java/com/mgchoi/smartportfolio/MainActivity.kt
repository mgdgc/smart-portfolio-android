package com.mgchoi.smartportfolio

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.snackbar.Snackbar
import com.mgchoi.smartportfolio.adapter.MainAdapter
import com.mgchoi.smartportfolio.databinding.ActivityMainBinding
import com.mgchoi.smartportfolio.databinding.HeaderNavMainBinding
import com.mgchoi.smartportfolio.databinding.LayoutMemberAddBinding
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.frament.IndexFragment
import com.mgchoi.smartportfolio.frament.PortfolioFragment
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.ViewStyle
import com.mgchoi.smartportfolio.value.SharedPreferenceKeys

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ACTION_INDEX = 2000
        private const val ACTION_PORTFOLIO = 1000
        private const val ACTION_LICENSE = 2001
        private const val ACTION_INFO = 2002
        private const val ACTION_SETTINGS = 2003
        private const val ACTION_ADD = 2004
        private const val ACTION_REMOVE = 2005
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
            ACTION_ADD -> {
                handleAdd()
            }
            ACTION_REMOVE -> {
                handleRemove()
            }
        }

        true
    }

    private fun handleAdd() {
        val addBinding = LayoutMemberAddBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.main_member_add_title)
            .setView(addBinding.root)
            .setPositiveButton(R.string.confirm) { d, _ ->
                val dao = MemberDAO(this)
                val member = Member(
                    0,
                    addBinding.editTextProfileName.text.toString().trim(),
                    null,
                    addBinding.editTextProfileUrl.text.toString().trim(),
                    ViewStyle.TIMELINE,
                    true
                )
                dao.insert(member)
                initData()
                initFragments()
                d.dismiss()
            }
            .setNegativeButton(R.string.cancel) { d, _ ->
                d.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun handleRemove() {
        val array = Array(data.size) { data[it].name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.main_nav_menu_remove)
            .setAdapter(adapter) { d, p ->
                val target = data[p]
                d.dismiss()
                if (target.destroyable) {
                    val confirm = AlertDialog.Builder(this)
                    confirm.setTitle(target.name)
                        .setMessage(R.string.main_member_remove_confirm)
                        .setPositiveButton(R.string.confirm) { dialog, _ ->
                            dialog.dismiss()
                            val dao = MemberDAO(this)
                            dao.delete(target.id)
                            initData()
                            initFragments()
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .show()
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.main_member_remove_not_destroyable,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.close) { }
                        .show()
                }
            }
            .setNegativeButton(R.string.cancel) { d, _ ->
                d.dismiss()
            }
            .show()
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
        // Data initialization
        val dao = MemberDAO(this)
        data.clear()
        data.addAll(dao.selectAll())

        // Add menu to navigation drawer
        binding.navMain.menu.clear()
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


        val portfolioManage = binding.navMain.menu.addSubMenu(R.string.main_nav_submenu_manage)
        portfolioManage.add(0, ACTION_ADD, 0, R.string.main_nav_menu_add)
            .setIcon(R.drawable.ic_baseline_add_black_24)
        portfolioManage.add(0, ACTION_REMOVE, 0, R.string.main_nav_menu_remove)
            .setIcon(R.drawable.ic_baseline_delete_24)

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

    fun setPage(page: Int) {
        binding.pagerMain.currentItem = page % adapter.itemCount
    }

    fun setToolbarImage(member: Member? = null, image: Bitmap? = null) {
        if (member == null || image == null) {
            binding.cardToolbarMain.visibility = View.GONE
            binding.layoutMainToolbar.setOnClickListener { }
        } else {
            binding.cardToolbarMain.visibility = View.VISIBLE
            binding.imgToolbarMain.setImageBitmap(image)
            binding.imgToolbarMain.setOnClickListener {
                val intent = Intent(this, PortfolioManageActivity::class.java)
                intent.putExtra(PortfolioManageActivity.EXTRA_MEMBER, member)
                startActivity(intent)
            }
        }
    }

    fun setToolbarText(text: String, url: String? = null) {
        binding.txtToolbarMain.text = text
        if (url == null) {
            binding.txtToolbarMainSub.visibility = View.GONE
            binding.layoutMainToolbar.setOnClickListener { }
        } else {
            binding.txtToolbarMainSub.text = url
            binding.txtToolbarMainSub.visibility = View.VISIBLE
            binding.layoutMainToolbar.setOnClickListener {
                openLink(url)
            }
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
                        WebViewActivity.openAsCustomTab(this, url)
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
            2 -> WebViewActivity.openAsCustomTab(this, url)
        }
    }

    private fun openLinkAsWebViewActivity(url: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_URL, url)
        startActivity(intent)
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