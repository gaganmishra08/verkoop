package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Toast
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import io.socket.client.Ack
import io.socket.client.Socket
import com.google.gson.Gson
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.adapter.CommentListAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.offlinechatdata.DbHelper
import com.verkoopapp.utils.*
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.comment_dialog_activity.*
import kotlinx.android.synthetic.main.item_details_activity.*
import kotlinx.android.synthetic.main.item_details_activity.tvAddress
import kotlinx.android.synthetic.main.item_details_activity.etComment
import kotlinx.android.synthetic.main.item_details_activity.tvPost
import kotlinx.android.synthetic.main.my_profile_details_row.*
import kotlinx.android.synthetic.main.toolbar_product_details.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*

class ProductDetailsActivity : AppCompatActivity() {
    private val socket: Socket? = VerkoopApplication.getAppSocket()
    private var imageURLLIst = ArrayList<String>()
    private val commentsList = ArrayList<CommentModal>()
    private val reportList = ArrayList<ReportResponse>()
    private val menuList = ArrayList<PowerMenuItem>()
    private var dataComment: CommentModal? = null
    private var dataIntent: DataItems? = null
    private var powerMenu: PowerMenu? = null
    private var itemId: Int = 0
    private var userId: Int = 0
    private var isSoldItem: Int = 0
    private var adapterPosition: Int = 0
    private var comingType: Int = 0
    private var screenHeight: Int = 0
    private var categoryType: Int = 0
    private var userName: String = ""
    private var profilePic: String = ""
    private var price: Double = 0.0
    private var productName: String = ""
    private var productDescription: String = ""
    private var productImage: String = ""
    private var dbHelper: DbHelper? = null
    private var dataResponse: DataItems? = null
    private var shareDialogFacebook: ShareDialog? = null
    var isClicked: Boolean = false
    var isLiked: Boolean = false
    var likeCount: Int = 0
    var likeInitialCount: Int = 0
    var likeId: Int = 0
    var comingFrom: String = ""

