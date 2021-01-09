package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.FavouritesAdapter
import com.verkoopapp.interfaces.OnItemClickListener
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.GridSpacingItemDecoration
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class FavouritesActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var favouritesAdapter: FavouritesAdapter
    private var itemsList = ArrayList<ItemHome>()
    private var comingFrom = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        setAdapter()
        if (comingFrom == 3) {
            if (intent.getStringExtra("visionData") != null) {
                val string: String = intent.getStringExtra("visionData")!!
                callSearchKeywordMultipleData(string)
            }
        } else {
            if (Utils.isOnline(this)) {
                getFavouriteApi()
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }

    }

    private fun callSearchKeywordMultipleData(string: String) {
        if (Utils.isOnline(this)) {
            searchKeywordMultipleData(string)
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            callSearchKeywordMultipleData(string)
        }
    }

    private fun searchKeywordMultipleData(string: String) {
        pbProgressFav.visibility = View.VISIBLE
        ServiceHelper().searchKeywordMultipleDataService(SearchKeywordMultipleDataRequest(string, Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressFav.visibility = View.GONE
                        val searchItemResponse = response.body() as SearchMultipleKeywordResponse?
                        if (searchItemResponse != null) {
                            if (searchItemResponse.data.items.isNotEmpty()) {
                                itemsList.clear()
                                itemsList = (searchItemResponse.data.items as ArrayList<ItemHome>)
                                favouritesAdapter.setData(itemsList)
                                favouritesAdapter.notifyDataSetChanged()
                            } else {
                                tvMssgData.visibility = View.VISIBLE
                                Utils.showSimpleMessage(this@FavouritesActivity, "No data found.").show()
                            }
                        }
                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(this@FavouritesActivity, msg!!).show()
                        pbProgressFav.visibility = View.GONE
                    }
                })
    }

    private fun setAdapter() {
        linearLayoutManager = GridLayoutManager(this, 2)
        rvFavouriteList.layoutManager = linearLayoutManager
        rvFavouriteList.addItemDecoration(GridSpacingItemDecoration(2, Utils.dpToPx(this, 2F).toInt(), false))
        favouritesAdapter = FavouritesAdapter(this, rvFavouriteList, 0,this)
        rvFavouriteList.adapter = favouritesAdapter
        //  rvFavouriteList.addOnScrollListener(recyclerViewOnScrollListener)
        ivLeftLocation.setOnClickListener { onBackPressed() }
        if (comingFrom == 3) {
            tvHeaderLoc.text = "Search Result"
        } else if (comingFrom != 1) {
            tvHeaderLoc.text = getString(R.string.favourites)
        } else {
            tvHeaderLoc.text = getString(R.string.your_daily_picks)
        }
    }

/*private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val lastCompletelyVisibleItemPosition = (recyclerView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        val visibleItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        val totalItemCount = linearLayoutManager.itemCount
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
     //   Log.e("LastVisibleCount", visibleItemCount.toString())
      //  Log.e("LastVisibleCount2", lastCompletelyVisibleItemPosition.toString())


    }
}*/

    private fun getFavouriteApi() {
        pbProgressFav.visibility = View.VISIBLE
        ServiceHelper().getFavouritesService(Utils.getPreferencesString(this, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbProgressFav.visibility = View.GONE
                val responseFav = response.body() as FavouritesResponse
                if(responseFav.data!=null){
                    if (responseFav.data.items.isNotEmpty()) {
                        itemsList = responseFav.data.items
                        favouritesAdapter.setData(itemsList)
                        favouritesAdapter.notifyDataSetChanged()

                    } else {
                        Utils.showSimpleMessage(this@FavouritesActivity, "No data found.").show()
                    }
                }


            }

            override fun onFailure(msg: String?) {
                pbProgressFav.visibility = View.GONE
                Utils.showSimpleMessage(this@FavouritesActivity, msg!!).show()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                val adapterPosition = data!!.getIntExtra(AppConstants.ADAPTER_POSITION, 0)
                if (data.getStringExtra(AppConstants.TYPE).equals("soldItem", ignoreCase = true)) {
                    itemsList[adapterPosition].is_sold = 1
                    favouritesAdapter.notifyDataSetChanged()
                } else if (data.getStringExtra(AppConstants.TYPE).equals("deleteItem", ignoreCase = true)) {
                    itemsList.removeAt(adapterPosition)
                    favouritesAdapter.notifyDataSetChanged()
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    override fun onClick(position: Int, ID: Int) {
        ServiceHelper().disLikeService(ID,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        /*itemsList[position].isClicked = !itemsList[position].isClicked
                        val likeResponse = response.body() as DisLikeResponse
                        itemsList[position].is_like = !itemsList[position].is_like
                        itemsList[position].items_like_count = itemsList[position].items_like_count - 1
                        itemsList[position].like_id = 0
                        getFavouriteApi(false)*/
                        itemsList.removeAt(position)
                        favouritesAdapter.setData(itemsList)
                        favouritesAdapter.notifyDataSetChanged()
                    }

                    override fun onFailure(msg: String?) {
                        favouritesAdapter.setData(itemsList)
                        favouritesAdapter.notifyDataSetChanged()
                    }
                })
    }
}