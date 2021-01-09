package com.verkoopapp.activity

import android.graphics.Point
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.AdvertisementAdapter
import com.verkoopapp.models.BannerDetailResponse
import com.verkoopapp.models.ItemHome
import com.verkoopapp.models.MessageEventOnLikeAdvertisementAdapter
import com.verkoopapp.models.MessageEventOnLikeUserProfile
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.user_profile_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Response


class AdvertisementActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var advertisementAdapter: AdvertisementAdapter
    private var itemsList = ArrayList<ItemHome>()
    private var categoryId: String = ""
    private var userId: String = ""
    private var isLoading = false
    private var totalPageCount: Int? = null
    private var currentPage = 0
    private var fromLikeEvent: Boolean = false
    private var positionFromLikeEvent: Int = 0
    private var typeForEventBus: Int = 0
    private var comingFrom: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        rvFavouriteList.visibility = View.GONE
        categoryId = intent.getStringExtra(AppConstants.BANNERID).toString()
        userId = intent.getStringExtra(AppConstants.USER_ID).toString()
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        setData()
        setAdapter(width)
        if (Utils.isOnline(this)) {

            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            pbProgressFav.visibility = View.VISIBLE
            currentPage = 0
            getItemService(0)
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter(width: Int) {
        linearLayoutManager = GridLayoutManager(this, 2)
        linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (advertisementAdapter.getItemViewType(position)) {
                    advertisementAdapter.BANNER_ROW -> 2
                    advertisementAdapter.SHOW_LOADER -> 2
                    else ->
                        1
                }
            }
        }
        rvFavouriteList.layoutManager = linearLayoutManager
        rvFavouriteList.setHasFixedSize(false)
        advertisementAdapter = AdvertisementAdapter(this, width)
        rvFavouriteList.adapter = advertisementAdapter
        rvFavouriteList.addOnScrollListener(recyclerViewOnScrollListener)
    }
    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val totalItemCount = linearLayoutManager.itemCount
            val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

            if (!isLoading && currentPage != totalPageCount) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= MifareUltralight.PAGE_SIZE) {
                    if (Utils.isOnline(this@AdvertisementActivity)) {
                        itemsList.add(ItemHome(isLoading=true))
                        Log.e("AddItemPosition",(itemsList.size-1).toString())
                        advertisementAdapter.notifyItemInserted((itemsList.size-1)+1)
                        currentPage += 1
                        getItemService(0)
                    } else {
                        //   Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
                    }
                }
            }
        }
    }


    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text = getString(R.string.details)
    }

    private fun getItemService(loadMore:Int) {
        isLoading=true
        ServiceHelper().getBannerItemService(categoryId.toString(), userId,currentPage, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProgressFav.visibility = View.GONE
                isLoading = false
                val homeDataResponse = response.body() as BannerDetailResponse?
              /*  if(swipeContainer!=null) {
                    swipeContainer.isRefreshing = false
                }*/
                if(currentPage>1&&itemsList.size>0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    advertisementAdapter.notifyItemRemoved(scrollPosition+1)
                }
                if (homeDataResponse!!.data != null) {
                    rvFavouriteList.visibility = View.VISIBLE
                   // itemsList.clear()
                    itemsList.addAll(homeDataResponse.data!!.items)
                    advertisementAdapter.setData(itemsList)
                    if(homeDataResponse.data!!.banner!=null) {
                        advertisementAdapter.setBanner(homeDataResponse.data!!.banner!!)
                    }
                    advertisementAdapter.notifyDataSetChanged()
                }

                if (fromLikeEvent == true) {
                    if (comingFrom.equals("AdvertisementAdapter")) {
                        rvUserPostsList.scrollToPosition(positionFromLikeEvent)
                    }
                }
            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Utils.showSimpleMessage(this@AdvertisementActivity, msg!!).show()
                pbProgressFav.visibility = View.GONE
              /*  if(swipeContainer!=null) {
                    swipeContainer.isRefreshing = false
                }*/
                isLoading = false
                if (currentPage >= 2) {
                    currentPage -= 1
                }
                if(currentPage>1&&itemsList.size>0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    advertisementAdapter.notifyItemRemoved(scrollPosition)
                }
            }
        })
    }


    @Subscribe
    fun MessageEventOnLikeAdvertisementAdapter(event: MessageEventOnLikeAdvertisementAdapter) {
        fromLikeEvent = true
        comingFrom = event.comingFrom
        positionFromLikeEvent = event.position
        if (Utils.isOnline(this)) {

            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            pbProgressFav.visibility = View.VISIBLE
            currentPage = 0
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