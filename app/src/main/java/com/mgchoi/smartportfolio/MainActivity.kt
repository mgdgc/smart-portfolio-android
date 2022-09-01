package com.mgchoi.smartportfolio

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.mgchoi.smartportfolio.databinding.ActivityMainBinding
import com.mgchoi.smartportfolio.databinding.HeaderNavMainBinding
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.frament.CardFragment
import com.mgchoi.smartportfolio.frament.IndexFragment
import com.mgchoi.smartportfolio.frament.MessageFragment
import com.mgchoi.smartportfolio.frament.TimelineFragment
import com.mgchoi.smartportfolio.model.Member

class MainActivity : AppCompatActivity() {

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
    }

    private fun initView() {
        // Attach header view
        binding.navMain.addHeaderView(headerBinding.root)

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

    }

    private fun initHeaderView() {
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
                Member(1, "1", 1, "1", ViewStyle.TIMELINE, true),
                Member(2, "2", 2, "2", ViewStyle.MESSAGE, true),
                Member(3, "3", 3, "3", ViewStyle.TIMELINE, true),
                Member(4, "4", 4, "4", ViewStyle.CARD, true),
                Member(5, "5", 5, "5", ViewStyle.MESSAGE, true)
            )
        )
    }

    private fun initFragments() {
        // Initialize adapter
        adapter = MainAdapter(this)
        binding.pagerMain.adapter = adapter

        // Add fragments to adapter
        adapter.addFragments(IndexFragment.newInstance())
        for (member in data) {
            adapter.addFragments(
                when (member.viewStyle) {
                    ViewStyle.TIMELINE -> TimelineFragment(member)
                    ViewStyle.MESSAGE -> MessageFragment(member)
                    ViewStyle.CARD -> CardFragment(member)
                }
            )
        }

        // Attach indicator
        binding.indicatorMain.attachTo(binding.pagerMain)

        setPage(0)
    }

    private fun setPage(page: Int) {
        binding.pagerMain.currentItem = page % adapter.itemCount
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerMain.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}