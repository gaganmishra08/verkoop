package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.UserProfileActivity
import com.verkoopapp.models.DataUser
import com.verkoopapp.models.DisLikeResponse
import com.verkoopapp.models.FollowRequest
import com.verkoopapp.models.FollowResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user_search_row.*
import retrofit2.Response


class FollowFollowingAdapter(private val context: Context,private val userId:Int,private val comingFrom:Int) : RecyclerView.Adapter<FollowFollowingAdapter.ViewHolder>(), Filterable {
    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var searchByUserList = ArrayList<DataUser>()
    private var mFilteredList = ArrayList<DataUser>()

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {
                    mFilteredList = searchByUserList
                } else {

                    val filteredList = ArrayList<DataUser>()

                    for (androidVersion in searchByUserList) {

                        if (androidVersion.username.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(androidVersion)
                        }
                    }

                    mFilteredList = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                mFilteredList = filterResults.values as ArrayList<DataUser>
                notifyDataSetChanged()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.user_search_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = mFilteredList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(modal: DataUser) {
            if(comingFrom==0){
                tvFollow.visibility=View.GONE
            }else{
                tvFollow.visibility=View.VISIBLE
            }
            tvSearchUserName.text = modal.username

            if (!TextUtils.isEmpty(modal.profile_pic)) {
                Picasso.with(context)
                        .load(AppConstants.IMAGE_URL + modal.profile_pic)
                        .resize(720, 720)
                        .centerCrop()
                        .error(R.mipmap.pic_placeholder)
                        .placeholder(R.mipmap.pic_placeholder)
                        .into(ivSearchUserPic)

            } else {
                ivSearchUserPic.setImageResource(R.mipmap.pic_placeholder)
            }
            if (modal.follower_id > 0) {
                tvFollow.text = context.getString(R.string.following)
                tvFollow.setTextColor(ContextCompat.getColor(context, R.color.gray_light))
            } else {
                tvFollow.text = context.getString(R.string.follow)
                tvFollow.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            if(Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()==modal.id) {
                tvFollow.visibility=View.GONE
            } else{
                tvFollow.visibility=View.VISIBLE
            }
            tvFollow.setOnClickListener {
                if(Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()==userId) {
                    if (!modal.isClicked) {
                        if (modal.follower_id > 0) {
                            modal.isClicked = !modal.isClicked
                            callUnFollowApi(modal.follower_id, adapterPosition)
                        } else {
                            modal.isClicked = !modal.isClicked
                            callFollowApi(modal.id, adapterPosition)
                        }
                    }
                }
            }
            llUserProfile.setOnClickListener {
                if(modal.id !=Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
                    val reportIntent = Intent(context, UserProfileActivity::class.java)
                    reportIntent.putExtra(AppConstants.USER_ID, modal.id)
                    reportIntent.putExtra(AppConstants.USER_NAME, modal.username)
                    context.startActivity(reportIntent)
                }
            }
        }
    }

    fun setData(userList: ArrayList<DataUser>) {
        searchByUserList = userList
       mFilteredList = userList
    }

    private fun callUnFollowApi(follower_id: Int, adapterPosition: Int) {
        ServiceHelper().unFollowService(follower_id,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val likeResponse = response.body() as DisLikeResponse
                        mFilteredList[adapterPosition].follower_id = 0
                        mFilteredList[adapterPosition].isClicked = false
                        notifyDataSetChanged()
                    }

                    override fun onFailure(msg: String?) {
                        mFilteredList[adapterPosition].isClicked = false
                        notifyDataSetChanged()
                    }
                })
    }

    private fun callFollowApi(id: Int,adapterPosition:Int) {
        val reportUserResponse = FollowRequest(Utils.getPreferencesString(context, AppConstants.USER_ID), id)
        ServiceHelper().followService(reportUserResponse,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val followResponse = response.body() as FollowResponse
                        mFilteredList[adapterPosition].follower_id = followResponse.data.id
                        mFilteredList[adapterPosition].isClicked = false
                        notifyDataSetChanged()
                    }

                    override fun onFailure(msg: String?) {
                        mFilteredList[adapterPosition].isClicked = false
                        notifyDataSetChanged()
                    }
                })

    }

}