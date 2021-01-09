package com.verkoopapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter



class HomePagerAdapter(fm: FragmentManager, private val pages: Int,private val fragmentList: ArrayList<Fragment>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> fragmentList[position]
            1 -> fragmentList[position]
            2 -> fragmentList[position]
            3 -> fragmentList[position]
            else -> null
        }!!
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

}