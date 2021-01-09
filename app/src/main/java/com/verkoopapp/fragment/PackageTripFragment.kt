package com.verkoopapp.fragment

import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.PackageActivity
import com.verkoopapp.adapter.TripsPagerAdapter
import kotlinx.android.synthetic.main.fragment_food_order.*


class PackageTripFragment : BaseFragment() {
    val TAG = PackageTripFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: PackageActivity


    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity = context as PackageActivity
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_package_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout.addTab(tabLayout.newTab().setText("Past Orders"))
        tabLayout.addTab(tabLayout.newTab().setText("Ongoing Orders"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL


        val cfManager: FragmentManager = childFragmentManager

        val adapter = TripsPagerAdapter(context!!, cfManager, tabLayout.tabCount)
        orders_viewpager.adapter = adapter

        orders_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                orders_viewpager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        

    }




    companion object {
        fun newInstance(): PackageTripFragment {
            val args = Bundle()
            val fragment = PackageTripFragment()
            fragment.arguments = args
            return fragment
        }
    }
}