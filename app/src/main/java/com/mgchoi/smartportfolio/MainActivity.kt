package com.mgchoi.smartportfolio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.contains
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.preference.PreferenceManager
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
import com.mgchoi.smartportfolio.value.IntentFilterActions
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
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var headerBinding: HeaderNavMainBinding
    private lateinit var adapter: MainAdapter

    // Member가 추가되었을 때 호출되는 브로드캐스트 리시버
    private var memberAddReceiver: BroadcastReceiver? = null

    private var data: ArrayList<Member> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        headerBinding = HeaderNavMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)

        // DB에서 Member 데이터 가져오기
        initData()
        // 네비게이션 헤더 뷰를 네비게이션 드로어에 추가
        initHeaderView()
        // 네비게이션 드로어 초기화
        initNavigationDrawer()
        // 기타 뷰 초기화
        initView()
        // 네비게이션 메뉴 초기화
        // 네비게이션 메뉴에 Member가 리스트되어 있으므로 데이터 초기화와 함께 진행되어야 한다
        initNavigationMenu()
        // Portfolio fragment 초기화
        initFragments()

        // 데이터가 수정되었을 때 다시 로드할 수 있는 BroadcastReceiver 등록
        initReceiver()
    }

    private fun initData() {
        // Data initialization
        val dao = MemberDAO(this)
        data.clear()
        data.addAll(dao.selectAll())
    }

    private fun initHeaderView() {
        // Attach header view
        if (!binding.navMain.contains(headerBinding.root)) {
            binding.navMain.addHeaderView(headerBinding.root)
        }

        // Set listeners
        headerBinding.imgBtnNavClose.setOnClickListener {
            binding.drawerMain.closeDrawer(GravityCompat.START)
        }

        // 로그아웃 버튼
        headerBinding.imgBtnNavLogout.setOnClickListener {
            // 로그인 정보 삭제
            MyApplication.login = false

            // 자동로그인 끄기
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            pref.edit()
                .putBoolean("auto_login", false)
                .putBoolean("biometric_login", false)
                .apply()

            // 로그인 Activity로 이동
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            this@MainActivity.finish()
        }

    }

    private fun initNavigationDrawer() {
        // Display hamburger menu
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerMain,
            R.string.drawer_open,
            R.string.drawer_close
        )
        toggle.syncState()
        supportActionBar?.title = ""

        // 뒤로 버튼을 눌렀을 때 네비게이션 드로어가 열려 있으면 네비게이션 드로어를 먼저 닫음
        val callback = onBackPressedDispatcher.addCallback {
            binding.drawerMain.closeDrawer(GravityCompat.START)
        }
        callback.isEnabled = false

        // 드로어가 열렸는지 여부에 따라 onBackPressed callback을 활성화하거나 비활성화
        binding.drawerMain.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit
            override fun onDrawerStateChanged(newState: Int) = Unit
            override fun onDrawerOpened(drawerView: View) {
                callback.isEnabled = true
            }

            override fun onDrawerClosed(drawerView: View) {
                callback.isEnabled = false
            }

        })
    }

    private fun initView() {
        // Viewpager 이전 버튼
        binding.imgBtnMainPrev.setOnClickListener {
            if (binding.pagerMain.currentItem > 0) {
                binding.pagerMain.currentItem--
            }
        }

        // Viewpager 다음 버튼
        binding.imgBtnMainNext.setOnClickListener {
            if (binding.pagerMain.currentItem < adapter.itemCount) {
                binding.pagerMain.currentItem++
            }
        }

        // NavigationItemSelectedListener, 함수로 구현하여 분리
        binding.navMain.setNavigationItemSelectedListener { item ->
            binding.drawerMain.closeDrawer(GravityCompat.START)
            onNavigationItemSelected(item)
        }

    }

    private fun initNavigationMenu() {
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

        // Member 추가 혹은 제거 메뉴
        // 로그인일 때만 나타남
        if (MyApplication.login) {
            val portfolioManage = binding.navMain.menu.addSubMenu(R.string.main_nav_submenu_manage)
            portfolioManage.add(0, ACTION_ADD, 0, R.string.main_nav_menu_add)
                .setIcon(R.drawable.ic_baseline_add_black_24)
            portfolioManage.add(0, ACTION_REMOVE, 0, R.string.main_nav_menu_remove)
                .setIcon(R.drawable.ic_baseline_delete_24)
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
                initNavigationMenu()
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
                            initNavigationMenu()
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

    }

    fun setPage(page: Int) {
        binding.pagerMain.currentItem = page
    }

    fun setToolbarImage(member: Member? = null, image: Bitmap? = null) {
        if (member == null || image == null) {
            binding.cardToolbarMain.visibility = View.GONE
            binding.layoutMainToolbar.setOnClickListener { }
        } else {
            binding.cardToolbarMain.visibility = View.VISIBLE
            binding.imgToolbarMain.setImageBitmap(image)
            binding.imgToolbarMain.setOnClickListener {
                if (MyApplication.login) {
                    val intent = Intent(this, PortfolioManageActivity::class.java)
                    intent.putExtra(PortfolioManageActivity.EXTRA_MEMBER, member.id)
                    startActivity(intent)
                }
            }
        }
    }

    fun setToolbarText(text: String, url: String? = null) {
        binding.txtToolbarMain.text = text
        if (url == null) {
            binding.layoutToolbarMainSub.visibility = View.GONE
            binding.layoutMainToolbar.setOnClickListener { }
        } else {
            binding.txtToolbarMainSub.text = url
            binding.layoutToolbarMainSub.visibility = View.VISIBLE
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerMain.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initReceiver() {
        // 멤버가 추가되거나 삭제되었을 때 데이터 다시 로드
        memberAddReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // 멤버가 추가되거나 삭제되었을 때만 동작
                if (intent?.action == IntentFilterActions.ACTION_MEMBER_ADDED ||
                    intent?.action == IntentFilterActions.ACTION_MEMBER_REMOVED
                ) {
                    initData()
                    initFragments()
                    initNavigationMenu()
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(IntentFilterActions.ACTION_MEMBER_ADDED)
            addAction(IntentFilterActions.ACTION_MEMBER_REMOVED)
        }
        registerReceiver(memberAddReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity가 종료되면 Broadcast receiver 제거
        memberAddReceiver?.let {
            unregisterReceiver(it)
            memberAddReceiver = null
        }
    }
}