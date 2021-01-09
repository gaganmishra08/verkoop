package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.DetailSubCategoryAdapter
import com.verkoopapp.adapter.FilterAdapter
import com.verkoopapp.adapter.ItemAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.GridSpacingItemDecoration
import com.verkoopapp.utils.SpacingItem
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.category_details_activity.*
import kotlinx.android.synthetic.main.category_details_activity.tvSell
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.toolbar_details_.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Response

@Suppress("DEPRECATED_IDENTITY_EQUALS")

class CategoryDetailsActivity : AppCompatActivity(), FilterAdapter.SelectFilterCallBack {
    val TAG = CategoryDetailsActivity::class.java.simpleName.toString()
    private var isClicked: Boolean = false
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var filterAdapter: FilterAdapter
    private var itemsList = ArrayList<ItemHome>()
    private var filterRequest: FilterRequest? = null
    private var filterList = ArrayList<FilterModal>()
    private var fromLikeEvent: Boolean = false
    private var positionFromLikeEvent: Int = 0
    private var typeForEventBus: Int = 0
    private var comingFrom: String = ""

    override fun onSelectingFilter() {
        val intent = Intent(this, FilterActivity::class.java)
        intent.putExtra(AppConstants.POST_DATA, filterRequest)
        startActivityForResult(intent, 1)
    }

    override fun removeFilter(remove: String, position: Int) {
        when {
            remove.equals("Condition :", ignoreCase = true) -> {
                filterRequest = FilterRequest(filterRequest!!.category_id, filterRequest!!.type, filterRequest!!.userId, filterRequest!!.sort_no, filterRequest!!.latitude, filterRequest!!.longitude, "", filterRequest!!.meet_up, filterRequest!!.min_price, filterRequest!!.max_price, filterRequest!!.search, filterRequest!!.country_code)
                getDetailsApi(filterRequest!!)
                filterList.removeAt(position)
                filterAdapter.notifyDataSetChanged()

            }
            remove.equals("Deal Option :", ignoreCase = true) -> {
                filterRequest = FilterRequest(filterRequest!!.category_id, filterRequest!!.type, filterRequest!!.userId, filterRequest!!.sort_no, filterRequest!!.latitude, filterRequest!!.longitude, filterRequest!!.item_type, "", filterRequest!!.min_price, filterRequest!!.max_price, filterRequest!!.search, filterRequest!!.country_code)
                getDetailsApi(filterRequest!!)
                filterList.removeAt(position)
                filterAdapter.notifyDataSetChanged()
            }
            remove.equals("Price :", ignoreCase = true) -> {
                filterRequest = FilterRequest(filterRequest!!.category_id, filterRequest!!.type, filterRequest!!.userId, filterRequest!!.sort_no, filterRequest!!.latitude, filterRequest!!.longitude, filterRequest!!.item_type, filterRequest!!.meet_up, "", "", filterRequest!!.search, filterRequest!!.country_code)
                getDetailsApi(filterRequest!!)
                filterList.removeAt(position)
                filterAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_details_activity)
        setData()
        setItemList()
    }

    private fun setSubcategoryData(subCategoryList: ArrayList<SubCategoryPost>) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        rvSubCategory.layoutParams.height = (width / 3)
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSubCategory.layoutManager = mManager
        val detailSubCategoryAdapter = DetailSubCategoryAdapter(this, rvSubCategory, subCategoryList)
        rvSubCategory.adapter = detailSubCategoryAdapter
    }

    private fun setData() {
        ivChatDetail.setOnClickListener {
            val intent = Intent(this, ChatInboxActivity::class.java)
            startActivity(intent)
        }
        tvSell.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivityForResult(intent, 2)

        }
//        ivFavouriteDetail.setOnClickListener {
//            val intent = Intent(this, FavouritesActivity::class.java)
//            startActivity(intent)
//        }

