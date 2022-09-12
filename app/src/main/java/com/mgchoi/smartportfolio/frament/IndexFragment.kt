package com.mgchoi.smartportfolio.frament

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

class IndexFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = IndexFragment()
    }

    private lateinit var binding: FragmentIndexBinding

    private var data: ArrayList<Member> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentIndexBinding.inflate(layoutInflater)
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
}