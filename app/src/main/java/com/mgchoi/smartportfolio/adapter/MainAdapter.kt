package com.mgchoi.smartportfolio.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainAdapter(private val activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    fun removeAll() {
        val original = this.fragments.size
        this.fragments.clear()
        this.notifyItemRangeRemoved(0, original)
    }

    fun removeAt(position: Int) {
        this.fragments.removeAt(position)
        this.notifyItemRemoved(position)
    }

    fun addFragments(vararg fragments: Fragment) {
        val original = this.fragments.size
        this.fragments.addAll(fragments)
        this.notifyItemRangeInserted(original, fragments.size)
    }

    fun addFragment(fragment: Fragment, position: Int) {
        val original = this.fragments.size
        this.fragments.add(fragment)
        this.notifyItemInserted(position)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return this.fragments[position]
    }

}