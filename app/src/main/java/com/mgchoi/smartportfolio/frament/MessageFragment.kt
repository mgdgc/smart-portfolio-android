package com.mgchoi.smartportfolio.frament

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mgchoi.smartportfolio.databinding.FragmentMessageBinding
import com.mgchoi.smartportfolio.model.Member

class MessageFragment(val member: Member) : Fragment() {

    private lateinit var binding: FragmentMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMessageBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }
}