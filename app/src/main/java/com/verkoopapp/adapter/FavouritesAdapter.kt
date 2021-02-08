package com.verkoopapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.activity.ProductDetailsActivity
import com.verkoopapp.activity.UserProfileActivity
import com.verkoopapp.interfaces.OnItemClickListener
import com.verkoopapp.models.DisLikeResponse
import com.verkoopapp.models.ItemHome
import com.verkoopapp.models.LickedRequest
import com.verkoopapp.models.LikedResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_favourite.*
import retrofit2.Response


class FavouritesAdapter(private val context: Activity, private val rvFavourites: RecyclerView, private val comingFrom: Int, val listener: OnItemClickListener) : RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var listFavourites = ArrayList<ItemHome>()
    private var width = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.adapter_favourite, parent, false)
        //val params = view.layoutParams
        //params.width = rvFavourites.width / 2
        //width = params.width
        // likeDisLikeListener=context as FavouritesActivity
        //view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listFavourites.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listFavourites[position]
        holder.bind(data)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: ItemHome) {
            //ivProductImageHome.layoutParams.height = width
            tvNameHome.text = data.username
            tvProductHome.text = data.name
            /* if(adapterPosition %2==0){
                 llSideDividerHome.visibility=View.VISIBLE
             }else{
                 llSideDividerHome.visibility=View.GONE
             }*/
            if (data.item_type == 1) {
                tvConditionHome.text = "New"
                iv_new.visibility = View.VISIBLE
                iv_used.visibility = View.GONE
            } else {
                tvConditionHome.text = context.getString(R.string.used)
                iv_new.visibility = View.GONE
                iv_used.visibility = View.VISIBLE
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
//            tvLikesHome.text=data.items_like_count.toString()
            if (data.item_type == 1) {
                tvConditionHome.text = "New"
                iv_new.visibility = View.VISIBLE
                iv_used.visibility = View.GONE
            } else {
                tvConditionHome.text = context.getString(R.string.used)
                iv_new.visibility = View.GONE
                iv_used.visibility = View.VISIBLE
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
                Picasso.with(context).load(AppConstants.IMAGE_URL + data.image_url)
                        .resize(720, 720)
                        .centerCrop()
                        .error(R.mipmap.post_placeholder)
                        .placeholder(R.mipmap.post_placeholder)
                        .into(ivProductImageHome)

            } else {
                ivProductImageHome.setImageResource(R.mipmap.post_placeholder)
            }

            tvItemPriceHome.text = Utils.convertCurrency(context, data.currency!!, data.price)
            itemView.setOnClickListener {
                // likeDisLikeListener.getItemDetailsClick(data.id,adapterPosition)
                if (comingFrom != 0) {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(AppConstants.ITEM_ID, data.id)
                    intent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(AppConstants.ITEM_ID, data.id)
                    intent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                    //  if (context is HomeActivity) {
                    context.startActivityForResult(intent, 3)
                    //}else if (con)
                }

            }
            tvLikesHome.setOnClickListener {
                //    likeDisLikeListener.getLikeDisLikeClick(data.is_like,adapterPosition,data.like_id,data.id)
                if (!data.isClicked) {
                    if (data.like_id > 0) {
                        data.isClicked = !data.isClicked
                        listener.onClick(adapterPosition, data.like_id)
//                        deleteLikeApi(adapterPosition, data.like_id)
                    } else {
                        data.isClicked = !data.isClicked
                        lickedApi(data.id, adapterPosition)
                    }
                }
            }
            tvPostOn.text = StringBuilder().append(Utils.getDateDifference(data.created_at!!.date)).append(" ").append("ago")
            llUserProfile.setOnClickListener {
                if (Utils.getPreferencesString(context, AppConstants.USER_ID).toInt() != data.user_id) {
                    val reportIntent = Intent(context, UserProfileActivity::class.java)
                    reportIntent.putExtra(AppConstants.USER_ID, data.user_id)
                    reportIntent.putExtra(AppConstants.USER_NAME, data.username)
                    context.startActivity(reportIntent)
                }
            }
        }

    }

    private fun lickedApi(itemId: Int, position: Int) {
        val lickedRequest = LickedRequest(Utils.getPreferencesString(context, AppConstants.USER_ID), itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        listFavourites[position].isClicked = !listFavourites[position].isClicked
                        val responseLike = response.body() as LikedResponse
                        listFavourites[position].is_like = !listFavourites[position].is_like
                        listFavourites[position].items_like_count = listFavourites[position].items_like_count + 1
                        listFavourites[position].like_id = responseLike.like_id
                        notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        listFavourites[position].isClicked = !listFavourites[position].isClicked
                        notifyItemChanged(position)
                        //      Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    private fun deleteLikeApi(position: Int, lickedId: Int) {
        ServiceHelper().disLikeService(lickedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        listFavourites[position].isClicked = !listFavourites[position].isClicked
                        val likeResponse = response.body() as DisLikeResponse
                        listFavourites[position].is_like = !listFavourites[position].is_like
                        listFavourites[position].items_like_count = listFavourites[position].items_like_count - 1
                        listFavourites[position].like_id = 0
                        notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        listFavourites[position].isClicked = !listFavourites[position].isClicked
                        notifyItemChanged(position)
                    }
                })
    }

    fun setData(itemsList: ArrayList<ItemHome>) {
        listFavourites = itemsList
    }
}