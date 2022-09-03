package com.mgchoi.smartportfolio.frament

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mgchoi.smartportfolio.databinding.FragmentPortfolioBinding
import com.mgchoi.smartportfolio.model.Member

class PortfolioFragment(val member: Member) : Fragment() {

    private lateinit var binding: FragmentPortfolioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPortfolioBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().title = member.name
    }
}