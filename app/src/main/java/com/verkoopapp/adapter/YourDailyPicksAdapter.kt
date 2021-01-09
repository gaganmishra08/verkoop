package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.ProductDetailsActivity
import com.verkoopapp.activity.UserProfileActivity
import com.verkoopapp.fragment.HomeFragment
import com.verkoopapp.models.DisLikeResponse
import com.verkoopapp.models.ItemHome
import com.verkoopapp.models.LickedRequest
import com.verkoopapp.models.LikedResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_row.*
import retrofit2.Response


class YourDailyPicksAdapter(private val context:Context,private val recyclerView:Int,private val itemsList: ArrayList<ItemHome>,private val homeFragment: HomeFragment):RecyclerView.Adapter<YourDailyPicksAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var width = 0
    lateinit var price: String;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_row, parent, false)
        val params = view.layoutParams
        params.width = (recyclerView - 60) / 2
        width = params.width
        Log.e("TAG", "onCreateViewHolder: "+itemsList)
        Log.e("TAG", "onCreateViewHolder: "+itemsList.size)
        //  view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = itemsList[position]

        holder.bind(data)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: ItemHome) {
            viewItem.visibility = View.VISIBLE
//            llSideDividerHome.visibility = View.VISIBLE
            ivProductImageHome.layoutParams.height = width - 16
            tvNameHome.text = data.username
            tvProductHome.text = data.name
            try {
                if (data.item_type == 1) {
                    tvConditionHome.text = "New"
                    iv_new.visibility=View.VISIBLE
                    iv_used.visibility=View.GONE
                } else {
                    tvConditionHome.text = context.getString(R.string.used)
                    iv_new.visibility=View.GONE
                    iv_used.visibility=View.VISIBLE
                }
                if (data.is_sold == 1) {
                    tvSoldFav.visibility = View.VISIBLE
                } else {
                    tvSoldFav.visibility = View.GONE
                }
                if (data.is_like) {
                    tvLikesHome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_colored, 0, 0, 0)
                } else {
                    tvLikesHome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_grey, 0, 0, 0)
                }
//                tvLikesHome.text = data.items_like_count.toString()
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
                price= Utils.convertCurrency(context, data.currency!!, data.price).toString()
            tvItemPriceHome.text = price
                if (!TextUtils.isEmpty(data.image_url)) {
                    Picasso.with(context).load(AppConstants.IMAGE_URL + data.image_url)
                            .resize(720, 720)
                            .centerCrop()
                            .error(R.mipmap.post_placeholder)
                            .placeholder(R.mipmap.post_placeholder)
                            .into(ivProductImageHome)

                } else {
                    ivProductImageHome.setImageResource(R.mipmap.post_placeholder)
                }

                itemView.setOnClickListener {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(AppConstants.ITEM_ID, data.id)
                    intent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                    intent.putExtra(AppConstants.COMING_FROM, "YourDailyPicksAdapter")
                    homeFragment.startActivityForResult(intent, 1777)
//                context.startActivity(intent)

                }
                tvLikesHome.setOnClickListener {
                    if (!data.isClicked) {
                        if (data.like_id > 0) {
                            data.isClicked = !data.isClicked
                            deleteLikeApi(adapterPosition, data.like_id)
                        } else {
                            data.isClicked = !data.isClicked
                            lickedApi(data.id, adapterPosition)
                        }
                    }

                }
            tvPostOn.text = StringBuilder().append(Utils.getDateDifference(data.created_at!!.date)).append(" ").append("ago").toString()
                llUserProfile.setOnClickListener {
                    if (Utils.getPreferencesString(context, AppConstants.USER_ID).toInt() != data.user_id) {
                        val reportIntent = Intent(context, UserProfileActivity::class.java)
                        reportIntent.putExtra(AppConstants.USER_ID, data.user_id)
                        reportIntent.putExtra(AppConstants.USER_NAME, data.username)
                        context.startActivity(reportIntent)
                    }
                }
            } catch (exc: Exception) {
                Log.e("TAG", "bindexc: " + exc.message)
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
                            notifyItemChanged(position)

                        }

                        override fun onFailure(msg: String?) {
                            itemsList[position].isClicked = !itemsList[position].isClicked
                            notifyItemChanged(position)
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
                            notifyItemChanged(position)
                        }

                        override fun onFailure(msg: String?) {
                            itemsList[position].isClicked = !itemsList[position].isClicked
                            notifyItemChanged(position)
                        }
                    })
        }

    }
}