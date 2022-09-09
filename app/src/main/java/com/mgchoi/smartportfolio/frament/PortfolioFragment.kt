package com.mgchoi.smartportfolio.frament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mgchoi.smartportfolio.MainActivity
import com.mgchoi.smartportfolio.adapter.PortfolioAdapter
import com.mgchoi.smartportfolio.databinding.FragmentPortfolioBinding
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.tool.ProfileImageManager

class PortfolioFragment(private val member: Member) : Fragment() {

    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var adapter: PortfolioAdapter

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
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Set Image & Text for MainActivity
        val activity = requireActivity() as MainActivity
        activity.setToolbarText(member.name)
        val manager = ProfileImageManager(requireContext())
        val profileImage = member.image?.let { manager.read(it) }
            ?: manager.defaultProfileImage(member.name)

        activity.setToolbarImage(member, profileImage)

        initData()
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
}