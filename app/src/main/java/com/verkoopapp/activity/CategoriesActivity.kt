package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.adapter.CategoryAdapter
import com.verkoopapp.fragment.FirstCategoryFragment
import com.verkoopapp.models.CategoriesResponse
import com.verkoopapp.models.DataCategory
import com.verkoopapp.models.LikedResponse
import com.verkoopapp.models.UpdateCategoryRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.categories_screen.*
import kotlinx.android.synthetic.main.categories_screen.llParentCate
import kotlinx.android.synthetic.main.categories_screen.rvCategoryList
import kotlinx.android.synthetic.main.first_category.*
import retrofit2.Response

class CategoriesActivity : AppCompatActivity(), CategoryAdapter.SelectedCategory {
    private var selectionCount: Int = 0
    private var doubleBackToExitPressedOnce = false
    private var id = 0
    private var type = 0
    private val categoryList = ArrayList<DataCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories_screen)
        type = intent.getIntExtra(AppConstants.TYPE, 0)
        id = intent.getIntExtra(AppConstants.ID, 0)
        if (Utils.isOnline(this)) {
            callCategoriesApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    override fun selectedCount(addItem: Int) {
        tvSelectionCount.text = StringBuilder().append(" ").append(addItem.toString()).append("\n").append("Categories")
        selectionCount = addItem

    }

    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(this, 3)
        rvCategoryList.layoutManager = linearLayoutManager
        val categoriesAdapter = CategoryAdapter(this, categoryList, llParentCate)
        rvCategoryList.adapter = categoriesAdapter
//        val mAdapter = PicturePreViewAdapter(supportFragmentManager)
//        vpCategories.adapter = mAdapter
//        vpCategories.offscreenPageLimit = 5
//        vpCategories.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                highlightIndicator(position)
//            }
//
//            override fun onPageSelected(position: Int) {
//
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//
//            }
//        })
        tvNext.setOnClickListener {
            val selectedList = ArrayList<String>()
            if (selectionCount >= 3) {
                for (i in categoryList.indices) {
                    if (categoryList[i].isSelected) {
                        selectedList.add(categoryList[i].id.toString())
                    }
                }
                if (Utils.isOnline(this)) {
                    updateCategoryApi(selectedList)
                } else {
                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.select_three)).show()
            }

        }
        tvSkip.setOnClickListener {
            Utils.savePreferencesString(this, AppConstants.COMING_FROM, "category_screen")
            val intent = Intent(this@CategoriesActivity, PickOptionActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    internal inner class PicturePreViewAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    FirstCategoryFragment.newInstance(position, categoryList)
                }
                1 -> {
                    FirstCategoryFragment.newInstance(position, categoryList)
                }
                2 -> {
                    FirstCategoryFragment.newInstance(position, categoryList)
                }
                3 -> {
                    FirstCategoryFragment.newInstance(position, categoryList)
                }
                else -> {
                    FirstCategoryFragment.newInstance(position, categoryList)
                }
            }

        }

        override fun getCount(): Int {
            return 5
        }
    }

    private fun highlightIndicator(count: Int) {
        when (count) {
            0 -> {
                setGrayBackground()
                ivIndicatorFirst.setImageResource(R.mipmap.dot_1)
            }
            1 -> {
                setGrayBackground()
                ivIndicatorSecond.setImageResource(R.mipmap.dot_1)
            }
            2 -> {
                setGrayBackground()
                ivIndicatorThird.setImageResource(R.mipmap.dot_1)
            }
            3 -> {
                setGrayBackground()
                ivIndicatorForth.setImageResource(R.mipmap.dot_1)
            }
            4 -> {
                setGrayBackground()
                ivIndicatorFifth.setImageResource(R.mipmap.dot_1)
            }
        }
    }

    private fun setGrayBackground() {
        ivIndicatorFirst.setImageResource(R.mipmap.dot_2)
        ivIndicatorSecond.setImageResource(R.mipmap.dot_2)
        ivIndicatorThird.setImageResource(R.mipmap.dot_2)
        ivIndicatorForth.setImageResource(R.mipmap.dot_2)
        ivIndicatorFifth.setImageResource(R.mipmap.dot_2)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    /*get categories Api*/
    private fun callCategoriesApi() {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().categoriesService(
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@CategoriesActivity)
                        val categoriesResponse = response.body() as CategoriesResponse
                        if (categoriesResponse.data != null) {
                            categoryList.addAll(categoriesResponse.data)
                            setAdapter()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@CategoriesActivity)
                        Utils.showSimpleMessage(this@CategoriesActivity, msg!!).show()
                    }
                })
    }


    private fun updateCategoryApi(selectedList: ArrayList<String>) {
        VerkoopApplication.instance.loader.show(this)
        val request = UpdateCategoryRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), selectedList.toString().replace("[", "").replace("]", ""))
        ServiceHelper().updateCategoryService(request,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@CategoriesActivity)
                        val response = response.body() as LikedResponse
                        Utils.showToast(this@CategoriesActivity, response.message)
                        Utils.savePreferencesString(this@CategoriesActivity, AppConstants.COMING_FROM, "category_screen")
                        val intent = Intent(this@CategoriesActivity, PickOptionActivity::class.java)
                        intent.putExtra(AppConstants.ID, id)
                        intent.putExtra(AppConstants.TYPE, type)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@CategoriesActivity)
                        Utils.showSimpleMessage(this@CategoriesActivity, msg!!).show()
                    }
                })
    }


}