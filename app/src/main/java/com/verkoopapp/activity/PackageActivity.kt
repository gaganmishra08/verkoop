package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.verkoopapp.R
import com.verkoopapp.adapter.HomePagerAdapter
import com.verkoopapp.fragment.*
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.toolbar_home.*

class PackageActivity : AppCompatActivity() {
    private var homeFragment: PackageHomeFragment? = null
    private var profileFragment: PackageProfileFragment? = null
    private var tripFragment: PackageTripFragment? = null
    private var vervoFragment: PackageVervoFragment? = null
    private var fragmentList = ArrayList<Fragment>()
    private var comingFrom: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package)
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        homeFragment = PackageHomeFragment.newInstance()
        profileFragment = PackageProfileFragment.newInstance()
        tripFragment = PackageTripFragment.newInstance()
        vervoFragment = PackageVervoFragment.newInstance()
        fragmentList.add(homeFragment!!)
        fragmentList.add(tripFragment!!)
        fragmentList.add(vervoFragment!!)
        fragmentList.add(profileFragment!!)
        setData()

        setIntentData()

        Log.e("context", "Context")

    }


    private fun setIntentData() {
        val result = intent!!.getIntExtra(AppConstants.TRANSACTION, 0)
        if (result == 1) {
            when {
                viewPager.currentItem == 0 -> {
                    viewPager.currentItem = 3
                //    profileFragment!!.refreshUI(0)
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_button3)
                    }, 100)
                }
                viewPager.currentItem == 1 -> {
                    viewPager.currentItem = 3
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_button3)
                    }, 100)
                }
                viewPager.currentItem == 2 -> {
                    viewPager.currentItem = 3
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_button3)
                    }, 100)
                }
                else -> {
                  //  profileFragment!!.refreshUI(1)
                }
            }

        }
    }



    private fun setTabLayout() {
        bottomTabLayout.setButtonTextStyle(R.style.TextGray13)
        // set buttons from menu resource
        bottomTabLayout.setItems(R.menu.menu_package)
        //set on selected tab listener.
        bottomTabLayout.setListener { id ->
            switchFragment(id)
        }
        /* bottomTabLayout.setListener { position ->
             viewPager.currentItem = position
         }*/
        bottomTabLayout.setSelectedTab(R.id.menu_button1)
        //enable indicator
        bottomTabLayout.setIndicatorVisible(true)
        //indicator height
        bottomTabLayout.setIndicatorHeight(resources.getDimension(R.dimen.dp_2))
        //indicator color
        bottomTabLayout.setIndicatorColor(R.color.white)
        //indicator line color
        bottomTabLayout.setIndicatorLineColor(R.color.colorPrimary)
        //bottomTabLayout.setSelectedTab(R.id.menu_button5)
    }

    private fun switchFragment(id: Int) {
        when (id) {
            R.id.menu_button1 -> viewPager.currentItem = 0
            R.id.menu_button2 -> viewPager.currentItem = 1
            R.id.vervo_button -> viewPager.currentItem = 2
            R.id.menu_button3 -> viewPager.currentItem = 3
        }
    }

    private fun setData() {
        val adapter = HomePagerAdapter(supportFragmentManager, 3, fragmentList)
        viewPager.adapter = adapter
        // viewPager.offscreenPageLimit = 2
        setTabLayout()

        if (comingFrom == 1) {
            when {
                viewPager.currentItem == 0 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                  //  profileFragment!!.refreshUI(0)
                }
                viewPager.currentItem == 1 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                }
                viewPager.currentItem == 2 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                }
                else -> {
                 //   profileFragment!!.refreshUI(1)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION, 0)
                if (result == 1) {
                    when {
                        viewPager.currentItem == 0 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
                         //   profileFragment!!.refreshUI(0)

                        }
                        viewPager.currentItem == 1 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
                        }
                        viewPager.currentItem == 2 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
                        }
                        else -> {
                          //  profileFragment!!.refreshUI(1)
                        }
                    }

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}