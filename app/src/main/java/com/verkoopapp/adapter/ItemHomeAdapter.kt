package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.ProductDetailsActivity
import com.verkoopapp.activity.UserProfileActivity
import com.verkoopapp.fragment.HomeFragment
import com.verkoopapp.models.ItemHome
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_row.*


class ItemHomeAdapter(private val context: Context, private val rvItemList: RecyclerView, private val homeFragment: HomeFragment) : RecyclerView.Adapter<ItemHomeAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemsList = ArrayList<ItemHome>()
    private var width = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_row, parent, false)
        val params = view.layoutParams
        params.width = rvItemList.width / 2
        width = params.width
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = itemsList[position]
        holder.bind(data, position)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(data: ItemHome, position: Int) {
            ivProductImageHome.layoutParams.height = width - 16
            tvNameHome.text = data.username
//            if (position % 2 == 0) {
//                llSideDividerHome.visibility = View.VISIBLE
//            } else {
//                llSideDividerHome.visibility = View.GONE
//            }
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
     //           likeDisLikeListener.getLikeDisLikeClick(data.is_like, adapterPosition, data.like_id, data.id)
            }
            tvProductHome.text = data.name
            tvItemPriceHome.text = "$" + data.price
            itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.ITEM_ID, data.id)
                context.startActivity(intent)
            }
            tvPostOn.text = StringBuilder().append(Utils.getDateDifference(data.created_at!!.date)).append(" ").append("ago")

            llUserProfile.setOnClickListener {
                val reportIntent = Intent(context, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID,data.user_id)
                reportIntent.putExtra(AppConstants.USER_NAME,data.username)
                context.startActivity(reportIntent)
            }
        }

    }

    fun setData(items: ArrayList<ItemHome>) {
        itemsList = items
    }
}