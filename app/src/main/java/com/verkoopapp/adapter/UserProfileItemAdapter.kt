package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.FollowFollowingActivity
import com.verkoopapp.activity.ProductDetailsActivity
import com.verkoopapp.activity.RatingActivity
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_row.*
import kotlinx.android.synthetic.main.my_profile_details_row.*
import kotlinx.android.synthetic.main.user_profile_detail_row.*
import retrofit2.Response


class UserProfileItemAdapter(private val context: Context, private val llProfileParent: Int, private val userId: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemsList = ArrayList<ItemHome>()
    val PROFILE_DETAILS = 0
    val ITEMS_ROW = 1
    val SHOW_LOADER = 2/*for future use*/
    private var width = 0
    private var profileData: DataUserProfile? = null
    private var isFollowClick: Boolean = false


    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return PROFILE_DETAILS
        } else {
            return ITEMS_ROW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            PROFILE_DETAILS -> {
                view = mLayoutInflater.inflate(R.layout.user_profile_detail_row, parent, false)
                UserProfileHolder(view)
            }
            SHOW_LOADER -> {
                view = mLayoutInflater.inflate(R.layout.show_loader_row, parent, false)
                ShowLoaderHolder(view)
            }
            else -> {
                view = mLayoutInflater.inflate(R.layout.item_row, parent, false)
                val params = view.layoutParams
                params.width = llProfileParent / 2
                width = params.width
                ItemsHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == PROFILE_DETAILS) {
            (holder as UserProfileHolder).bind(profileData)
        } else {
            val modal = itemsList[position - 1]
            (holder as ItemsHolder).bind(modal)
        }
    }

    fun setData(data: ArrayList<ItemHome>) {
        itemsList = data
    }

    fun setProfileData(data: DataUserProfile) {
        profileData = data
    }

    inner class ShowLoaderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind() {

        }

    }

    inner class UserProfileHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: DataUserProfile?) {
            if (data != null) {
                tvUserGood.text = data.good.toString()
                tvUserNorma.text = data.average.toString()
                tvUserSad.text = data.sad.toString()
                tvNameUser.text = data.username
                tvUserFollowers.text = data.follower_count.toString()
                tvUserFollowing.text = data.follow_count.toString()
                if (data.follower_id > 0) {
                    tvUserFollow.background = ContextCompat.getDrawable(context, R.drawable.brown_rectangular_shape)
                    tvUserFollow.text = context.getString(R.string.following)
                    tvUserFollow.setPaddingRelative(Utils.dpToPx(context, 48.0f).toInt(), Utils.dpToPx(context, 10.0f).toInt(), Utils.dpToPx(context, 48.0f).toInt(), Utils.dpToPx(context, 10.0f).toInt())
                } else {
                    tvUserFollow.background = ContextCompat.getDrawable(context, R.drawable.red_rectangular_shape)
                    tvUserFollow.text = context.getString(R.string.follow)
                    tvUserFollow.setPaddingRelative(Utils.dpToPx(context, 60.0f).toInt(), Utils.dpToPx(context, 10.0f).toInt(), Utils.dpToPx(context, 60.0f).toInt(), Utils.dpToPx(context, 10.0f).toInt())
                }
                tvUserJoiningDate.text = StringBuffer().append(": ").append(Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.created_at, "dd MMMM yyyy"))
                if (!TextUtils.isEmpty(data.profile_pic)) {
                    Picasso.with(context).load(AppConstants.IMAGE_URL + data.profile_pic)
                            .resize(720, 720)
                            .centerInside()
                            .error(R.mipmap.pic_placeholder)
                            .placeholder(R.mipmap.pic_placeholder)
                            .into(ivUserPic)
                }
                if (!TextUtils.isEmpty(data.city) && !TextUtils.isEmpty(data.state)) {
                    tvAddressUser.text = StringBuilder().append(data.state).append(", ").append(data.city)

                    tvAddressUser.visibility = View.VISIBLE
                } else {
                    tvAddressUser.visibility = View.GONE
                }
                tvCountryUser.text = data.country
            }
            llFollowersUser.setOnClickListener {
                val intent = Intent(context, FollowFollowingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 0)
                intent.putExtra(AppConstants.USER_ID, userId)
                context.startActivity(intent)
            }
            llFollowingUser.setOnClickListener {
                val intent = Intent(context, FollowFollowingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 1)
                intent.putExtra(AppConstants.USER_ID, userId)
                context.startActivity(intent)
            }
            llGoodUser.setOnClickListener {
                val intent = Intent(context, RatingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 1)
                intent.putExtra(AppConstants.USER_ID,data!!.id)
                context.startActivity(intent)
            }
            llBadUser.setOnClickListener {
                val intent = Intent(context, RatingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 2)
                intent.putExtra(AppConstants.USER_ID,data!!.id)
                context.startActivity(intent)
            }
            llPoorUser.setOnClickListener {
                val intent = Intent(context, RatingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 3)
                intent.putExtra(AppConstants.USER_ID,data!!.id)
                context.startActivity(intent)
            }

            tvUserFollow.setOnClickListener {
                if (!isFollowClick) {
                    isFollowClick = true
                    if (tvUserFollow.text.toString().equals("Follow", ignoreCase = true)) {
                        followService()
                    } else {
                        unFollowService(data!!.follower_id)
                    }
                }

            }
        }

    }

    private fun unFollowService(follower_id: Int) {
        ServiceHelper().unFollowService(follower_id,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isFollowClick = false
                        val likeResponse = response.body() as DisLikeResponse
                        profileData!!.follower_id = 0
                        profileData!!.follower_count -= 1
                        notifyItemChanged(0)
                    }

                    override fun onFailure(msg: String?) {
                        isFollowClick = false
                        Utils.showSimpleMessage(context, msg!!).show()
                    }
                })
    }

    private fun followService() {
        //  pbProgressUser.visibility=View.VISIBLE
        val reportUserResponse = FollowRequest(Utils.getPreferencesString(context, AppConstants.USER_ID), userId)
        ServiceHelper().followService(reportUserResponse,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isFollowClick = false
                        val response = response.body() as FollowResponse
                        profileData!!.follower_id = response.data.id
                        profileData!!.follower_count += 1
                        notifyItemChanged(0)
                    }

                    override fun onFailure(msg: String?) {
                        isFollowClick = false
                        //    pbProgressReport.visibility=View.GONE
                        //    Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })

    }


    inner class ItemsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: ItemHome) {
            llUserProfile.visibility = View.GONE
            ivProductImageHome.layoutParams.height = width - 16
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
                intent.putExtra(AppConstants.USER_ID, userId)
                intent.putExtra(AppConstants.COMING_FROM, 1)
                intent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                intent.putExtra(AppConstants.COMING_FROM,"UserProfileAdapter")
                context.startActivity(intent)
            }

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
                        notifyItemChanged(position + 1)
                    }

                    override fun onFailure(msg: String?) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        notifyItemChanged(position + 1)
                    }
                })
    }
    /* inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{

      fun bind(data: ItemUserProfile, position: Int) {
          ivProductImage.layoutParams.height =width-16
          if(position %2==0){
              llSideDividerProfile.visibility= View.VISIBLE
          }else{
              llSideDividerProfile.visibility= View.GONE
          }
          if(data.is_like){
              tvLikesProfile.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_favorite_colored, 0, 0, 0)
          }else{
              tvLikesProfile.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_favorite_grey, 0, 0, 0)
          }
          tvLikesProfile.text=data.items_like_count.toString()
          if(data.item_type==1){
              tvConditionProfile.text="New"
          }else{
              tvConditionProfile.text=context.getString(R.string.used)
          }
          if(data.is_sold==1){
              tvSold.visibility=View.VISIBLE
          }else{
              tvSold.visibility=View.GONE
          }
          if(!TextUtils.isEmpty(data.image_url)) {
              Picasso.with(context).load(AppConstants.IMAGE_URL + data.image_url)
                      .resize(720, 720)
                      .centerCrop()
                      .error(R.mipmap.post_placeholder)
                      .placeholder(R.mipmap.post_placeholder)
                      .into(ivProductImage)

          }else{
              ivProductImage.setImageResource(R.mipmap.post_placeholder)
          }

          tvNameProfile.text=data.name
          tvItemPriceProfile.text="$"+data.price
          itemView.setOnClickListener {
              likeDisLikeListener.getItemDetailsClick(data.id,data.user_id)

          }
          tvLikesProfile.setOnClickListener {
              likeDisLikeListener.getLikeDisLikeClick(data.is_like,adapterPosition,data.like_id,data.id)
          }
      }
 }*/

    /*fun setData(data: ArrayList<ItemUserProfile>) {
        myItemsList=data
    }*/
}