package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.*
import com.verkoopapp.fragment.HomeFragment
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.GridSpacingItemDecoration
import com.verkoopapp.utils.SpacingItem
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adds_category_row.*
import kotlinx.android.synthetic.main.cars_properties_row.*
import kotlinx.android.synthetic.main.item_row.*
import kotlinx.android.synthetic.main.your_daily_picks.*
import retrofit2.Response


class HomeAdapter(private val context: Context, private val rvItemList: Int, private val homeFragment: HomeFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    val CATEGORY_LIST_ROW = 0
    val PROPERTIES_ROW = 2
    val ITEMS_ROW = 4
    val SHOW_LOADER = 5
    val YOUR_DAILY_PICKS = 1
    val RECOMMENDED_YOU = 3
    private var width = 0
    private var comingFromOnLike = ""
    private var widthOrg = 0
    private var widthDaily = 0
    private var positionOnLikeEvent = 0
    private var itemsList = ArrayList<ItemHome>()
    private var dailyPicksList = ArrayList<ItemHome>()
    private var categoryList = ArrayList<Category>()
    private var advertismentsList = ArrayList<Advertisment>()

    var dailyPicksAdapter: YourDailyPicksAdapter? = null

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> CATEGORY_LIST_ROW
            position == 1 -> YOUR_DAILY_PICKS
            position == 2 -> PROPERTIES_ROW
            position == 3 -> RECOMMENDED_YOU
            itemsList[position - 4].isLoading -> SHOW_LOADER
            else ->
                ITEMS_ROW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            CATEGORY_LIST_ROW -> {
                view = mLayoutInflater.inflate(R.layout.adds_category_row, parent, false)
                val params = view.layoutParams
                params.width = rvItemList
                widthOrg = params.width
                AddsAndItemsHolder(view)
            }
            YOUR_DAILY_PICKS -> {
                view = mLayoutInflater.inflate(R.layout.your_daily_picks, parent, false)
                val params = view.layoutParams
                params.width = rvItemList
                widthDaily = params.width
                YourDailyPickHolder(view)
            }
            PROPERTIES_ROW -> {
                view = mLayoutInflater.inflate(R.layout.cars_properties_row, parent, false)
                CarAndPropertiesHolder(view)
            }
            RECOMMENDED_YOU -> {
                view = mLayoutInflater.inflate(R.layout.recommended_for_you, parent, false)
                RecommendedYouHolder(view)
            }
            SHOW_LOADER -> {
                view = mLayoutInflater.inflate(R.layout.show_loader_row, parent, false)
                ShowLoaderHolder(view)
            }
            else -> {
                view = mLayoutInflater.inflate(R.layout.item_row, parent, false)
                val params = view.layoutParams
                params.width = rvItemList / 2
                width = params.width
                //view.layoutParams = params
                ItemsHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size + 4
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            position == CATEGORY_LIST_ROW -> (holder as AddsAndItemsHolder).bind(categoryList, advertismentsList)
            position == YOUR_DAILY_PICKS -> {
                if (dailyPicksList.isEmpty()){

                }
                else {
                    (holder as YourDailyPickHolder).bind()
                    if (comingFromOnLike.equals("YourDailyPicksAdapter")) {
                        if (positionOnLikeEvent > 0) {
                            val spanCount = 2 // 3 columns

                            val spacing = 20 // 50px

                            val includeEdge = true
                            holder.rvYourDailyPicks.addItemDecoration(SpacingItem(spanCount, spacing, includeEdge))
                            holder.rvYourDailyPicks.scrollToPosition(positionOnLikeEvent)
                        }
                    }
                }
            }
            position == PROPERTIES_ROW -> (holder as CarAndPropertiesHolder).bind()
            position == RECOMMENDED_YOU -> (holder as RecommendedYouHolder).bind()
            itemsList[position - 4].isLoading -> (holder as ShowLoaderHolder).bind()
            else -> {
                val modal = itemsList[position - 4]
                (holder as ItemsHolder).bind(modal)
                if (comingFromOnLike.equals("RecommendedForYou")) {
                    if (positionOnLikeEvent > 0) {
                        val spanCount = 2 // 3 columns

                        val spacing = 20 // 50px

                        val includeEdge = true
                        holder.rvYourDailyPicks.addItemDecoration(SpacingItem(spanCount, spacing, includeEdge))
                        holder.rvYourDailyPicks.scrollToPosition(positionOnLikeEvent)
                    }
                }
            }
        }
    }

    inner class ShowLoaderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind() {

        }

    }

    inner class AddsAndItemsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(categoryList: ArrayList<Category>, advertismentsList: ArrayList<Advertisment>) {
            mDemoSlider.removeAllSliders()
//            custom_indicator.visibility=View.GONE
//            custom_indicator.setDefaultIndicatorColor(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.light_gray))
//            mDemoSlider.setCustomIndicator(custom_indicator)
            for (i in 0 until advertismentsList.size) {
                val textSliderView = DefaultSliderView(context)
                //textSliderView.tex
                textSliderView.bundle(Bundle())
                textSliderView.bundle.putString(AppConstants.CATEGORY_ID, advertismentsList[i].category_id)
                textSliderView.bundle.putString(AppConstants.BANNERID, advertismentsList[i].id.toString())
                if (advertismentsList[i].user_id != null) {
                    textSliderView.bundle.putString(AppConstants.USER_ID, advertismentsList[i].user_id.toString())
                }
                textSliderView.image(AppConstants.IMAGE_URL + advertismentsList[i].image)
                        .setOnSliderClickListener { slider ->
                            if (slider.bundle.getString(AppConstants.CATEGORY_ID) != null) {
                                mDemoSlider.isEnabled = false
                                Handler().postDelayed(Runnable {
                                    mDemoSlider.isEnabled = true
                                }, 1000)
                                val intent = Intent(context, AdvertisementActivity::class.java)
                                intent.putExtra(AppConstants.CATEGORY_ID, slider.bundle.getString(AppConstants.CATEGORY_ID, "0"))
                                intent.putExtra(AppConstants.BANNERID, slider.bundle.getString(AppConstants.BANNERID, "0"))
                                intent.putExtra(AppConstants.USER_ID, slider.bundle.getString(AppConstants.USER_ID, "0"))
                                context.startActivity(intent)
                            }
                            //    Toast.makeText(context,slider.bundle.getInt(AppConstants.CATEGORY_ID,0).toString(),Toast.LENGTH_SHORT).show()
                        }.scaleType = BaseSliderView.ScaleType.Fit

                if (mDemoSlider != null) {
                    mDemoSlider.addSlider(textSliderView)
                }
            }
            mDemoSlider.setDuration(3000)
//            rvCategoryHome.layoutParams.height = widthOrg / 3
            val linearLayoutManager = GridLayoutManager(context, 2,GridLayoutManager.HORIZONTAL,false)
            rvCategoryHome.layoutManager = linearLayoutManager
            val categoryAdapter = CategoryListAdapter(context as HomeActivity, categoryList, rvItemList)

//            rvCategoryHome.addItemDecoration(SpacingItem(2, 20, true))
            rvCategoryHome.setHasFixedSize(true)
            rvCategoryHome.adapter = categoryAdapter
            rvCategoryHome!!.adapter!!.notifyDataSetChanged()
            //   rvCategoryHome.setRecycledViewPool(v iewPool)
            tvViewAll.setOnClickListener {
                tvViewAll.isEnabled = false
                val intent = Intent(context, FullCategoriesActivity::class.java)
                context.startActivityForResult(intent, 2)
                Handler().postDelayed(Runnable {
                    tvViewAll.isEnabled = true
                }, 700)
            }

        }

    }

    inner class CarAndPropertiesHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind() {
            ivBuyCar.setOnClickListener {
                ivBuyCar.isEnabled = false
                val intent = Intent(context, BuyCarsActivity::class.java)
                (context as HomeActivity).startActivityForResult(intent, 2)
                Handler().postDelayed(Runnable {
                    ivBuyCar.isEnabled = true
                }, 1000)
            }
            ivBuyProperty.setOnClickListener {
                ivBuyProperty.isEnabled = false
                val intent = Intent(context, BuyPropertiesActivity::class.java)
                (context as HomeActivity).startActivityForResult(intent, 2)
                Handler().postDelayed(Runnable {
                    ivBuyProperty.isEnabled = true
                }, 1000)
            }
        }

    }

    inner class RecommendedYouHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind() {

        }

    }

    inner class ItemsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: ItemHome) {
            ivProductImageHome.layoutParams.height = width - 16
            tvNameHome.text = data.username
            /* if (adapterPosition % 2 == 0) {
                 llSideDividerHome.visibility = View.VISIBLE
             } else {
                 llSideDividerHome.visibility = View.GONE
             }*/
            if (data.is_like) {
                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_colored, 0, 0, 0)
            } else {
                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_grey, 0, 0, 0)
            }
