package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.AdvertisementActivity
import com.verkoopapp.activity.ProductDetailsActivity
import com.verkoopapp.activity.UserProfileActivity
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adds_category_row.*
import kotlinx.android.synthetic.main.banner_row.*
import kotlinx.android.synthetic.main.item_row.*
import retrofit2.Response


class AdvertisementAdapter(private val context: Context, private val widthScreen: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    val BANNER_ROW = 0
    val ITEMS_ROW = 1
    val SHOW_LOADER = 2
    var width: Int = 0
    private var itemsList = ArrayList<ItemHome>()
    private var advertismentsList = ArrayList<Banner>()
    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> BANNER_ROW
            itemsList[position - 1].isLoading -> SHOW_LOADER
            else ->
                ITEMS_ROW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == BANNER_ROW) {
            view = mLayoutInflater.inflate(R.layout.banner_row, parent, false)
            return BannerViewHolder(view)
        } else if (viewType == SHOW_LOADER) {
            view = mLayoutInflater.inflate(R.layout.show_loader_row, parent, false)
          return ShowLoaderHolder(view)
        } else {
            view = mLayoutInflater.inflate(R.layout.item_row, parent, false)
            val params = view.layoutParams
            params.width = widthScreen / 2
            width = params.width
            //view.layoutParams = params
            return ItemsHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            position == BANNER_ROW -> (holder as BannerViewHolder).bind(advertismentsList)
            itemsList[position-1].isLoading -> (holder as ShowLoaderHolder).bind()
            else -> {
                val modal = itemsList[position - 1]
                (holder as ItemsHolder).bind(modal)
            }
        }
    }

    inner class BannerViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(advertismentsList: ArrayList<Banner>) {
            mDemoSliderBa.removeAllSliders()
            custom_indicatorBa.setDefaultIndicatorColor(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.light_gray))
            mDemoSliderBa.setCustomIndicator(custom_indicatorBa)
            for (i in 0 until advertismentsList.size) {
                val textSliderView = DefaultSliderView(context)
                //textSliderView.tex
                textSliderView.bundle(Bundle())
                textSliderView.bundle.putInt(AppConstants.CATEGORY_ID, advertismentsList[i].category_id)
                textSliderView.image(AppConstants.IMAGE_URL + advertismentsList[i].image)
                        .setOnSliderClickListener({ slider ->
                          /*  val intent = Intent(context, AdvertisementActivity::class.java)
                            intent.putExtra(AppConstants.CATEGORY_ID, slider.bundle.getInt(AppConstants.CATEGORY_ID, 0))
                            context.startActivity(intent)*/
                        }).scaleType = BaseSliderView.ScaleType.Fit

                if (mDemoSliderBa != null) {
                    mDemoSliderBa.addSlider(textSliderView)
                }
            }
            mDemoSliderBa.setDuration(3000)
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
                        deleteLikeApi(adapterPosition - 1, data.like_id)
                    } else {
                        data.isClicked = !data.isClicked
                        lickedApi(data.id, adapterPosition - 1)
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
                intent.putExtra(AppConstants.COMING_FROM,"AdvertisementAdapter")
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

    inner class ShowLoaderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind() {

        }

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
                        notifyItemChanged(position + 1)
                    }

                    override fun onFailure(msg: String?) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        notifyItemChanged(position + 1)
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
                        notifyItemChanged(position + 4)
                    }

                    override fun onFailure(msg: String?) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        notifyItemChanged(position + 4)
                    }
                })
    }

    fun setData(items: ArrayList<ItemHome>) {
        itemsList = items
    }

    fun setBanner(advertisments: ArrayList<Banner>) {
        advertismentsList = advertisments
    }
}