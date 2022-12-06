package com.mgchoi.smartportfolio.frament

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mgchoi.smartportfolio.MainActivity
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.adapter.IndexAdapter
import com.mgchoi.smartportfolio.databinding.FragmentIndexBinding
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.value.IntentFilterActions

class IndexFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = IndexFragment()
    }

    private lateinit var binding: FragmentIndexBinding
    private lateinit var adapter: IndexAdapter

    private var data: ArrayList<Member> = arrayListOf()
    private var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentIndexBinding.inflate(layoutInflater)
        initBroadcastReceiver()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initData()
        initRecyclerView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.setToolbarText(getString(R.string.app_name))
        activity.setToolbarImage()
    }

    private fun initData() {
        data.clear()
        val dao = MemberDAO(requireContext())
        val members = dao.selectAll()
        data.addAll(members)
    }

    private fun initRecyclerView() {
        adapter = IndexAdapter()
        adapter.data = data

        binding.rvIndex.adapter = adapter
        binding.rvIndex.layoutManager = LinearLayoutManager(requireContext())

        adapter.onItemClick = {
            (requireActivity() as MainActivity).setPage(1 + it)
        }
    }

    private fun refreshRecyclerView() {
        adapter.data = data
    }

    private fun initBroadcastReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                initData()
                refreshRecyclerView()
            }
        }

        val filter = IntentFilter().apply {
            addAction(IntentFilterActions.ACTION_MEMBER_ADDED)
            addAction(IntentFilterActions.ACTION_MEMBER_REMOVED)
        }
        requireContext().registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let {
            requireContext().unregisterReceiver(it)
            receiver = null
        }
    }
}