    private lateinit var commentListAdapter: CommentListAdapter
    private lateinit var shareDialog: DeleteCommentDialog
    private var createOfferDialog: CreatOfferDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_details_activity)
        shareDialogFacebook = ShareDialog(this)
        initKeyBoardListener()
        dbHelper = DbHelper()
        llBuying.visibility = View.GONE
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        mDemoSliderDetails.minimumHeight = (screenHeight / 2) + 100
        comingType = intent.getIntExtra(AppConstants.COMING_TYPE, 0)
        if (intent.getStringExtra(AppConstants.COMING_FROM) != null) {
            comingFrom = intent.getStringExtra(AppConstants.COMING_FROM)!!
        }
        setCommentAdapter()
        adapterPosition = intent.getIntExtra(AppConstants.ADAPTER_POSITION, 0)
        if (intent.getIntExtra(AppConstants.COMING_FROM, 0) == 1) {
            llChat.visibility = View.GONE
            ivRightProduct.visibility = View.VISIBLE
        }
        if (Utils.isOnline(this)) {
            getItemDetailsService(intent.getIntExtra(AppConstants.ITEM_ID, 0))
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
        setClickData()

        /* scrollView1.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
             if (scrollY > oldScrollY) {
                val animation =  TranslateAnimation(0f,0f,0f,1000f)
                 animation.duration = 1000
                 sellCardItem.startAnimation(animation)
                 sellCardItem.visibility = View.GONE

             } else {
                 val animation =  TranslateAnimation(0f,0f,1000f,0f)
                 animation.duration = 500
                 sellCardItem.startAnimation(animation)
                 sellCardItem.visibility = View.VISIBLE
             }
         })*/
    }

    private fun setClickData() {
        ivWhatAppShare.setOnClickListener {
            ivWhatAppShare.isEnabled = false
            Handler().postDelayed(Runnable {
                ivWhatAppShare.isEnabled = true
            }, 700)
            val installed = Utils.appInstalledOrNot(this, "com.whatsapp")
            if (installed) {
                sharedDetails(2)/*WhatsApp Share*/
            } else {
                Utils.showSimpleMessage(this, getString(R.string.not_installed)).show()

            }
        }
        tvFacebookShare.setOnClickListener {
            tvFacebookShare.isEnabled = false
            Handler().postDelayed(Runnable {
                tvFacebookShare.isEnabled = true
            }, 1000)
            sharedDetails(1)/*facebook Share*/
        }
        llChat.setOnClickListener {
            llChat.isEnabled = false
            Handler().postDelayed(Runnable {
                llChat.isEnabled = true
            }, 1000)
            if (userId != Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(AppConstants.USER_ID, userId)
                intent.putExtra(AppConstants.USER_NAME, userName)
                intent.putExtra(AppConstants.ITEM_ID, itemId)
                intent.putExtra(AppConstants.PROFILE_URL, profilePic)
                intent.putExtra(AppConstants.PRODUCT_URL, productImage)
                intent.putExtra(AppConstants.PRODUCT_PRICE, price)
                //intent.putExtra(AppConstants.OFFERED_PRICE, Offerprice)
                intent.putExtra(AppConstants.IS_SOLD, isSoldItem)
                intent.putExtra(AppConstants.PRODUCT_NAME, productName)
                startActivity(intent)
            } else {
                val intent = Intent(this, ChatInboxActivity::class.java)
                intent.putExtra(AppConstants.ITEM_ID, itemId)
                startActivity(intent)
            }
        }
        tvShare.setOnClickListener {
            tvShare.isEnabled = false
            Handler().postDelayed(Runnable {
                tvShare.isEnabled = true
            }, 1000)
            sharedDetails(0)/*open Share*/
        }
    }

    private fun setCommentAdapter() {
        val mManager = LinearLayoutManager(this)
        rvPostCommentList.layoutManager = mManager
        commentListAdapter = CommentListAdapter(this, pbProgressProduct, comingType)
        rvPostCommentList.adapter = commentListAdapter
    }

    private fun setData(data: DataItems) {
        dataResponse = data
        itemId = data.id
        userId = data.user_id
        userName = data.username
        isSoldItem = data.is_sold
        if (data.meet_up == 1 && data.latitude != 0.0 && data.longitude != 0.0) {
            tvAddress.text = data.address
            llMeetUp.visibility = View.VISIBLE
            viewMeetUp.visibility = View.VISIBLE
        } else {
            llMeetUp.visibility = View.GONE
            viewMeetUp.visibility = View.GONE
        }
        if (data.is_sold == 1 || data.user_id == Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
            llBuying.visibility = View.GONE
            llChat.visibility = View.VISIBLE
        } else {
            llBuying.visibility = View.VISIBLE
            llChat.visibility = View.VISIBLE
        }

        llChatDetails.setOnClickListener {
            if (Utils.getPreferencesString(this, AppConstants.USER_ID).toInt() != data.user_id) {
                val reportIntent = Intent(this, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID, data.user_id)
                reportIntent.putExtra(AppConstants.USER_NAME, data.username)
                startActivity(reportIntent)
            }
        }
        tvAddress.setOnClickListener {
            if (data.latitude != 0.0 && data.longitude != 0.0) {
                val geoUri = "http://maps.google.com/maps?q=loc:" + data.latitude + "," + data.longitude + "(" + data.address + ")"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                startActivity(intent)
            }
        }
        if (!TextUtils.isEmpty(data.profile_pic)) {
            profilePic = data.profile_pic
            Picasso.with(this@ProductDetailsActivity).load(AppConstants.IMAGE_URL + data.profile_pic)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        } else {
            Picasso.with(this@ProductDetailsActivity).load(R.mipmap.pic_placeholder)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        }
        ivRightProduct.setImageResource(R.drawable.menu_icon_black)

        ivRightProduct.setOnClickListener {
            if (userId != Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
                menuList.clear()
                val powerMenu1 = PowerMenuItem(getString(R.string.report_listing), false)
                val powerMenu2 = PowerMenuItem(getString(R.string.share), false)
                menuList.add(powerMenu1)
                menuList.add(powerMenu2)
                openPowerMenu(menuList)
            } else {
                menuList.clear()
                val powerMenu = PowerMenuItem(getString(R.string.share), false)
                val powerMenu1 = PowerMenuItem(getString(R.string.edit_item), false)
                val powerMenu2 = PowerMenuItem(getString(R.string.mark_sold), false)
                val powerMenu3 = PowerMenuItem(getString(R.string.delete_listing), false)
                if (isSoldItem != 1) {
                    menuList.add(powerMenu)
                    menuList.add(powerMenu1)
                    menuList.add(powerMenu2)
                    menuList.add(powerMenu3)
                    openPowerMenu(menuList)
                } else {
                    menuList.add(powerMenu)
                    menuList.add(powerMenu3)
                    openPowerMenu(menuList)
                }

            }

        }
        tvPost.setOnClickListener {
            if (Utils.isOnline(this)) {
                if (!TextUtils.isEmpty(etComment.text.toString())) {
                    KeyboardUtil.hideKeyboard(this)
                    callPostCommentApi()
                } else {
                    Utils.showSimpleMessage(this, "Please enter Comment.").show()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }



//        tvPostComment.setOnClickListener {
//            if (data.id != 0) {
//                val i = Intent(this, AddCommentActivity::class.java)
//                i.putExtra(AppConstants.ITEM_ID, data.id)
//                startActivityForResult(i, 1)
//            }
//        }
        ivLeft.setOnClickListener { onBackPressed() }
//        custom_indicator_detail.setDefaultIndicatorColor(ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.light_gray))
        custom_indicator_detail.setIndicatorStyleResource(R.mipmap.ic_red_oval,R.mipmap.ic_gray_dot)
        mDemoSliderDetails.setCustomIndicator(custom_indicator_detail)
        for (i in data.items_image.indices) {
            val textSliderView = DefaultSliderView(this)
            // initialize a SliderLayout
            textSliderView.image(AppConstants.IMAGE_URL + data.items_image[i].url).setOnSliderClickListener { slider ->
            }.scaleType = BaseSliderView.ScaleType.CenterCrop
            mDemoSliderDetails.addSlider(textSliderView)
        }
        mDemoSliderDetails.stopAutoCycle()
        tvProductName.text = data.name
        data.price = Utils.convertCurrencyWithoutSymbol(this@ProductDetailsActivity,data.currency,data.price)
        data.offer_price = Utils.convertCurrencyWithoutSymbol(this@ProductDetailsActivity,data.currency,data.offer_price)
        data.additional_info!!.min_price = Utils.convertCurrencyWithoutSymbol(this@ProductDetailsActivity,data.currency,data.additional_info!!.min_price)
        data.additional_info!!.max_price = Utils.convertCurrencyWithoutSymbol(this@ProductDetailsActivity,data.currency,data.additional_info!!.max_price)
        price = data.price
        productName = data.name
        productDescription = data.description
        if (data.items_image.isNotEmpty()) {
            productImage = data.items_image[0].url
        }
        if (data.is_like) {
            isLiked = true
            tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_colored, 0, 0, 0)
        } else {
            isLiked = false
            tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_grey, 0, 0, 0)
        }

        if (userId == Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
            tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_colored, 0, 0, 0)
        }

        tvLikes.text = data.items_like_count.toString()
        likeInitialCount = data.items_like_count
        likeCount = data.items_like_count
        likeId = data.like_id
        tvLikes.setOnClickListener {
            if (userId != Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
                tvLikes.isEnabled = false
                if (!isClicked) {
                    if (likeId > 0) {
                        isClicked = !isClicked
                        deleteLikeApi(adapterPosition - 1, likeId)
                    } else {
                        isClicked = !isClicked
                        likedApi(data.id, adapterPosition - 1)
                    }
                }
                Handler().postDelayed(Runnable {
                    tvLikes.isEnabled = true
                }, 1000)
            }
        }
        tvPrice.text = StringBuilder().append(Utils.getPreferencesString(this@ProductDetailsActivity,AppConstants.CURRENCY_SYMBOL)).append(" ").append(data.price)
        tvDescription.text = data.description
        tvUserName.text = data.username
        tvDateDetails.text = StringBuilder().append(Utils.getDateDifferenceDiff(data.created_at)).append(" ").append("ago")
        tvDateTool.text = StringBuilder().append(Utils.getDateDifferenceDiff(data.created_at)).append(" ").append("ago")
        tvCategoryDetail.text = StringBuilder().append(": ").append(data.category_name)
        if (data.item_type == 1) {
            tvType.setImageDrawable(resources.getDrawable(R.drawable.ic_new_item))
        } else {
            tvType.setImageDrawable(resources.getDrawable(R.drawable.ic_used_item))
        }
        categoryType = data.type
        Log.e("actegoryType=", data.type.toString())
        if (data.type == 1) {
            tvType.visibility = View.GONE
            llCarDetails.visibility = View.VISIBLE
            CommonView.visibility = View.VISIBLE
            llPropertyDetails.visibility = View.GONE
            if (data.additional_info != null) {
                tvPrice.text = StringBuilder().append(": ").append(Utils.getPreferencesString(this@ProductDetailsActivity,AppConstants.CURRENCY_SYMBOL)).append(data.additional_info!!.min_price).append(" - ").append(Utils.getPreferencesString(this@ProductDetailsActivity,AppConstants.CURRENCY_SYMBOL)).append(data.additional_info!!.max_price)
                tvRegistrationYear.text = StringBuilder().append(": ").append(data.additional_info!!.from_year.toString()).append(" - ").append(data.additional_info!!.to_year.toString())
                tvCarBrand.text = StringBuilder().append(": ").append(data.additional_info!!.brand_name)
                tvCarType.text = StringBuilder().append(": ").append(data.additional_info!!.car_type)
                tvCarLocation.text = StringBuilder().append(": ").append(data.additional_info!!.location)
                if (!TextUtils.isEmpty(data.additional_info!!.model_name)) {
                    tvCarModal.text = StringBuilder().append(": ").append(data.additional_info!!.model_name)
                }
                if (data.additional_info!!.direct_owner == 1) {
                    tvDirectOwner.text = StringBuilder().append(": ").append("Private")
                } else {
                    tvDirectOwner.text = StringBuilder().append(": ").append("Dealership")
                }
                if (data.additional_info!!.transmission_type == 1) {
                    tvTransmission.text = StringBuilder().append(": ").append(getString(R.string.manual))
                } else {
                    tvTransmission.text = StringBuilder().append(": ").append(getString(R.string.automatic))
                }
            }

        } else if (data.type == 2 || data.type == 3) {
            tvPrice.text = StringBuilder().append(": ").append(Utils.getPreferencesString(this@ProductDetailsActivity,AppConstants.CURRENCY_SYMBOL)).append(data.additional_info!!.min_price).append(" - ").append(Utils.getPreferencesString(this@ProductDetailsActivity,AppConstants.CURRENCY_SYMBOL)).append(data.additional_info!!.max_price)
            tvType.visibility = View.GONE
            llPropertyDetails.visibility = View.VISIBLE
            CommonView.visibility = View.VISIBLE
            llCarDetails.visibility = View.GONE
            if (data.additional_info != null) {
                tvStreetName.text = StringBuilder().append(": ").append(data.additional_info!!.street_name)
                tvPostalCode.text = StringBuilder().append(": ").append(data.additional_info!!.postal_code)
                tvBedRoom.text = StringBuilder().append(": ").append(data.additional_info!!.bedroom)
                tvBathRoom.text = StringBuilder().append(": ").append(data.additional_info!!.bathroom)
                tvZone.text = StringBuilder().append(": ").append(data.additional_info!!.location)
                tvArea.text = StringBuilder().append(": ").append(data.additional_info!!.city)
                tvPropertyType.text = StringBuilder().append(": ").append(data.additional_info!!.property_type)
                if (data.additional_info!!.parking_type == 1) {
                    tvParking.text = StringBuilder().append(": ").append(getString(R.string.parking))
                } else {
                    tvParking.text = StringBuilder().append(": ").append(getString(R.string.garage))
                }
                if (data.type == 3) {
                    llFurnish.visibility = View.VISIBLE
                    if (data.additional_info!!.furnished == 1) {
                        tvFurnished.text = StringBuilder().append(": ").append(getString(R.string.yes))
                    } else {
                        tvFurnished.text = StringBuilder().append(": ").append(getString(R.string.no))
                    }

                } else {
                    llFurnish.visibility = View.GONE
                }
            }
        } else {
            tvType.visibility = View.VISIBLE
            llCarDetails.visibility = View.GONE
            llPropertyDetails.visibility = View.GONE
            CommonView.visibility = View.GONE

        }
        if (!data.make_offer) {
            tvBuying.text = getString(R.string.make_offer_)
        } else {
            tvBuying.text = getString(R.string.view_offer)
        }
        if (data.make_offer && data.user_id == Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
            tvAll.text = StringBuilder().append("View Chats").append("[").append(data.chat_count).append("]")
        } else if (data.make_offer && data.user_id != Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
            tvAll.text = StringBuilder().append("View Chats").append("[").append(data.message_count).append("]")
        }

        llBuying.setOnClickListener {
            if (data.type == 1 || data.type == 2 || data.type == 3) {
                if (!data.make_offer) {
                    makeOffer(1, 0, data.price, 0.0, data.additional_info!!.min_price)
                } else {
                    makeOffer(1, 1, data.price, data.offer_price, data.additional_info!!.min_price)
                }
            } else {
                if (!data.make_offer) {
                    makeOffer(0, 0, data.price, 0.0, 0.0)
                } else {
                    makeOffer(0, 1, data.price, data.offer_price, 0.0)
                }
            }
        }

    }


    private fun callPostCommentApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressProduct.visibility = View.VISIBLE
        ServiceHelper().postCommentService(PostCommentRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), itemId, etComment.text.toString()),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressProduct.visibility = View.GONE
                        val commentResponse = response.body() as CommentResponse
                        if (commentResponse.data != null) {
                            val intent = Intent(this@ProductDetailsActivity, ProductDetailsActivity::class.java)
                            intent.putExtra(AppConstants.ITEM_ID, itemId)
                            startActivity(intent)
                            finish()
                        } else {
                            Utils.showSimpleMessage(this@ProductDetailsActivity, commentResponse.message).show()
                        }

                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressProduct.visibility = View.GONE
                        Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                    }
                })

    }

    private fun likedApi(itemId: Int, position: Int) {
        val lickedRequest = LickedRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked = !isClicked
                        val responseLike = response.body() as LikedResponse
                        isLiked = !isLiked
                        likeCount = likeCount + 1
                        likeId = responseLike.like_id
                        setLikeData()
                        if (comingFrom != null && comingFrom.equals("YourDailyPicksAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLike(adapterPosition,"YourDailyPicksAdapter"))
                        }
                        if (comingFrom != null && comingFrom.equals("RecommendedForYou")) {
                            EventBus.getDefault().post(MessageEventOnLike(adapterPosition,"RecommendedForYou"))
                        }
                        if (comingFrom != null && comingFrom.equals("CategoryDetailsActivity")) {
                            EventBus.getDefault().post(MessageEventOnLikeCategory(adapterPosition,"CategoryDetailsActivity"))
                        }
                        if (comingFrom != null && comingFrom.equals("BuyCarsAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLikeBuyCars(adapterPosition,"BuyCarsAdapter"))
                        }
                        if (comingFrom != null && comingFrom.equals("BuyPropertyAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLikeBuyProperty(adapterPosition,"BuyPropertyAdapter"))
                        }
                        if (comingFrom != null && comingFrom.equals("UserProfileAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLikeUserProfile(adapterPosition,"UserProfileAdapter"))
                        }
                        if (comingFrom != null && comingFrom.equals("AdvertisementAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLikeAdvertisementAdapter(adapterPosition,"AdvertisementAdapter"))
                        }
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = !isClicked
                        //      Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    private fun setLikeData() {
        if (isLiked == true) {
            tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_colored, 0, 0, 0)
        } else {
            tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_grey, 0, 0, 0)
        }
        tvLikes.text = likeCount.toString()


    }

    private fun deleteLikeApi(position: Int, lickedId: Int) {
        ServiceHelper().disLikeService(lickedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked = !isClicked
                        val likeResponse = response.body() as DisLikeResponse
                        isLiked = !isLiked
                        likeCount = likeCount - 1
                        likeId = 0
                        setLikeData()
                        if (comingFrom != null && comingFrom.equals("YourDailyPicksAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLike(adapterPosition,"YourDailyPicksAdapter"))
                        }
                        if (comingFrom != null && comingFrom.equals("RecommendedForYou")) {
                            EventBus.getDefault().post(MessageEventOnLike(adapterPosition,"RecommendedForYou"))
                        }
                        if (comingFrom != null && comingFrom.equals("CategoryDetailsActivity")) {
                            EventBus.getDefault().post(MessageEventOnLikeCategory(adapterPosition,"CategoryDetailsActivity"))
                        }
                        if (comingFrom != null && comingFrom.equals("BuyCarsAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLikeBuyCars(adapterPosition,"BuyCarsAdapter"))
                        }
                        if (comingFrom != null && comingFrom.equals("BuyPropertyAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLikeBuyProperty(adapterPosition,"BuyPropertyAdapter"))
                        }
                        if (comingFrom != null && comingFrom.equals("UserProfileAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLikeUserProfile(adapterPosition,"UserProfileAdapter"))
                        }
                        if (comingFrom != null && comingFrom.equals("AdvertisementAdapter")) {
                            EventBus.getDefault().post(MessageEventOnLikeAdvertisementAdapter(adapterPosition,"AdvertisementAdapter"))
                        }
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = !isClicked
                    }
                })
    }

    private fun openPowerMenu(menuList: ArrayList<PowerMenuItem>) {
        powerMenu = PowerMenu.Builder(this)
                .addItemList(menuList)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setMenuRadius(1f)
                .setMenuShadow(10f)
                .setTextColor(ContextCompat.getColor(this, R.color.black_dark))
                .setMenuColor(Color.WHITE)
                .setDivider(ContextCompat.getDrawable(this, R.drawable.horizontal_line))
                .setDividerHeight(1)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build()
        powerMenu!!.showAsAnchorRightTop(ivRightProduct)
    }

    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, item ->
        powerMenu!!.selectedPosition = position
        powerMenu!!.dismiss()
        if (item.title.equals(getString(R.string.report_listing), ignoreCase = true)) {
            val reportIntent = Intent(this, ReportUserActivity::class.java)
            reportIntent.putParcelableArrayListExtra(AppConstants.REPORT_LIST, reportList)
            reportIntent.putExtra(AppConstants.ITEM_ID, itemId)
            startActivity(reportIntent)
        } else if (item.title.equals(getString(R.string.share), ignoreCase = true)) {
            sharedDetails(0)/*open Share*/
        } else if (item.title.equals(getString(R.string.mark_sold), ignoreCase = true)) {
            marksSoldDialogBox()

        } else if (item.title.equals(getString(R.string.delete_listing), ignoreCase = true)) {
            deleteProductDialog()

        } else if (item.title.equals(getString(R.string.edit_item), ignoreCase = true)) {
            //   val commentsList= ArrayList<CommentModal>()
            //   val reportsList=ArrayList<ReportResponse>()
            //  dataIntent!!.comments=commentsList
            //  dataIntent!!.reports=reportsList
            //    Log.e("<<clicked>>","clicked")
            val intent = Intent(this, AddDetailsActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 1)
            intent.putExtra(AppConstants.PRODUCT_DETAIL, dataIntent!!)
            startActivityForResult(intent, 2)

        }
    }

    private fun deleteProductDialog() {
        shareDialog = DeleteCommentDialog(this, getString(R.string.delete_heading), getString(R.string.delete_des), object : SelectionListener {
            override fun leaveClick() {
                if (Utils.isOnline(this@ProductDetailsActivity)) {
                    deleteItemApi()
                } else {
                    Utils.showSimpleMessage(this@ProductDetailsActivity, getString(R.string.check_internet)).show()
                }

            }
        })
        shareDialog.show()
    }

    private fun deleteItemApi() {
        pbProgressProduct.visibility = View.VISIBLE
        ServiceHelper().deleteListingService(itemId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressProduct.visibility = View.GONE
                        val likeResponse = response.body() as DisLikeResponse
                        Utils.showToast(this@ProductDetailsActivity, "Item deleted successfully.")
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                        returnIntent.putExtra(AppConstants.TYPE, "deleteItem")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressProduct.visibility = View.GONE
                        Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                    }
                })
    }

    private fun sharePost() {
        val i = Intent(android.content.Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Verkoop App product!")
        i.putExtra(android.content.Intent.EXTRA_TEXT, "find text for ")
        startActivity(Intent.createChooser(i, "Share via"))
    }

    private fun marksSoldDialogBox() {
        val shareDialog = DeleteCommentDialog(this, getString(R.string.confirm_sold), getString(R.string.sold_des), object : SelectionListener {
            override fun leaveClick() {
                if (Utils.isOnline(this@ProductDetailsActivity)) {
                    markAsSoldApi()
                } else {
                    Utils.showSimpleMessage(this@ProductDetailsActivity, getString(R.string.check_internet)).show()
                }

            }
        })
        shareDialog.show()
    }

    private fun markAsSoldApi() {
        ServiceHelper().markAsSoldService(itemId, MarkAsSoldRequest(Utils.getPreferencesString(this, AppConstants.USER_ID)),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        //   val blockResponse = response.body() as DisLikeResponse
                        Utils.showToast(this@ProductDetailsActivity, "Item marked as sold successfully.")
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                        returnIntent.putExtra(AppConstants.TYPE, "soldItem")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    override fun onFailure(msg: String?) {
                        // pbProgressReport.visibility=View.GONE
                        Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                    }
                })

    }

    private fun getItemDetailsService(itemId: Int) {
        pbProgressProduct.visibility = View.VISIBLE
        ServiceHelper().getItemDetailService(itemId, Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressProduct.visibility = View.GONE
                        val detailsResponse = response.body() as ItemDetailsResponse
                        if (detailsResponse.data != null) {
                            dataIntent = detailsResponse.data
                            commentsList.addAll(detailsResponse.data.comments!!)
                            commentListAdapter.setData(commentsList)
                            commentListAdapter.notifyDataSetChanged()
                            reportList.addAll(detailsResponse.data.reports!!)
                            /*for (i in detailsResponse.data.items_image.indices) {
                                imageURLLIst.add(detailsResponse.data.items_image[i].url)
                            }*/
                            setData(detailsResponse.data)
                        } else {
//                                Utils.showSimpleMessage(this@ProductDetailsActivity, detailsResponse.message).show()
                            Toast.makeText(this@ProductDetailsActivity, detailsResponse.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressProduct.visibility = View.GONE
                        Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                dataComment = data!!.getParcelableExtra(AppConstants.COMMENT_RESULR)
                if (dataComment != null) {
                    val dataComment = CommentModal(dataComment!!.username, dataComment!!.profile_pic, dataComment!!.id, Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), dataComment!!.comment, dataComment!!.created_at)
                    commentsList.add(dataComment)
                    commentListAdapter.setData(commentsList)
                    commentListAdapter.notifyDataSetChanged()
                    Handler().postDelayed({
                        scrollView1.fullScroll(View.FOCUS_DOWN)
                    }, 200)
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val type = data!!.getStringExtra(AppConstants.TYPE)
                if (type.equals("UpdateItem", ignoreCase = true)) {
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.TYPE, "UpdateItem")
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }

        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }


    override fun onBackPressed() {
        if (powerMenu != null && powerMenu!!.isShowing) {
            powerMenu!!.dismiss()
//        } else if (likeInitialCount != likeCount) {
//            val returnIntent = Intent()
//            returnIntent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
//            returnIntent.putExtra(AppConstants.TOTAL_LIKE, likeCount)
//            returnIntent.putExtra(AppConstants.IS_LIKED, isLiked)
//            returnIntent.putExtra(AppConstants.COMING_FROM, comingFrom)
//            setResult(Activity.RESULT_OK, returnIntent)
//            finish()
        } else {
            //  super.onBackPressed()
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }
    }

    private fun makeOffer(productType: Int, type: Int, price: Double, offeredPrice: Double, minPrice: Double) {
        createOfferDialog = CreatOfferDialog(productType, minPrice, type, offeredPrice, price, this, object : MakeOfferListener {
            override fun makeOfferClick(offerPrice: Double) {
                // Utils.showToast(this@ProductDetailsActivity,offerPrice.toString())
                makeOfferEvent(offerPrice)
            }

        })
        createOfferDialog!!.show()
        createOfferDialog!!.showDialog(0, this@ProductDetailsActivity)
    }

    private fun makeOfferEvent(Offerprice: Double) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", userId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", Offerprice)
            jsonObject.put("type", 2)
            jsonObject.put("price", Offerprice)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.MAKE_OFFER_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject

                runOnUiThread {
                    try {
                        if (data.getString("status") == "1") {
                            saveDataToDb(data)
                            val intent = Intent(this, ChatActivity::class.java)
                            intent.putExtra(AppConstants.USER_ID, userId)
                            intent.putExtra(AppConstants.USER_NAME, userName)
                            intent.putExtra(AppConstants.ITEM_ID, itemId)
                            intent.putExtra(AppConstants.PROFILE_URL, profilePic)
                            intent.putExtra(AppConstants.PRODUCT_URL, productImage)
                            intent.putExtra(AppConstants.PRODUCT_PRICE, price)
                            intent.putExtra(AppConstants.OFFERED_PRICE, Offerprice)
                            intent.putExtra(AppConstants.IS_SOLD, isSoldItem)
                            intent.putExtra(AppConstants.PRODUCT_NAME, productName)
                            startActivity(intent)
                            dataResponse!!.make_offer = dataResponse!!.make_offer
                            dataResponse!!.offer_price = Offerprice

                        } else {

                        }
                    } catch (e: Exception) {
                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun saveDataToDb(data: JSONObject) {
        val addToDb = ArrayList<ChatData>()
        try {
            val chatData = ChatData(data.getInt("message_id"),
                    data.getInt("sender_id"),
                    data.getInt("receiver_id"),
                    data.getString("message"),
                    data.getString("timestamp"),
                    data.getInt("type"),
                    data.getInt("item_id"),
                    data.getInt("chat_user_id"),
                    0)
            addToDb.add(chatData)
            dbHelper!!.chatHistoryInsertData(addToDb)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun initKeyBoardListener() {
        // Threshold for minimal keyboard height.
        val MIN_KEYBOARD_HEIGHT_PX = 150
        // Top-level window decor view.
        val decorView = window.decorView
        // Register global layout listener.
        decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            // Retrieve visible rectangle inside window.
            private val windowVisibleDisplayFrame = Rect()
            private var lastVisibleDecorViewHeight: Int = 0

            override fun onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
                val visibleDecorViewHeight = windowVisibleDisplayFrame.height()

                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        Log.e("Pasha", "SHOW")
                        if (createOfferDialog != null) {
                            createOfferDialog!!.showDialog(1, this@ProductDetailsActivity)
                        }

                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        Log.e("Pasha", "HIDE")
                        if (createOfferDialog != null) {
                            createOfferDialog!!.showDialog(0, this@ProductDetailsActivity)
                        }

                    }

                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight
            }
        })
    }

    private fun sharedDetails(type: Int) {

        val buo = BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                //.setCanonicalIdentifier("content/12345")
                .setTitle(productName)
                .setContentDescription(productDescription)
                .setContentImageUrl(AppConstants.IMAGE_URL + productImage)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(ContentMetadata()
                        .addCustomMetadata("product_id", itemId.toString())
                        .addCustomMetadata("type", 1.toString()))
        val lp = LinkProperties()
                .setChannel("sms")
                .setFeature("sharing")

        buo.generateShortUrl(this, lp) { url, error ->
            if (error == null) {
                when (type) {
                    1 -> faceBookShareDialog(url)
                    2 -> {
                        val sendIntent = Intent("android.intent.action.MAIN")
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.type = "text/plain"
                        sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                        sendIntent.`package` = "com.whatsapp"
                        startActivity(sendIntent)
                    }
                    else -> {
                        val sharingIntent = Intent(Intent.ACTION_SEND)
                        sharingIntent.type = "text/html"
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Verkoop")
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, url)
                        startActivity(Intent.createChooser(sharingIntent, "Share using"))
                    }
                }
            }
        }

    }

    private fun faceBookShareDialog(url: String?) {
        val content = ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url))
                .build()
        shareDialogFacebook!!.show(content)
    }
}

