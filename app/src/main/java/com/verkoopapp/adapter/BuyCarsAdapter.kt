package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.CarBrandsActivity
import com.verkoopapp.activity.CarsFilterActivity
import com.verkoopapp.activity.ProductDetailsActivity
import com.verkoopapp.activity.UserProfileActivity
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.car_brand_row.*
import kotlinx.android.synthetic.main.car_filter_row.*
import kotlinx.android.synthetic.main.item_row.*
import retrofit2.Response

class BuyCarsAdapter(private val context: Context, private var rvItemList: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    val BRAND_LIST_ROW = 0
    val CATEGORY_LIST_ROW = 1
    val ITEMS_ROW = 2
    val SHOW_LOADER = 3
    private var width = 0
    private var widthOrg = 0
    private var widthOrgCarType = 0
    private var itemsList = ArrayList<ItemHome>()
    private var brandsList = ArrayList<Brand>()
    private var carTypeLIst = ArrayList<CarType>()


    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> BRAND_LIST_ROW
            position == 1 -> CATEGORY_LIST_ROW
            itemsList[position-2].isLoading -> SHOW_LOADER
            else -> ITEMS_ROW
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            BRAND_LIST_ROW -> {
                view = mLayoutInflater.inflate(R.layout.car_brand_row, parent, false)
                val params = view.layoutParams
                params.width = rvItemList.width
                widthOrg = params.width
                CarBrandHolder(view)
            }
            CATEGORY_LIST_ROW -> {
                view = mLayoutInflater.inflate(R.layout.car_filter_row, parent, false)
                val params = view.layoutParams
                params.width = rvItemList.width
                widthOrgCarType = params.width
                CarFilterHolder(view)
            }
            SHOW_LOADER -> {
                view = mLayoutInflater.inflate(R.layout.show_loader_row, parent, false)
                ShowLoaderHolder(view)
            }
            else -> {
                view = mLayoutInflater.inflate(R.layout.item_row, parent, false)
                val params = view.layoutParams
                params.width = rvItemList.width / 2
                width = params.width
                //view.layoutParams = params
                ItemsHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size + 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == BRAND_LIST_ROW) {
            (holder as CarBrandHolder).bind(brandsList)
        } else if (position == CATEGORY_LIST_ROW) {
            (holder as CarFilterHolder).bind(carTypeLIst)
        }else if( itemsList[position-2].isLoading ){
            (holder as ShowLoaderHolder).bind()
        } else {
            val modal = itemsList[position - 2]
            (holder as ItemsHolder).bind(modal)
        }
    }
    inner class ShowLoaderHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{
        fun bind() {

        }

    }

    inner class ItemsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: ItemHome) {
            ll_condition.visibility = View.GONE
            ivProductImageHome.layoutParams.height = width - 16
            tvNameHome.text = data.username
            /*if (adapterPosition % 2 == 0) {
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
                        deleteLikeApi(adapterPosition - 2, data.like_id)
                    } else {
                        data.isClicked = !data.isClicked
                        lickedApi(data.id, adapterPosition - 2)
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
                intent.putExtra(AppConstants.USER_ID, data.user_id)
                intent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                intent.putExtra(AppConstants.COMING_FROM,"BuyCarsAdapter")
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

    inner class CarFilterHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(carTypeLIst: ArrayList<CarType>) {
            tvCarBodyType.visibility=View.GONE
            rvCarBodyType.visibility=View.GONE
            // llCarFilter.layoutParams.height = widthOrgCarType-20
           /* val mManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvCarBodyType.layoutManager = mManager
            val carBodyTypeAdapter = CarBodyTypeAdapter(context, widthOrgCarType, carTypeLIst, 0)
            rvCarBodyType.adapter = carBodyTypeAdapter*/
            ll15PerMonth.setOnClickListener {
                val intent = Intent(context, CarsFilterActivity::class.java)
                intent.putExtra(AppConstants.FILTER_ID, 1)
                intent.putExtra(AppConstants.FILTER_TYPE, context.getString(R.string.cost_filter))
                intent.putExtra(AppConstants.TYPE, 1)
                context.startActivity(intent)
            }
            llMore15PerMonth.setOnClickListener {
                val intent = Intent(context, CarsFilterActivity::class.java)
                intent.putExtra(AppConstants.FILTER_ID, 2)
                intent.putExtra(AppConstants.FILTER_TYPE, context.getString(R.string.cost_filter))
                intent.putExtra(AppConstants.TYPE, 1)
                context.startActivity(intent)
            }
            ll25PerMonth.setOnClickListener {
                val intent = Intent(context, CarsFilterActivity::class.java)
                intent.putExtra(AppConstants.FILTER_ID, 3)
                intent.putExtra(AppConstants.FILTER_TYPE, context.getString(R.string.cost_filter))
                intent.putExtra(AppConstants.TYPE, 1)
                context.startActivity(intent)
            }
            llMore25PerMonth.setOnClickListener {
                val intent = Intent(context, CarsFilterActivity::class.java)
                intent.putExtra(AppConstants.FILTER_ID, 4)
                intent.putExtra(AppConstants.FILTER_TYPE, context.getString(R.string.cost_filter))
                intent.putExtra(AppConstants.TYPE, 1)
                context.startActivity(intent)
            }
        }
    }

    inner class CarBrandHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(brandList: ArrayList<Brand>) {
            //  llBuyCar.layoutParams.height = widthOrg / 3
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvBrands.layoutManager = linearLayoutManager
            val brandListAdapter = BrandListAdapter(context, widthOrg, brandList)
            rvBrands.adapter = brandListAdapter
            tvViewAllBrands.setOnClickListener {
                val intent = Intent(context, CarBrandsActivity::class.java)
                intent.putParcelableArrayListExtra(AppConstants.CAR_BRAND_LIST, brandList)
                context.startActivity(intent)
            }

        }
    }

    fun setData(itemsData: ArrayList<ItemHome>) {
        itemsList = itemsData
    }

    fun setBrandAndTypeList(brands: ArrayList<Brand>, car_types: ArrayList<CarType>) {
        brandsList = brands
        carTypeLIst = car_types
    }

    private fun lickedApi(itemId: Int, position: Int) {
        val lickedRequest = LickedRequest(Utils.getPreferencesString(context, AppConstants.USER_ID), itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        val responseLike = response.body() as LikedResponse
                        itemsList[position].is_like = !itemsList[position].is_like
                        itemsList[position].items_like_count = itemsList[position].items_like_count + 1
                        itemsList[position].like_id = responseLike.like_id
                        notifyItemChanged(position + 2)
                    }

                    override fun onFailure(msg: String?) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        notifyItemChanged(position + 2)
                        //      Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    private fun deleteLikeApi(position: Int, lickedId: Int) {
        ServiceHelper().disLikeService(lickedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        val likeResponse = response.body() as DisLikeResponse
                        itemsList[position].is_like = !itemsList[position].is_like
                        itemsList[position].items_like_count = itemsList[position].items_like_count - 1
                        itemsList[position].like_id = 0
                        notifyItemChanged(position + 2)
                    }

                    override fun onFailure(msg: String?) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        notifyItemChanged(position + 2)
                    }
                })
    }
}