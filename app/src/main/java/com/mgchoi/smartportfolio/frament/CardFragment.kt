package com.mgchoi.smartportfolio.frament

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mgchoi.smartportfolio.databinding.FragmentCardBinding
import com.mgchoi.smartportfolio.model.Member

class CardFragment(val member: Member) : Fragment() {

    private lateinit var binding: FragmentCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCardBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }
}