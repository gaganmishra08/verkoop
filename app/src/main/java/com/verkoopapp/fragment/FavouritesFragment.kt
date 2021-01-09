package com.verkoopapp.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.FavouritesAdapter
import com.verkoopapp.interfaces.OnItemClickListener
import com.verkoopapp.models.FavouritesResponse
import com.verkoopapp.models.ItemHome
import com.verkoopapp.models.SearchKeywordMultipleDataRequest
import com.verkoopapp.models.SearchMultipleKeywordResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.SpacingItem
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.favourites_activity.pbProgressFav
import kotlinx.android.synthetic.main.favourites_activity.rvFavouriteList
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class FavouritesFragment : Fragment(), OnItemClickListener {
    private lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var favouritesAdapter: FavouritesAdapter
    private var itemsList = ArrayList<ItemHome>()
    private var comingFrom = 0
    private var hasBeenVisibleOnce = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvHeaderLoc.setText("My Favourite")
        ivLeftLocation.visibility = View.GONE
        ivLeftLocation.setOnClickListener {
            fragmentManager?.popBackStack()
        }
//        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        setAdapter()
//        if (comingFrom == 3) {
//            if (intent.getStringExtra("visionData") != null) {
//                val string: String = intent.getStringExtra("visionData")!!
//                callSearchKeywordMultipleData(string)
//            }
//        } else {

//        }
    }

    private fun callSearchKeywordMultipleData(string: String) {
        if (context?.let { Utils.isOnline(it) }!!) {
            searchKeywordMultipleData(string)
        } else {
            Utils.showSimpleMessage(context!!, getString(R.string.check_internet)).show()
            callSearchKeywordMultipleData(string)
        }
    }

    private fun searchKeywordMultipleData(string: String) {
        pbProgressFav.visibility = View.VISIBLE
        context?.let { Utils.getPreferencesString(it, AppConstants.USER_ID).toInt() }?.let { SearchKeywordMultipleDataRequest(string, it) }?.let {
            ServiceHelper().searchKeywordMultipleDataService(it,
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
                                    tvNoMsgData.visibility = View.GONE
                                } else {
                                    tvNoMsgData.visibility = View.VISIBLE
                                    //Utils.showSimpleMessage(context!!, "No data found.").show()
                                }
                            }
                        }

                        override fun onFailure(msg: String?) {
                            Utils.showSimpleMessage(context!!, msg!!).show()
                            pbProgressFav.visibility = View.GONE
                        }
                    })
        }
    }

    private fun setAdapter() {
        linearLayoutManager = GridLayoutManager(context!!, 2)
        rvFavouriteList.layoutManager = linearLayoutManager
//        rvFavouriteList.addItemDecoration(GridSpacingItemDecoration(2, Utils.dpToPx(context!!, 2F).toInt(), false))
        rvFavouriteList.addItemDecoration(SpacingItem(2, 20, false))
        favouritesAdapter = FavouritesAdapter(activity!!, rvFavouriteList, 0, this)

        rvFavouriteList.adapter = favouritesAdapter
        //  rvFavouriteList.addOnScrollListener(recyclerViewOnScrollListener)
        ivLeftLocation.setOnClickListener {
//            onBackPressed()
        }
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

    private fun getFavouriteApi(isProgress: Boolean) {
        if (isProgress)
            pbProgressFav.visibility = View.VISIBLE
        ServiceHelper().getFavouritesService(Utils.getPreferencesString(context!!, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbProgressFav.visibility = View.GONE
                val responseFav = response.body() as FavouritesResponse
                if (responseFav.data != null) {
                    if (responseFav.data.items.isNotEmpty()) {
                        itemsList = responseFav.data.items
                        favouritesAdapter.setData(itemsList)
                        favouritesAdapter.notifyDataSetChanged()
                        tvNoMsgData.visibility = View.GONE

                    } else {
                        favouritesAdapter.setData(arrayListOf())
                        favouritesAdapter.notifyDataSetChanged()
                        //   Utils.showSimpleMessage(context!!, "No data found.").show()
                        tvNoMsgData.visibility = View.VISIBLE
                    }
                }


            }

            override fun onFailure(msg: String?) {
                pbProgressFav.visibility = View.GONE
                context?.let { Utils.showSimpleMessage(it, msg!!).show() }
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


    companion object {
        fun newInstance(): FavouritesFragment {
            val arg = Bundle()
            val fragment = FavouritesFragment()
            fragment.arguments = arg
            return fragment
        }
    }


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
                        try {
                            itemsList.removeAt(position)
                            favouritesAdapter.setData(itemsList)
                            favouritesAdapter.notifyDataSetChanged()
                            if (itemsList.size == 0)
                                tvNoMsgData.visibility = View.VISIBLE
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        favouritesAdapter.setData(itemsList)
                        favouritesAdapter.notifyDataSetChanged()
                    }
                })
    }

    override fun setUserVisibleHint(visible: Boolean) {
        super.setUserVisibleHint(true)
        if (this.isVisible) {
            if (visible) {
                if (context?.let { Utils.isOnline(it) }!!) {
                    getFavouriteApi(true)
                } else {
                    Utils.showSimpleMessage(context!!, getString(R.string.check_internet)).show()
                }
                hasBeenVisibleOnce = true
            }
        }
    }
}