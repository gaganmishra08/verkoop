package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.BuyPropertyAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.SpacingItem
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.buy_cars_activity.*
import kotlinx.android.synthetic.main.toolbar_cars_properties.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Response

class BuyPropertiesActivity : AppCompatActivity() {
    private var itemsList = ArrayList<ItemHome>()
    private var isLoading = false
    private var totalPageCount: Int? = null
    private var currentPage = 0
    private lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var buyPropertyAdapter: BuyPropertyAdapter
    private var carBrandList = ArrayList<CarType>()
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
            currentPage = 1
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
        etSearchFullCar.text = getString(R.string.search_properties)
        tvHeaderCar.text = getString(R.string.properties)
        iv_leftCar.setOnClickListener { onBackPressed() }
        ivFavouriteCar.setOnClickListener {
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
        }
        etSearchFullCar.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(AppConstants.CATEGORY_NAME,getString(R.string.search_properties))
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
        linearLayoutManager = GridLayoutManager(this, 2)
        linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (buyPropertyAdapter.getItemViewType(position)) {
                    buyPropertyAdapter.CATEGORY_LIST_ROW -> 2
                    buyPropertyAdapter.SHOW_LOADER -> 2
                    else -> 1
                }
            }
        }
        rvBuyCarList.layoutManager = linearLayoutManager
        rvBuyCarList.addItemDecoration(SpacingItem(2, 20, false))
        rvBuyCarList.setHasFixedSize(false)
        buyPropertyAdapter = BuyPropertyAdapter(this, rvBuyCarList)
        rvBuyCarList.adapter = buyPropertyAdapter
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
                    if (Utils.isOnline(this@BuyPropertiesActivity)) {
                        itemsList.add(ItemHome(isLoading=true))
                        buyPropertyAdapter.notifyItemInserted((itemsList.size-1)+1)
                        currentPage += 1
                        getItemService(0)
                    } else {
                      //  Utils.showSimpleMessage(this@BuyPropertiesActivity, getString(R.string.check_internet)).show()
                    }
                }
            }
        }
    }

    private fun getItemService(lodeMode:Int) {
        isLoading = true
        ServiceHelper().getBuyCarService(HomeRequest(2,Utils.getPreferencesString(this, AppConstants.COUNTRY_CODE), currentPage), Utils.getPreferencesString(this, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                scCars.isRefreshing = false
                isLoading = false
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbCars.visibility = View.GONE
                rvBuyCarList.visibility = View.VISIBLE
                if(currentPage>1&&itemsList.size>0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    buyPropertyAdapter.notifyItemRemoved(scrollPosition+1)
                }

                val homeDataResponse = response.body() as BuyCarResponse?
                if (homeDataResponse!!.data != null) {
                    setApiData(lodeMode,homeDataResponse.data)
                }
                if (fromLikeEvent == true) {
                    if (comingFrom.equals("BuyPropertyAdapter")) {
                        rvBuyCarList.scrollToPosition(positionFromLikeEvent)
                    }
                }
            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Utils.showSimpleMessage(this@BuyPropertiesActivity, msg!!).show()
                scCars.isRefreshing = false
                pbCars.visibility = View.GONE
                isLoading = false
                if(currentPage>1&&itemsList.size>0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    buyPropertyAdapter.notifyItemRemoved(scrollPosition)
                }
                if (currentPage >= 2) {
                    currentPage -= 1
                }
            }
        })
    }

    private fun setApiData(loadMore:Int,data: DataCarResponse?) {
        val nameList = arrayOf("East", "West", "North East", "North", "Central")
        val subList = arrayOf(1, 2, 3, 4, 5)
        val image = arrayOf("public/images/zones/d_east.png", "public/images/zones/d_west.png", "public/images/zones/d_south.png", "public/images/zones/d_north.png", "public/images/zones/d_east.png")
        for (i in nameList.indices) {
            val dataCar = CarType(subList[i], nameList[i], image[i])
            carBrandList.add(dataCar)
        }
        if (currentPage <= 1) {
            buyPropertyAdapter.setZoneType(carBrandList)
        }

        totalPageCount = data!!.totalPage
        if(loadMore!=1) {
            itemsList.addAll(data.items)
        }else{
            itemsList.clear()
            itemsList.addAll(data.items)
        }
        buyPropertyAdapter.setData(itemsList)
        buyPropertyAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
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

    @Subscribe
    fun MessageEventOnLikeBuyProperty(event: MessageEventOnLikeBuyProperty) {
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