//            tvLikesHome.text = data.items_like_count.toString()
            if (data.item_type == 1) {
                tvConditionHome.text = "New"
                iv_new.visibility=View.VISIBLE
                iv_used.visibility=View.GONE
            } else {
                tvConditionHome.text = context.getString(R.string.used)
                iv_new.visibility=View.GONE
                iv_used.visibility=View.VISIBLE
            }
            if (!TextUtils.isEmpty(data.profile_pic)) {
                Picasso.with(context)
                        .load(AppConstants.IMAGE_URL + data.profile_pic)
                        .resize(720, 720)
                        .centerCrop()
                        .error(R.mipmap.pic_placeholder)
                        .placeholder(R.mipmap.pic_placeholder)
                        .into(ivPicProfile)

            } else {
                ivPicProfile.setImageResource(R.mipmap.pic_placeholder)
            }
            if (!TextUtils.isEmpty(data.image_url)) {
                Picasso.with(context)
                        .load(AppConstants.IMAGE_URL + data.image_url)

                        .resize(720, 720)
                        .centerCrop()
                        .error(R.mipmap.post_placeholder)
                        .placeholder(R.mipmap.post_placeholder)
                        .into(ivProductImageHome)

            } else {
                ivProductImageHome.setImageResource(R.mipmap.post_placeholder)
            }
            tvLikesHome.setOnClickListener {

                if (!data.isClicked) {
                    if (data.like_id > 0) {
                        data.isClicked = !data.isClicked
                        deleteLikeApi(adapterPosition - 4, data.like_id)
                    } else {
                        data.isClicked = !data.isClicked
                        lickedApi(data.id, adapterPosition - 4)
                    }
                }
            }
            tvProductHome.text = data.name
            tvItemPriceHome.text = Utils.convertCurrency(context, data.currency!!, data.price)
            itemView.setOnClickListener {
                itemView.isEnabled = false
                Handler().postDelayed(Runnable {
                    itemView.isEnabled = true
                }, 1000)
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.ITEM_ID, data.id)
                intent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                intent.putExtra(AppConstants.COMING_FROM, "RecommendedForYou")
                context.startActivity(intent)
            }
            tvPostOn.text = StringBuilder().append(Utils.getDateDifference(data.created_at!!.date)).append(" ").append("ago")

            llUserProfile.setOnClickListener {
                val reportIntent = Intent(context, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID, data.user_id)
                reportIntent.putExtra(AppConstants.USER_NAME, data.username)
                context.startActivity(reportIntent)
            }
        }
    }

    inner class YourDailyPickHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind() {
            val spanCount = 2 // 3 columns

            val spacing = 20 // 50px

            val includeEdge = true
            val linearLayoutManager = GridLayoutManager(context,1,RecyclerView.HORIZONTAL,false)
            linearLayoutManager.isAutoMeasureEnabled = true
            rvYourDailyPicks.layoutManager = linearLayoutManager
            Log.e("<<YourDailyPickHolder>>", rvItemList.toString())
//            val dailyPicksAdapter = YourDailyPicksAdapter(context, rvItemList, itemsList, homeFragment)
            dailyPicksAdapter = YourDailyPicksAdapter(context, rvItemList, itemsList, homeFragment)
            rvYourDailyPicks.addItemDecoration(SpacingItem(spanCount, spacing, includeEdge))
            rvYourDailyPicks.setHasFixedSize(true)
            rvYourDailyPicks.adapter = dailyPicksAdapter
            tvViewAllDailyPicks.setOnClickListener {
                tvViewAllDailyPicks.isEnabled = false
                val intent = Intent(context, FavouritesActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 1)
                context.startActivity(intent)
                Handler().postDelayed(Runnable {
                    tvViewAllDailyPicks.isEnabled = true
                }, 700)
            }
        }
    }


    private fun lickedApi(itemId: Int, position: Int) {
        (context as HomeActivity).showLoader()
        val lickedRequest = LickedRequest(Utils.getPreferencesString(context, AppConstants.USER_ID), itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        val responseLike = response.body() as LikedResponse
                        itemsList[position].is_like = !itemsList[position].is_like
                        itemsList[position].items_like_count = itemsList[position].items_like_count + 1
                        itemsList[position].like_id = responseLike.like_id
                        notifyItemChanged(position + 4)
                        (context as HomeActivity).hideLoader()
                    }

                    override fun onFailure(msg: String?) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        notifyItemChanged(position + 4)
                        //      Utils.showSimpleMessage(homeActivity, msg!!).show()
                        (context as HomeActivity).hideLoader()
                    }
                })
    }

    private fun deleteLikeApi(position: Int, lickedId: Int) {
        (context as HomeActivity).showLoader()
        ServiceHelper().disLikeService(lickedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        val likeResponse = response.body() as DisLikeResponse
                        itemsList[position].is_like = !itemsList[position].is_like
                        itemsList[position].items_like_count = itemsList[position].items_like_count - 1
                        itemsList[position].like_id = 0
                        notifyItemChanged(position + 4)
                        (context as HomeActivity).hideLoader()
                    }

                    override fun onFailure(msg: String?) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        notifyItemChanged(position + 4)
                        (context as HomeActivity).hideLoader()
                    }
                })
    }

    fun setData(items: ArrayList<ItemHome>) {
        itemsList = items
    }

    fun setCategoryAndAddsData(advertisments: ArrayList<Advertisment>, categories: ArrayList<Category>) {
        categoryList = categories
        advertismentsList = advertisments
    }

    fun updateDailyPicksData(itemsDaily: ArrayList<ItemHome>) {
        dailyPicksList = itemsDaily
    }

    fun setLikeOnPostViewDaily(position: Int, isLiked: Boolean, totalLike: Int) {
        itemsList[position].is_like = isLiked
        itemsList[position].items_like_count = totalLike
        dailyPicksAdapter!!.notifyDataSetChanged()
    }

    fun getpositionFromLike(position: Int, comingFromLike: String) {
        positionOnLikeEvent = position
        comingFromOnLike = comingFromLike
    }

}