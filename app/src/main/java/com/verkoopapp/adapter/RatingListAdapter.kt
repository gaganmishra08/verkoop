package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.ProductDetailsActivity
import com.verkoopapp.activity.UserProfileActivity
import com.verkoopapp.models.DataMyRating
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.rating_row.*
import java.util.ArrayList


class RatingListAdapter(private val context:Context):RecyclerView.Adapter<RatingListAdapter.ViewHolder>(){
    private val layoutInflater:LayoutInflater= LayoutInflater.from(context)
    private var ratingList= ArrayList<DataMyRating>()
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int):ViewHolder {
        val view =layoutInflater.inflate(R.layout.rating_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ratingList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val data=ratingList[position]
        holder.bind(data)
    }

inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{
    fun bind(data: DataMyRating) {
        tvRateUserName.text = data.userName
        tvProductNameRate.text = data.name
        tvLastMssgRate.text=data.rating.toString()
        rbRatingInbox.rating=data.rating.toFloat()
        tvRateTime.text =Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.created_at, "MMMM dd, yyyy")
        if (!TextUtils.isEmpty(data.profile_pic)) {
            Picasso.with(context).load(AppConstants.IMAGE_URL + data.profile_pic)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfilePicRate)
        }
        if (!TextUtils.isEmpty(data.url)) {
            Picasso.with(context).load(AppConstants.IMAGE_URL + data.url)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.post_placeholder)
                    .placeholder(R.mipmap.post_placeholder)
                    .into(ivImageRate)
        }
        llUserProfile.setOnClickListener {
            Handler().postDelayed(Runnable {
                if(data.user_id!= Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
                    val reportIntent = Intent(context, UserProfileActivity::class.java)
                    reportIntent.putExtra(AppConstants.USER_ID, data.user_id)
                    reportIntent.putExtra(AppConstants.USER_NAME, data.userName)
                    context.startActivity(reportIntent)
                }
            },100)
        }
        flItemImage.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra(AppConstants.ITEM_ID, data.item_id)
            context.startActivity(intent)
        }

    }
}

    fun setData(data: ArrayList<DataMyRating>) {
        ratingList=data
    }
}