package com.mgchoi.smartportfolio.frament

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.util.Pair
import com.mgchoi.smartportfolio.MainActivity
import com.mgchoi.smartportfolio.PortfolioDetailActivity
import com.mgchoi.smartportfolio.adapter.DetailViewRequestListener
import com.mgchoi.smartportfolio.adapter.PortfolioAdapter
import com.mgchoi.smartportfolio.databinding.FragmentPortfolioBinding
import com.mgchoi.smartportfolio.databinding.RowCardBinding
import com.mgchoi.smartportfolio.databinding.RowMessageBinding
import com.mgchoi.smartportfolio.databinding.RowTimelineBinding
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.tool.ProfileImageHelper
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

        adapter.detailViewRequestListener = object : DetailViewRequestListener {
            override fun cardView(binding: RowCardBinding, portfolioId: Int) {
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    Pair(binding.txtRowCardTitle, "detail_title"),
                    Pair(binding.txtRowCardContent, "detail_content"),
                    Pair(binding.imgRowCardImage, "detail_image")
                )
                startDetailActivityWithTransition(option, portfolioId)
            }

            override fun timelineView(binding: RowTimelineBinding, portfolioId: Int) {
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    Pair(binding.txtRowTimelineTitle, "detail_title"),
                    Pair(binding.txtRowTimelineContent, "detail_content"),
                    Pair(binding.imgRowTimelineImage, "detail_image")
                )
                startDetailActivityWithTransition(option, portfolioId)
            }

            override fun messageView(binding: RowMessageBinding, portfolioId: Int) {
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    Pair(binding.txtRowMessageTitle, "detail_title"),
                    Pair(binding.txtRowMessageContent, "detail_content"),
                    Pair(binding.imgRowMessageImage, "detail_image")
                )
                startDetailActivityWithTransition(option, portfolioId)
            }

        }
    }

    private fun startDetailActivityWithTransition(
        options: ActivityOptionsCompat,
        portfolioId: Int
    ) {
        val intent = Intent(requireContext(), PortfolioDetailActivity::class.java)
        intent.putExtra(PortfolioDetailActivity.EXTRA_PORTFOLIO_ID, portfolioId)
        startActivity(intent, options.toBundle())
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
        val manager = ProfileImageHelper(requireContext())
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