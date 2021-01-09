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
import com.verkoopapp.activity.*
import com.verkoopapp.fragment.ProfileFragment
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_row.*
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.my_profile_details_row.*
import retrofit2.Response


class ProfileAdapter(private val context: Context, private val screenWidth: Int, private val profileFragment: ProfileFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemsList = ArrayList<ItemHome>()
    val PROFILE_DETAILS = 0
    val ITEMS_ROW = 1
    val SHOW_LOADER = 2/*for future use*/
    private var width = 0
    private var dataProfile: DataProfile? = null


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
                view = mLayoutInflater.inflate(R.layout.my_profile_details_row, parent, false)
                ProfileDetailsHolder(view)
            }
            SHOW_LOADER -> {
                view = mLayoutInflater.inflate(R.layout.show_loader_row, parent, false)
                ShowLoaderHolder(view)
            }
            else -> {

                view = mLayoutInflater.inflate(R.layout.item_row, parent, false)
                val params = view.layoutParams
                params.width = screenWidth / 2
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
            (holder as ProfileDetailsHolder).bind(dataProfile)
        } else {
            val modal = itemsList[position - 1]
            (holder as ItemsHolder).bind(modal)
        }
    }


    inner class ShowLoaderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind() {

        }

    }

    inner class ProfileDetailsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: DataProfile?) {
            if (data != null) {
                tvName.text = data.username
                tvFollowers.text = data.follower_count.toString()
                tvFollowing.text = data.follow_count.toString()
                tvGood.text = data.good.toString()
                tvAverage.text = data.average.toString()
                tvbad.text = data.sad.toString()
                tvJoiningDate.text = StringBuffer().append(": ").append(Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.created_at, "dd MMMM yyyy"))
                if (!TextUtils.isEmpty(data.profile_pic)) {
                    Picasso.with(context).load(AppConstants.IMAGE_URL + data.profile_pic)
                            .resize(720, 720)
                            .centerInside()
                            .error(R.mipmap.pic_placeholder)
                            .placeholder(R.mipmap.pic_placeholder)
                            .into(ivProfilePic)
                }
                if (!TextUtils.isEmpty(data.city) && !TextUtils.isEmpty(data.state)) {
                    tvAddress.text = StringBuilder().append(data.state).append(", ").append(data.city)
                    tvAddress.visibility = View.VISIBLE
                } else {
                    tvAddress.visibility = View.GONE
                }
                tvCountry.text = data.country
            }
            llFollowers.setOnClickListener {
                llFollowers.isEnabled = false
                Handler().postDelayed(Runnable {
                    llFollowers.isEnabled = true
                }, 700)
                val intent = Intent(context, FollowFollowingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 0)
                intent.putExtra(AppConstants.USER_ID, Utils.getPreferencesString(context, AppConstants.USER_ID).toInt())
                context.startActivity(intent)
            }
            llFollowing.setOnClickListener {
                llFollowing.isEnabled = false
                Handler().postDelayed(Runnable {
                    llFollowing.isEnabled = true
                }, 700)
                val intent = Intent(context, FollowFollowingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 1)
                intent.putExtra(AppConstants.USER_ID, Utils.getPreferencesString(context, AppConstants.USER_ID).toInt())
                context.startActivity(intent)
            }
            llFavourite.setOnClickListener {
                llFavourite.isEnabled = false
                Handler().postDelayed(Runnable {
                    llFavourite.isEnabled = true
                }, 700)
                val intent = Intent(context, FavouritesActivity::class.java)
                context.startActivity(intent)
            }
            ivSetting.setOnClickListener {
                ivSetting.isEnabled = false
                Handler().postDelayed(Runnable {
                    ivSetting.isEnabled = true
                }, 700)
                val intent = Intent(context, SettingActivity::class.java)
                context.startActivity(intent)
            }
            llCoins.setOnClickListener {
                llCoins.isEnabled = false
                val intent = Intent(context, CoinsActivity::class.java)
                context.startActivity(intent)
                Handler().postDelayed(Runnable {
                    llCoins.isEnabled = true
                }, 1000)
            }
            ivScanner.setOnClickListener {
                ivScanner.isEnabled = false
                val intent = Intent(context, QRScannerActivity::class.java)
                context.startActivity(intent)
                Handler().postDelayed(Runnable {
                    ivScanner.isEnabled = true
                }, 1000)
            }
            llWallet.setOnClickListener {
                llWallet.isEnabled = false
                val intent = Intent(context, MyWalletstripeActivity::class.java)
                context.startActivity(intent)
                Handler().postDelayed(Runnable {
                    llWallet.isEnabled = true
                }, 1000)
            }
            llGood.setOnClickListener {
                llGood.isEnabled = false
                Handler().postDelayed(Runnable {
                    llGood.isEnabled = true
                }, 700)
                val intent = Intent(context, RatingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 1)/*good*/
                intent.putExtra(AppConstants.USER_ID, Utils.getPreferencesString(context, AppConstants.USER_ID).toInt())
                context.startActivity(intent)
            }
            llBad.setOnClickListener {
                llBad.isEnabled = false
                Handler().postDelayed(Runnable {
                    llBad.isEnabled = true
                }, 700)
                val intent = Intent(context, RatingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 2)/*bad*/
                intent.putExtra(AppConstants.USER_ID, Utils.getPreferencesString(context, AppConstants.USER_ID).toInt())
                context.startActivity(intent)
            }
            llPoor.setOnClickListener {
                llPoor.isEnabled = false
                Handler().postDelayed(Runnable {
                    llPoor.isEnabled = true
                }, 700)
                val intent = Intent(context, RatingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 3)/*poor*/
                intent.putExtra(AppConstants.USER_ID, Utils.getPreferencesString(context, AppConstants.USER_ID).toInt())
                context.startActivity(intent)
            }
            llShare.setOnClickListener {
                llShare.isEnabled = false
                Handler().postDelayed(Runnable {
                    llShare.isEnabled = true
                }, 700)
                sharedDetails(data!!.username, data.profile_pic)

                /*   val sharingIntent = Intent(Intent.ACTION_SEND)
                   sharingIntent.type = "text/html"
                   sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Verkoop")
                   sharingIntent.putExtra(Intent.EXTRA_TEXT, "Verkoop")
                   context.startActivity(Intent.createChooser(sharingIntent, "Share using"))*/
            }

        }
    }

    private fun sharedDetails(userName: String, profilePic: String) {
        val buo = BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle(userName)
                .setContentImageUrl(AppConstants.IMAGE_URL + profilePic)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(ContentMetadata()
                        .addCustomMetadata("user_id", Utils.getPreferencesString(context, AppConstants.USER_ID))
                        .addCustomMetadata("type", 2.toString()))
        val lp = LinkProperties()
                .setChannel("sms")
                .setFeature("sharing")

        buo.generateShortUrl(context, lp) { url, error ->
            if (error == null) {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/html"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Verkoop")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, url)
                context.startActivity(Intent.createChooser(sharingIntent, "Share using"))
            }

        }
    }

    inner class ItemsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: ItemHome) {
            if (data.type == 0) {
                ll_condition.visibility = View.VISIBLE
            } else {
                ll_condition.visibility = View.GONE
            }
            llUserProfile.visibility = View.GONE
            ivProductImageHome.layoutParams.height = width - 16
            if (data.is_sold == 1) {
                tvSoldFav.visibility = View.VISIBLE
            } else {
                tvSoldFav.visibility = View.GONE
            }
//            if (data.is_like) {
                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_colored, 0, 0, 0)
//            } else {
//                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_grey, 0, 0, 0)
//            }

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

//                if (!data.isClicked) {
//                    if (data.like_id > 0) {
//                        data.isClicked = !data.isClicked
//                        deleteLikeApi(adapterPosition - 1, data.like_id)
//                    } else {
//                        data.isClicked = !data.isClicked
//                        lickedApi(data.id, adapterPosition - 1)
//                    }
//                }
            }
            tvProductHome.text = data.name
            tvItemPriceHome.text = Utils.convertCurrency(context, data.currency!!, data.price)
            itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.ITEM_ID, data.id)
                intent.putExtra(AppConstants.COMING_FROM, 1)
                intent.putExtra(AppConstants.COMING_TYPE, 1)
                intent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                profileFragment.startActivityForResult(intent, 3)
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

    fun setData(dataList: ArrayList<ItemHome>) {
        itemsList = dataList
    }

    fun profileDetail(data: DataProfile) {
        dataProfile = data
    }

}