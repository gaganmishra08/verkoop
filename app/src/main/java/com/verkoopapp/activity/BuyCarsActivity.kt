package com.verkoopapp.activity

import android.content.Intent
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import com.verkoopapp.R
import com.verkoopapp.adapter.BuyCarsAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.buy_cars_activity.*
import kotlinx.android.synthetic.main.toolbar_cars_properties.*
import retrofit2.Response
import android.app.Activity
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.utils.SpacingItem
import kotlinx.android.synthetic.main.category_details_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class BuyCarsActivity: AppCompatActivity() {
    private var itemsList = ArrayList<ItemHome>()
    private var isLoading = false
    private var totalPageCount: Int? = null
    private var currentPage = 0
    private lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var buyCarsAdapter:BuyCarsAdapter
    private var fromLikeEvent: Boolean = false
    private var positionFromLikeEvent: Int = 0
    private var typeForEventBus: Int = 0
    private var comingFrom: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buy_cars_activity)
        setData()
        setBuyCarAdapter()
        if (Utils.isOnline(this)) {
            itemsList.clear()
            currentPage=1
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            pbCars.visibility = View.VISIBLE
            getItemService(0)
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setData() {
        scCars.setOnRefreshListener {
            if (Utils.isOnline(this)) {
                currentPage = 1
                getItemService(1)
            } else {
                scCars.isRefreshing = false
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
        scCars.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary)
        iv_leftCar.setOnClickListener { onBackPressed() }

        ivFavouriteCar.setOnClickListener {
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
        }
        etSearchFullCar.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(AppConstants.CATEGORY_NAME,getString(R.string.search_cars))
            startActivityForResult(intent, 2)
        }
        tvSellCar.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
             startActivityForResult(intent, 2)
        }
        ivChatCar.setOnClickListener {
            val intent = Intent(this, ChatInboxActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setBuyCarAdapter() {
            linearLayoutManager =  GridLayoutManager(this,2 )
            linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (buyCarsAdapter.getItemViewType(position)) {
                        buyCarsAdapter.BRAND_LIST_ROW -> 2
                        buyCarsAdapter.CATEGORY_LIST_ROW -> 2
                        buyCarsAdapter.SHOW_LOADER -> 2
                        else -> 1
                    }
                }
            }
        rvBuyCarList.layoutManager = linearLayoutManager
        rvBuyCarList.setHasFixedSize(false)
        rvBuyCarList.addItemDecoration(SpacingItem(2, 20, false))
        buyCarsAdapter = BuyCarsAdapter(this, rvBuyCarList)
        rvBuyCarList.adapter = buyCarsAdapter
        rvBuyCarList.addOnScrollListener(recyclerViewOnScrollListener)

    }

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val totalItemCount = linearLayoutManager.itemCount
            val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

            if (!isLoading && currentPage != totalPageCount) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= MifareUltralight.PAGE_SIZE) {
                    if (Utils.isOnline(this@BuyCarsActivity)) {
                        itemsList.add(ItemHome(isLoading=true))
                        buyCarsAdapter.notifyItemInserted((itemsList.size-1)+2)
                        currentPage += 1
                        getItemService(0)
                    } else {
                      //  Utils.showSimpleMessage(this@BuyCarsActivity, getString(R.string.check_internet)).show()
                    }
                }
            }
        }
    }

    private fun getItemService(loadMore:Int) {
        isLoading = true
        ServiceHelper().getBuyCarService(HomeRequest(1,Utils.getPreferencesString(this, AppConstants.COUNTRY_CODE),currentPage), Utils.getPreferencesString(this, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                scCars.isRefreshing = false
                isLoading = false
                if(currentPage>1&&itemsList.size>0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    buyCarsAdapter.notifyItemRemoved(scrollPosition+2)
                }
              window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbCars.visibility = View.GONE
                rvBuyCarList.visibility= View.VISIBLE

                val homeDataResponse = response.body() as BuyCarResponse?
                if (homeDataResponse!!.data != null) {
                    setApiData(loadMore,homeDataResponse.data)
                }
                if (fromLikeEvent == true) {
                    if (comingFrom.equals("BuyCarsAdapter")) {
                        rvBuyCarList.scrollToPosition(positionFromLikeEvent)
                    }
                }
            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Utils.showSimpleMessage(this@BuyCarsActivity, msg!!).show()
                pbCars.visibility = View.GONE
                scCars.isRefreshing = false
                isLoading = false
                if(currentPage>=2){
                    currentPage -= 1
                }
                if(currentPage>1&&itemsList.size>0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    buyCarsAdapter.notifyItemRemoved(scrollPosition)
                }
            }
        })
    }

    private fun setApiData(loadMore:Int,data: DataCarResponse?) {
        if(data!!.brands.size>0&&data.car_types.size>0) {
          buyCarsAdapter.setBrandAndTypeList(data.brands, data.car_types)
        }
        totalPageCount = data.totalPage
        if(loadMore!=1) {
            itemsList.addAll(data.items)
        }else{
            itemsList.clear()
            itemsList.addAll(data.items)
        }
        buyCarsAdapter.setData(itemsList)
        buyCarsAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION,0)
                if(result==1){
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

    @Subscribe
    fun MessageEventOnLikeBuyCars(event: MessageEventOnLikeBuyCars) {
        fromLikeEvent = true
        comingFrom = event.comingFrom
        positionFromLikeEvent = event.position
        if (Utils.isOnline(this)) {
            itemsList.clear()
            currentPage=1
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            pbCars.visibility = View.VISIBLE
            getItemService(0)
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
        Handler().postDelayed(Runnable {
            fromLikeEvent = false
        }, 2500)
    }

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




