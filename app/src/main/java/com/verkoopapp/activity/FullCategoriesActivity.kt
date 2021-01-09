package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.FullCategoryAdapter
import com.verkoopapp.adapter.SubCategoryAdapter
import com.verkoopapp.fragment.HomeFragment
import com.verkoopapp.models.CategoriesResponse
import com.verkoopapp.models.DataCategory
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.full_categories_activity.*
import kotlinx.android.synthetic.main.toolbar_category.*
import kotlinx.android.synthetic.main.toolbar_category.ivChat
import kotlinx.android.synthetic.main.toolbar_category.ivFavourite
import kotlinx.android.synthetic.main.toolbar_category.iv_left
import kotlinx.android.synthetic.main.toolbar_category_new.*
import retrofit2.Response
import java.util.*

class FullCategoriesActivity : AppCompatActivity(), FullCategoryAdapter.SelectedCategory, SubCategoryAdapter.SelectedSubcategory {
    val TAG = FullCategoriesActivity::class.java.simpleName.toString()

    override fun subCategoryName(subCategoryId: Int, subCategoryName: String, categoryId: Int) {
        when (categoryId) {
            85 -> {
                val intent = Intent(this, BuyCarsActivity::class.java)
                startActivityForResult(intent, 2)
            }
            24 -> {
                val intent = Intent(this, BuyPropertiesActivity::class.java)
                startActivityForResult(intent, 2)
            }
            else -> {
                val intent = Intent(this, CategoryDetailsActivity::class.java)
                intent.putExtra(AppConstants.CATEGORY_ID, subCategoryId)
                intent.putExtra(AppConstants.SUB_CATEGORY, subCategoryName)
                intent.putExtra(AppConstants.TYPE, 1)
                intent.putExtra(AppConstants.Search, "")
                startActivityForResult(intent, 2)
            }
        }
    }

    override fun categoryName(categoryId: Int, categoryName: String) {
        when (categoryId) {
            85 -> {
                val intent = Intent(this, BuyCarsActivity::class.java)
                startActivityForResult(intent, 2)
            }
            24 -> {
                val intent = Intent(this, BuyPropertiesActivity::class.java)
                startActivityForResult(intent, 2)
            }
            else -> {
                val intent = Intent(this, CategoryDetailsActivity::class.java)
                intent.putExtra(AppConstants.CATEGORY_ID, categoryId)
                intent.putExtra(AppConstants.SUB_CATEGORY, categoryName)
                intent.putExtra(AppConstants.TYPE, 0)
                intent.putExtra(AppConstants.Search, "")
                startActivityForResult(intent, 2)
            }
        }

    }

    private val categoryList = ArrayList<DataCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_categories_activity)
        if (Utils.isOnline(this)) {
            callCategoriesApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setData() {
        ivChat.setOnClickListener {
            val intent = Intent(this, ChatInboxActivity::class.java)
            startActivity(intent)
        }
        val mManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        rvCategoryList.layoutManager = mManager
        val fullCategoryAdapter = FullCategoryAdapter(this, categoryList)
        rvCategoryList.adapter = fullCategoryAdapter
        iv_left.setOnClickListener { onBackPressed() }
        ivFavourite.setOnClickListener {
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
        }
        etSearchFull.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivityForResult(intent, 2)
        }

    }

    private fun callCategoriesApi() {
        pvProgressCat.visibility = View.VISIBLE
        ServiceHelper().categoriesService(
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pvProgressCat.visibility = View.GONE
                        val categoriesResponse = response.body() as CategoriesResponse
                        if (categoriesResponse.data != null) {
                            categoryList.addAll(categoriesResponse.data)
                            setData()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        pvProgressCat.visibility = View.GONE
                        Utils.showSimpleMessage(this@FullCategoriesActivity, msg!!).show()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION, 0)
                if (result == 1) {
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.TRANSACTION, result)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }
}