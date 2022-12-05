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
import android.widget.AdapterView
import android.widget.SimpleAdapter
import com.mgchoi.smartportfolio.MainActivity
import com.mgchoi.smartportfolio.R
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
        initListView()
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

    private fun initListView() {
        val arrayList = arrayListOf<MutableMap<String, String>>()
        this.data.forEach {
            val items = mutableMapOf<String, String>()
            items["item1"] = it.name
            items["item2"] = it.url ?: ""
            arrayList.add(items)
        }

        val adapter = SimpleAdapter(
            requireContext(),
            arrayList,
            android.R.layout.simple_list_item_2,
            arrayOf("item1", "item2"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        binding.lstIndex.adapter = adapter
        binding.lstIndex.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            (requireActivity() as MainActivity).setPage(1 + position)
        }
    }

    private fun initBroadcastReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                initData()
                initListView()
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