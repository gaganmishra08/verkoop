package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.verkoopapp.R
import com.verkoopapp.adapter.FoodHomePagerAdapter
import com.verkoopapp.fragment.*
import com.verkoopapp.utils.AppConstants
import kotlinx.android.synthetic.main.home_activity.*


class FoodHomeActivity : AppCompatActivity() {

    private var foodHomeFragment: FoodHomeFragment? = null
    private var foodOrderFragment: FoodOrderFragment? = null
    private var foodCouponFragmnet: FoodCouponFragmnet? = null
    private var foodVervoFragment: FoodVervoFragment? = null
    private var foodProfileFragment: FoodProfileFragment? = null

    private var fragmentList = ArrayList<Fragment>()
    private var comingFrom: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_home)

        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        foodHomeFragment = FoodHomeFragment.newInstance()
        foodCouponFragmnet = FoodCouponFragmnet.newInstance()
        foodOrderFragment = FoodOrderFragment.newInstance()
        foodVervoFragment = FoodVervoFragment.newInstance()
        foodProfileFragment = FoodProfileFragment.newInstance()
        fragmentList.add(foodHomeFragment!!)
        fragmentList.add(foodCouponFragmnet!!)
        fragmentList.add(foodOrderFragment!!)
        fragmentList.add(foodVervoFragment!!)
        fragmentList.add(foodProfileFragment!!)

        setData()
        setIntentData()

    }


    private fun setIntentData() {
        val result = intent!!.getIntExtra(AppConstants.TRANSACTION, 0)
        if (result == 1) {
            when {
                viewPager.currentItem == 0 -> {
                    viewPager.currentItem = 3
                 //     foodProfileFragment!!.refreshUI(0)
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_profile)
                    }, 100)
                }
                viewPager.currentItem == 1 -> {
                    viewPager.currentItem = 3
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_profile)
                    }, 100)
                }
                viewPager.currentItem == 2 -> {
                    viewPager.currentItem = 3
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_profile)
                    }, 100)
                }
                viewPager.currentItem == 3 -> {
                    viewPager.currentItem = 4
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_profile)
                    }, 100)
                }
                else -> {
               //  foodProfileFragment!!.refreshUI(1)
                }
            }

        }
    }


    private fun setTabLayout() {
        bottomTabLayout.setButtonTextStyle(R.style.TextGray13)
        // set buttons from menu resource
        bottomTabLayout.setItems(R.menu.menu_bottom_food)
        //set on selected tab listener.
        bottomTabLayout.setListener { id ->
            switchFragment(id)
        }
        /* bottomTabLayout.setListener { position ->
             viewPager.currentItem = position
         }*/
        bottomTabLayout.setSelectedTab(R.id.menu_browse)
        //enable indicator
        bottomTabLayout.setIndicatorVisible(true)
        //indicator height
        bottomTabLayout.setIndicatorHeight(resources.getDimension(R.dimen.dp_2))
        //indicator color
        bottomTabLayout.setIndicatorColor(R.color.black)
        //indicator line color
        bottomTabLayout.setIndicatorLineColor(R.color.colorPrimary)

        //bottomTabLayout.setSelectedTab(R.id.menu_button5)
    }

    private fun switchFragment(id: Int) {
        when (id) {
            R.id.menu_browse -> viewPager.currentItem = 0
            R.id.menu_coupon -> viewPager.currentItem = 1
            R.id.menu_order -> viewPager.currentItem = 2
            R.id.menu_vervo -> viewPager.currentItem = 3
            R.id. menu_profile -> viewPager.currentItem = 4
        }
    }


    private fun setData() {
        val adapter = FoodHomePagerAdapter(supportFragmentManager, 4, fragmentList)
        viewPager.adapter = adapter

        setTabLayout()

        if (comingFrom == 1) {
            when (viewPager.currentItem) {
                0 -> {
                    bottomTabLayout.selectTab(R.id.menu_profile)
                    viewPager.currentItem = 3
                            //    foodProfileFragment!!.refreshUI(0)
                }
                1 -> {
                    bottomTabLayout.selectTab(R.id.menu_profile)
                    viewPager.currentItem = 3
                }
                2 -> {
                    bottomTabLayout.selectTab(R.id.menu_profile)
                    viewPager.currentItem = 3
                }
                3 -> {
                    bottomTabLayout.selectTab(R.id.menu_profile)
                    viewPager.currentItem = 3
                }
                else -> {
                //    foodProfileFragment!!.refreshUI(1)
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
                            bottomTabLayout.selectTab(R.id.menu_profile)
                            viewPager.currentItem = 3
                       //     foodProfileFragment!!.refreshUI(0)

                        }
                        viewPager.currentItem == 1 -> {
                            bottomTabLayout.selectTab(R.id.menu_profile)
                            viewPager.currentItem = 3
                        }
                        viewPager.currentItem == 2 -> {
                            bottomTabLayout.selectTab(R.id.menu_profile)
                            viewPager.currentItem = 3
                        }
                        viewPager.currentItem == 3 -> {
                            bottomTabLayout.selectTab(R.id.menu_profile)
                            viewPager.currentItem = 3
                        }
                        else -> {
                         //   foodProfileFragment!!.refreshUI(1)
                        }
                    }

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
//        if (requestCode == 1888 ) {
//            if (resultCode == Activity.RESULT_OK ) {
////                VerkoopApplication.instance.loader.show(this)
////                val currentImage = data.extras!!.get("data") as Bitmap
////                val baos = ByteArrayOutputStream()
////                currentImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
////                inputStream = ByteArrayInputStream(baos.toByteArray())
////                imageToVision()
//
//                VerkoopApplication.instance.loader.show(this)
//                val f = File(mCurrentPhotoPath!!)
//                uriTemp = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", f)
//                val currentImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uriTemp) as Bitmap;
//
////                val currentImage = data.extras!!.get("data") as Bitmap
//                val baos = ByteArrayOutputStream()
//                currentImage.compress(Bitmap.CompressFormat.JPEG, 50, baos)
//                inputStream = ByteArrayInputStream(baos.toByteArray())
//                imageToVision()
//            }
//        }
    }
}