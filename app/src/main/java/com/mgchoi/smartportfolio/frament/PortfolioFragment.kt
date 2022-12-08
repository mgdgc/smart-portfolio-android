package com.mgchoi.smartportfolio.frament

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mgchoi.smartportfolio.MainActivity
import com.mgchoi.smartportfolio.adapter.PortfolioAdapter
import com.mgchoi.smartportfolio.databinding.FragmentPortfolioBinding
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.tool.ProfileImageManager
import com.mgchoi.smartportfolio.value.IntentFilterActions

class PortfolioFragment(private var member: Member) : Fragment() {

    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var adapter: PortfolioAdapter

    private var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPortfolioBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initRecyclerView()
        initData()
        initBroadcastReceiver()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setMainActivityToolbar()
    }

    private fun initData() {
        val dao = PortfolioDAO(requireContext())
        adapter.data = dao.selectAll(member.id)
    }

    private fun initRecyclerView() {
        adapter = PortfolioAdapter(requireContext(), member)

        binding.rvPortfolio.adapter = adapter
        binding.rvPortfolio.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initBroadcastReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    IntentFilterActions.ACTION_PORTFOLIO_ADDED,
                    IntentFilterActions.ACTION_PORTFOLIO_UPDATE,
                    IntentFilterActions.ACTION_PORTFOLIO_REMOVED -> {
                        initData()
                    }
                    IntentFilterActions.ACTION_MEMBER_UPDATE -> {
                        val dao = MemberDAO(requireContext())
                        dao.select(member.id)?.let {
                            member = it
                            adapter.setMember(it)
                            setMainActivityToolbar()
                            initData()
                        }
                    }
                }
            }
        }

        receiver?.let {
            val filter = IntentFilter().apply {
                addAction(IntentFilterActions.ACTION_PORTFOLIO_ADDED)
                addAction(IntentFilterActions.ACTION_PORTFOLIO_UPDATE)
                addAction(IntentFilterActions.ACTION_PORTFOLIO_REMOVED)
                addAction(IntentFilterActions.ACTION_MEMBER_UPDATE)
            }
            requireContext().registerReceiver(receiver, filter)
        }
    }

    private fun setMainActivityToolbar() {
        // Set Image & Text for MainActivity
        val activity = requireActivity() as MainActivity
        activity.setToolbarText(member.name, member.url)
        val manager = ProfileImageManager(requireContext())
        val profileImage = member.image?.let { manager.read(it) }
            ?: manager.defaultProfileImage(member.name)

        activity.setToolbarImage(member, profileImage)
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let {
            requireContext().unregisterReceiver(it)
            receiver = null
        }
    }
}