        if (intent.getStringExtra(AppConstants.SUB_CATEGORY)?.isEmpty()!!){
            tvCategory.text = intent.getStringExtra(AppConstants.Search)
        }
        else {
            tvCategory.text = intent.getStringExtra(AppConstants.SUB_CATEGORY)
        }
        val type = intent.getIntExtra(AppConstants.TYPE, 0)
        typeForEventBus = type
        if (intent.getStringExtra(AppConstants.SUB_CATEGORY)?.isEmpty()!!){
            toolbar_title_.text = intent.getStringExtra(AppConstants.Search)
        }
        else {
            toolbar_title_.text = intent.getStringExtra(AppConstants.SUB_CATEGORY)
        }
        toolbar_title_.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(AppConstants.CATEGORY_NAME, toolbar_title_.text.toString())
            startActivityForResult(intent, 2)
        }
        if (type == 1) {
            llParent.visibility = View.GONE
            if (intent.getIntExtra(AppConstants.CATEGORY_ID, 0)==0){
                filterRequest = FilterRequest("", type, Utils.getPreferencesString(this, AppConstants.USER_ID), "2", "", "", "", "", "", "", intent.getStringExtra(AppConstants.Search)
                        ?: "", Utils.getPreferencesString(this@CategoryDetailsActivity, AppConstants.COUNTRY_CODE))

                getDetailsApi_android(filterRequest!!)

            } else {
                filterRequest = FilterRequest(intent.getIntExtra(AppConstants.CATEGORY_ID, 0).toString(), type, Utils.getPreferencesString(this, AppConstants.USER_ID), "2", "", "", "", "", "", "", intent.getStringExtra(AppConstants.Search)
                        ?: "", Utils.getPreferencesString(this@CategoryDetailsActivity, AppConstants.COUNTRY_CODE), intent.getIntExtra(AppConstants.ITEM_ID, 0))

                getDetailsApi(filterRequest!!)
            }

        } else {
            tvCategorySelected.text = intent.getStringExtra(AppConstants.SUB_CATEGORY)
            llParent.visibility = View.VISIBLE
            filterRequest = FilterRequest(intent.getIntExtra(AppConstants.CATEGORY_ID, 0).toString(), type, Utils.getPreferencesString(this, AppConstants.USER_ID), "2", "", "", "", "", "", "", intent.getStringExtra(AppConstants.Search)
                    ?: "",Utils.getPreferencesString(this@CategoryDetailsActivity,AppConstants.COUNTRY_CODE))

            getDetailsApi(filterRequest!!)
        }
        iv_left_detail.setOnClickListener { onBackPressed() }
        ivFilter.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java)
            intent.putExtra(AppConstants.POST_DATA, filterRequest)
            startActivityForResult(intent, 1)
        }
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilterSelected.layoutManager = mManager
        filterAdapter = FilterAdapter(this)
        rvFilterSelected.adapter = filterAdapter
        val filterModal = FilterModal(getString(R.string.shopr), getString(R.string.popular), false, 1)
        filterList.add(filterModal)
        filterAdapter.showData(filterList)
        filterAdapter.notifyDataSetChanged()
    }

    private fun setItemList() {
        val linearLayoutManager = GridLayoutManager(this, 2)
        rvItemListDetails.layoutManager = linearLayoutManager
        rvItemListDetails.addItemDecoration(SpacingItem(2,20, false))
        itemAdapter = ItemAdapter(this, rvItemListDetails)
        rvItemListDetails.isNestedScrollingEnabled = false
        rvItemListDetails.adapter = itemAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === 1) {
            if (resultCode === Activity.RESULT_OK) {
                filterRequest = data!!.getParcelableExtra(AppConstants.POST_DATA)
                setFilter(filterRequest)
                getDetailsApi(filterRequest!!)
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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

    private fun setFilter(filterRequest: FilterRequest?) {
        if (filterRequest != null) {
            filterList.clear()
            if (!TextUtils.isEmpty(filterRequest.sort_no)) {
                when {
                    filterRequest.sort_no.toInt() == 1 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.nearby), false, 1)
                        filterList.add(filterModal)
                    }
                    filterRequest.sort_no.toInt() == 2 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.popular), false, 1)
                        filterList.add(filterModal)
                    }
                    filterRequest.sort_no.toInt() == 3 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.recently_added), false, 1)
                        filterList.add(filterModal)
                    }
                    filterRequest.sort_no.toInt() == 4 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.price_high_to_low), false, 1)
                        filterList.add(filterModal)
                    }
                    filterRequest.sort_no.toInt() == 5 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.price_low_to_high), false, 1)
                        filterList.add(filterModal)
                    }
                    else -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.popular), false, 1)
                        filterList.add(filterModal)
                    }
                }

            }
            if (!TextUtils.isEmpty(filterRequest.item_type)) {
                if (filterRequest.item_type.equals("1", ignoreCase = true)) {
                    val filterModal = FilterModal("Condition :", "New", false, 2)
                    filterList.add(filterModal)
                } else {
                    val filterModal = FilterModal("Condition :", getString(R.string.used), false, 2)
                    filterList.add(filterModal)
                }
            }
            if (!TextUtils.isEmpty(filterRequest.meet_up)) {
                if (filterRequest.meet_up.equals("1", ignoreCase = true)) {
                    val filterModal = FilterModal("Deal Option :", "Meet-up", false, 3)
                    filterList.add(filterModal)
                }
            }
            if (!TextUtils.isEmpty(filterRequest.min_price) && !TextUtils.isEmpty(filterRequest.max_price)) {
                val filterModal = FilterModal(getString(R.string.pric), Utils.getPreferencesString(this@CategoryDetailsActivity, AppConstants.CURRENCY_SYMBOL) + filterRequest.min_price + " - "+Utils.getPreferencesString(this@CategoryDetailsActivity,AppConstants.CURRENCY_SYMBOL) + filterRequest.max_price, false, 4)
                filterList.add(filterModal)
            } else if (!TextUtils.isEmpty(filterRequest.min_price) && TextUtils.isEmpty(filterRequest.max_price)) {
                val filterModal = FilterModal(getString(R.string.pric), "From "+Utils.getPreferencesString(this@CategoryDetailsActivity,AppConstants.CURRENCY_SYMBOL) + filterRequest.min_price, false, 4)
                filterList.add(filterModal)
            } else if (TextUtils.isEmpty(filterRequest.min_price) && !TextUtils.isEmpty(filterRequest.max_price)) {
                val filterModal = FilterModal(getString(R.string.pric), "Up to "+Utils.getPreferencesString(this@CategoryDetailsActivity,AppConstants.CURRENCY_SYMBOL) + filterRequest.max_price, false, 4)
                filterList.add(filterModal)
            }
        }
        filterAdapter.showData(filterList)
        filterAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun getDetailsApi_android(request: FilterRequest) {
        pvProgressDetail.visibility = View.VISIBLE

        ServiceHelper().categoryPostService_android(request,
                object : ServiceHelper.OnResponse {

                    override fun onSuccess(response: Response<*>) {
                        itemsList.clear()
                        pvProgressDetail.visibility = View.GONE

                        if (fromLikeEvent == true) {
                            if (comingFrom.equals("CategoryDetailsActivity")) {
                                rvItemListDetails.scrollToPosition(positionFromLikeEvent)
                            }
                        }
                        val categoryPostResponse = response.body() as CategoryPostResponse
                        if (categoryPostResponse.data.subCategoryList.size > 0) {
                            scroll_view_category_detail.visibility = View.VISIBLE
                            setSubcategoryData(categoryPostResponse.data.subCategoryList)
                        }
                        if (categoryPostResponse.data.items.size > 0) {
                            scroll_view_category_detail.visibility = View.VISIBLE
                            itemsList = categoryPostResponse.data.items
                            itemAdapter.setData(itemsList)
                            itemAdapter.notifyDataSetChanged()
                        }
                        else if(categoryPostResponse.data.subCategoryList.size == 0
                                && categoryPostResponse.data.items.size == 0){
//                            Log.e(TAG, "onSuccess@3: ")
                            scroll_view_category_detail.visibility = View.GONE
                            tvMssgData.visibility = View.VISIBLE
                        }
                        else {
//                            Log.e(TAG, "onSuccess@4: ")
                            itemsList.clear()
                            itemAdapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(msg: String?) {
//                        Log.e(TAG, "onFailure: "+msg)
                        pvProgressDetail.visibility = View.GONE
                        Utils.showSimpleMessage(this@CategoryDetailsActivity, msg!!).show()
                    }
                })
    }

    private fun getDetailsApi(request: FilterRequest) {
        pvProgressDetail.visibility = View.VISIBLE

        ServiceHelper().categoryPostService(request,
                object : ServiceHelper.OnResponse {

                    override fun onSuccess(response: Response<*>) {

                        itemsList.clear()
                        pvProgressDetail.visibility = View.GONE

                        if (fromLikeEvent == true) {
                            if (comingFrom.equals("CategoryDetailsActivity")) {
                                rvItemListDetails.scrollToPosition(positionFromLikeEvent)
                            }
                        }
                        val categoryPostResponse = response.body() as CategoryPostResponse
                        if (categoryPostResponse.data.subCategoryList.size > 0) {
                            Log.e(TAG, "onSuccess@1: ")
                            Log.e(TAG, "categoryPostResponse: "+categoryPostResponse.data.subCategoryList)
                            scroll_view_category_detail.visibility = View.VISIBLE
                            setSubcategoryData(categoryPostResponse.data.subCategoryList)
                        }
                        if (categoryPostResponse.data.items.size > 0) {
                            Log.e(TAG, "onSuccess@2: ")
                            Log.e(TAG, "categoryPostResponse: "+categoryPostResponse.data.items)
                            scroll_view_category_detail.visibility = View.VISIBLE
                            itemsList = categoryPostResponse.data.items
                            itemAdapter.setData(itemsList)
                            itemAdapter.notifyDataSetChanged()
                        }
                        else if(categoryPostResponse.data.subCategoryList.size == 0
                                && categoryPostResponse.data.items.size == 0){
                            Log.e(TAG, "onSuccess@3: ")
                            scroll_view_category_detail.visibility = View.GONE
                            tvMssgData.visibility = View.VISIBLE
                        }
                        else {
                            Log.e(TAG, "onSuccess@4: ")
                            itemsList.clear()
                            itemAdapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(msg: String?) {
                        Log.e(TAG, "onFailure: "+msg)
                        pvProgressDetail.visibility = View.GONE
                        Utils.showSimpleMessage(this@CategoryDetailsActivity, msg!!).show()
                    }
                })
    }

//    @Subscribe
//    fun MessageEventOnLikeCategory(event: MessageEventOnLikeCategory) {
//        fromLikeEvent = true
//        comingFrom = event.comingFrom
//        positionFromLikeEvent = event.position
//        if (Utils.isOnline(this@CategoryDetailsActivity)) {
//            if (typeForEventBus == 1) {
//                llParent.visibility = View.GONE
//                filterRequest = FilterRequest(intent.getIntExtra(AppConstants.CATEGORY_ID, 0).toString(), typeForEventBus, Utils.getPreferencesString(this, AppConstants.USER_ID), "2", "", "", "", "", "", "", intent.getIntExtra(AppConstants.ITEM_ID, 0))
//                getDetailsApi(filterRequest!!)
//
//            } else {
//                tvCategorySelected.text = intent.getStringExtra(AppConstants.SUB_CATEGORY)
//                llParent.visibility = View.VISIBLE
//                filterRequest = FilterRequest(intent.getIntExtra(AppConstants.CATEGORY_ID, 0).toString(), typeForEventBus, Utils.getPreferencesString(this, AppConstants.USER_ID), "2", "", "", "", "", "", "")
//                getDetailsApi(filterRequest!!)
//            }
//        } else {
//            Utils.showSimpleMessage(this@CategoryDetailsActivity, getString(R.string.check_internet)).show()
//        }
//        Handler().postDelayed(Runnable {
//            fromLikeEvent = false
//        }, 2500)
//    }

    override fun onStart() {
        super.onStart()
        try {
            EventBus.getDefault().register(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}