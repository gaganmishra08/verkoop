package com.verkoopapp.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.annotation.RequiresApi
//import com.github.nkzawa.socketio.client.Ack
//import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.adapter.ChatAdapter
import com.verkoopapp.adapter.ChatSuggestionAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.offlinechatdata.ChatResponse
import com.verkoopapp.offlinechatdata.DbHelper
import com.verkoopapp.utils.*
import io.realm.RealmResults
import io.socket.client.Ack
import io.socket.client.Socket
import kotlinx.android.synthetic.main.chat_activity.*
import kotlinx.android.synthetic.main.toolbar_bottom.*
import kotlinx.android.synthetic.main.toolbar_product_details.*
import okhttp3.internal.Util
import org.apache.commons.lang3.StringEscapeUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private val socket: Socket? = VerkoopApplication.getAppSocket()
    private var powerMenu: PowerMenu? = null
    private var senderId = 0
    private var itemId = 0
    private var isSold = 0
    private var is_block = 0
    private var user_block_id = 0
    private var isRate = 0
    private var categoryId = 0
    private var userName = ""
    private var profileUrl = ""
    private var price: Double = 0.0
    private var minPrice: Double = 0.0
    private var maxPrice: Double = 0.0
    private var productUrl = ""
    private var productName = ""
    private lateinit var chatAdapter: ChatAdapter
    private var createOfferDialog: CreatOfferDialog? = null
    private var chatList = ArrayList<ChatData>()
    private var dbHelper: DbHelper? = null
    private var chatHistoryModels: RealmResults<ChatResponse>? = null
    private var isMyProduct: Boolean = false
    private var chatUserId = 0
    private var uriTemp: Uri? = null
    private var mCurrentPhotoPath: String? = null
    private var isBlockClick: Boolean = false
    private var blockedId: Int = 0
    private var userId: Int = 0
    private var reportsList = ArrayList<ReportListData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
        senderId = intent.getIntExtra(AppConstants.USER_ID, 0)/*Receiver id*/
        itemId = intent.getIntExtra(AppConstants.ITEM_ID, 0)
        categoryId = intent.getIntExtra(AppConstants.CATEGORY_ID, 0)
        price = intent.getDoubleExtra(AppConstants.PRODUCT_PRICE, 0.0)
        minPrice = intent.getDoubleExtra(AppConstants.MIN_PRICE, 0.0)
        maxPrice = intent.getDoubleExtra(AppConstants.MAX_PRICE, 0.0)
        userName = intent.getStringExtra(AppConstants.USER_NAME).toString()
        profileUrl = intent.getStringExtra(AppConstants.PROFILE_URL).toString()
        productUrl = intent.getStringExtra(AppConstants.PRODUCT_URL).toString()
        productName = intent.getStringExtra(AppConstants.PRODUCT_NAME).toString()
        isMyProduct = intent.getBooleanExtra(AppConstants.IS_MY_PRODUCT, false)
        isRate = intent.getIntExtra(AppConstants.IS_RATE, 0)
        isSold = intent.getIntExtra(AppConstants.IS_SOLD, 0)
//        is_block = intent.getIntExtra(AppConstants.IS_BLOCK, 0)
//        user_block_id = intent.getIntExtra(AppConstants.USER_BLOCK_ID, 0)
//        blockedId = intent.getIntExtra(AppConstants.BLOCK_ID, 0)

        if (socket!!.connected()) {
            directChat()
        }
        if (isRate == 1) {
            llViewOffer.visibility = View.GONE
        }
        if (isSold == 1) {
            llViewOffer.visibility = View.GONE
        } else {
            llViewOffer.visibility = View.GONE
        }
        dbHelper = DbHelper()
        initKeyBoardListener()
        setData()
        setAdapter()
        if (Utils.isOnline(this)) {
            setOfflineHistory()
            getChatHistory()
            getReportList()
        } else {
            Handler().postDelayed({
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }, 300)
            setOfflineHistory()
        }

        rvChatSuggestion.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
                this@ChatActivity,
                LinearLayoutManager.HORIZONTAL,
                false
        )
        rvChatSuggestion.layoutManager = linearLayoutManager
        rvChatSuggestion.itemAnimator = DefaultItemAnimator()
        val dividerItemDecoration = DividerItemDecoration(
                rvChatSuggestion.context,
                linearLayoutManager.orientation
        )
        ContextCompat.getDrawable(this@ChatActivity, R.drawable.rv_devider)
                ?.let { it1 ->
                    dividerItemDecoration.setDrawable(
                            it1
                    )
                }
        rvChatSuggestion.addItemDecoration(dividerItemDecoration)
        val adapter =
                ChatSuggestionAdapter(
                        this@ChatActivity,
                        arrayListOf("is still available", "what is your best price", "is the price is negotiable", "what conditions is it in?")
                ) {
                    if (socket!!.connected()) {
                        sendMssgEvent(it, 0)
                        rvChatSuggestion.visibility = View.GONE
                    } else {
                        Utils.showSimpleMessage(this, "Socket Disconnected.").show()
                    }
                }
        rvChatSuggestion.adapter = adapter
    }

    private fun getReportList() {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().reportListService(1,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@ChatActivity)
                        val reportListData = response.body() as ReportListResponse
                        if (reportListData.data != null) {
                            reportsList = reportListData.data
                        }
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@ChatActivity)
                        Utils.showSimpleMessage(this@ChatActivity, msg!!).show()
                    }
                })
    }

    private fun directChat() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("price", price.toString())
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            if (socket!!.connected()) {
                socket.emit(AppConstants.DIRECT_CHAT, jsonObject, Ack {
                    Log.e("<<<Chat_User_Id>>>", Gson().toJson(it[0]))
                    val data = it[0] as JSONObject
                    runOnUiThread {
                        try {
                            if (data.getString("status") == "1") {
                                runOnUiThread {
                                    chatUserId = data.getInt("chat_user_id")
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }
                })
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setOfflineHistory() {
        chatList.clear()
        chatHistoryModels = dbHelper!!.getChatHistoryList(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId, itemId)
        for (i in chatHistoryModels!!.indices) {
            val chatData = ChatData(chatHistoryModels!![i]!!.message_id,
                    chatHistoryModels!![i]!!.sender_id,
                    chatHistoryModels!![i]!!.receiver_id,
                    chatHistoryModels!![i]!!.message!!,
                    chatHistoryModels!![i]!!.timestamp!!,
                    chatHistoryModels!![i]!!.type,
                    chatHistoryModels!![i]!!.item_id,
                    chatHistoryModels!![i]!!.chat_user_id,
                    chatHistoryModels!![i]!!.is_read)
            chatList.add(chatData)
        }
        chatAdapter.setData(chatList)
        chatAdapter.notifyDataSetChanged()
        rvChatList.scrollToPosition(chatList.size - 1)

    }

    private fun getChatHistory() {
        chatHistoryModels = dbHelper!!.getChatHistoryList(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId, itemId)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            if (chatHistoryModels!!.size > 0) {
                //  jsonObject.put("message_id", chatHistoryModels!![chatHistoryModels!!.size - 1]!!.message_id)
                jsonObject.put("message_id", chatHistoryModels!!.last()!!.message_id)
            } else {
                jsonObject.put("message_id", "0")
            }
            jsonObject.put("item_id", itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            if (socket!!.connected()) {
                socket.emit(AppConstants.CHAT_LIST, jsonObject, Ack {
                    Log.e("<<<Response>>>", Gson().toJson(it[0]))
                    val data = it[0] as JSONObject
                    runOnUiThread {
                        try {
                            if (data.getString("status") == "1") {
                                if (data.has("block_id")) {
                                    blockedId = data.getInt("block_id")
                                }
                                if (data.has("user_block_id")) {
                                    user_block_id = data.getInt("user_block_id")
                                }
                                is_block = data.getInt("is_block")
                                if (user_block_id == null) {
                                    chatList.clear()
                                }
                                try {
                                    val listdata = java.util.ArrayList<JSONObject>()
                                    try {
                                        val chatHistory = data.getJSONArray("data")

                                        if (chatHistory != null) {
                                            for (i in 0 until chatHistory.length()) {
                                                listdata.add(chatHistory.getJSONObject(i))
                                            }
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }

                                    for (data2 in listdata) {
                                        try {
                                            val chatData = ChatData(data2.getInt("message_id"),
                                                    data2.getInt("sender_id"),
                                                    data2.getInt("receiver_id"),
                                                    data2.getString("message"),
                                                    data2.getString("timestamp"),
                                                    data2.getInt("type"),
                                                    data2.getInt("item_id"),
                                                    data2.getInt("chat_user_id"),
                                                    data2.getInt("is_read"))
                                            chatList.add(chatData)
                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }

                                    }
                                    if (user_block_id == null) {
                                        dbHelper!!.chatHistoryInsertData(chatList)
                                        chatAdapter.setData(chatList)
                                        chatAdapter.notifyDataSetChanged()
                                        rvChatList.scrollToPosition(chatList.size - 1)
                                        setOfflineHistory()
                                    }

                                    if (is_block == 1) {
                                        if (user_block_id != null) {
                                            if (user_block_id == Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
                                                etMssg.isEnabled = false
                                                ivUpload.isEnabled = false
                                                ivSend.isEnabled = false
                                                etMssg.setHint(getString(R.string.you_cant_reply))
                                            } else {
                                                etMssg.isEnabled = false
                                                etMssg.setText("")
                                                ivUpload.isEnabled = false
                                                ivSend.isEnabled = false
                                                etMssg.setHint(getString(R.string.unblock_this_user))
                                            }
                                            etMssg.isEnabled = false
                                            ivUpload.isEnabled = false
                                            ivSend.isEnabled = false
                                            llViewOffer.visibility = View.GONE
                                        }
                                    } else {
                                        etMssg.isEnabled = true
                                        ivUpload.isEnabled = true
                                        ivSend.isEnabled = true
                                        etMssg.setHint(getString(R.string.enter_here))
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                makeOfferManage(data.getInt("offer_status"), data.getInt("is_block"))
                            } else {

                            }
                        } catch (e: Exception) {
                        }
                    }
                })
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun makeOfferManage(offerStatus: Int, blockStatus: Int) {
        if (isSold != 1) {
            if (isMyProduct && offerStatus == 5) {
                llViewOffer.visibility = View.GONE
            } else if (!isMyProduct && offerStatus == 5) {
                llViewOffer.visibility = View.VISIBLE
                tvViewProfile.text = getString(R.string.view_seller)
                tvMakeOffer.text = getString(R.string.make_offer)
            } else if (isMyProduct && offerStatus == 0) {
                llViewOffer.visibility = View.VISIBLE
                tvViewProfile.text = getString(R.string.decline_offer)
                tvMakeOffer.text = getString(R.string.accept_offer)
            } else if (!isMyProduct && offerStatus == 0) {
                llViewOffer.visibility = View.VISIBLE
                tvMakeOffer.text = getString(R.string.edit_offer)
                tvViewProfile.text = getString(R.string.cancel_offer)
            } else if (isMyProduct && offerStatus == 1) {
                llViewOffer.visibility = View.VISIBLE
                tvMakeOffer.visibility = View.GONE
                tvViewProfile.text = getString(R.string.leave_review)
            } else if (!isMyProduct && offerStatus == 1) {
                llViewOffer.visibility = View.VISIBLE
                tvMakeOffer.visibility = View.GONE
                tvViewProfile.text = getString(R.string.leave_review_seller)
            } else if (isMyProduct && offerStatus == 2) {
                llViewOffer.visibility = View.GONE
            } else if (!isMyProduct && offerStatus == 2) {
                llViewOffer.visibility = View.VISIBLE
                tvViewProfile.text = getString(R.string.view_seller)
                tvMakeOffer.text = getString(R.string.make_offer)
            }
        } else {
            if (isRate != 1) {
                if (isMyProduct && offerStatus == 1) {
                    llViewOffer.visibility = View.VISIBLE
                    tvMakeOffer.visibility = View.GONE
                    tvViewProfile.text = getString(R.string.leave_review)
                } else if (!isMyProduct && offerStatus == 1) {
                    llViewOffer.visibility = View.VISIBLE
                    tvMakeOffer.visibility = View.GONE
                    tvViewProfile.text = getString(R.string.leave_review_seller)
                } else if (offerStatus == 6) {
                    llViewOffer.visibility = View.GONE
                }
            } else {
                llViewOffer.visibility = View.GONE
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvChatList.layoutManager = layoutManager
        chatAdapter = ChatAdapter(this)
        rvChatList.adapter = chatAdapter
        rvChatList.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom)
                layoutManager.smoothScrollToPosition(rvChatList, null, chatAdapter.itemCount)
        }

    }

    private fun setData() {
        tvUserName.text = userName
        tvProductDes.text = productName
        tvProducePrice.text = StringBuilder().append(Utils.getPreferencesString(this@ChatActivity, AppConstants.CURRENCY_SYMBOL) + " ").append(price.toString())
        if (!TextUtils.isEmpty(productUrl)) {
            Picasso.with(this@ChatActivity).load(AppConstants.IMAGE_URL + productUrl)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.post_placeholder)
                    .placeholder(R.mipmap.post_placeholder)
                    .into(ivProductImageHome)
        }
        if (!TextUtils.isEmpty(profileUrl)) {
            Picasso.with(this@ChatActivity).load(AppConstants.IMAGE_URL + profileUrl)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        } else {
            Picasso.with(this@ChatActivity).load(R.mipmap.pic_placeholder)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        }
        ivLeft.setOnClickListener { onBackPressed() }
        llChatDetails.setOnClickListener {
            if (Utils.getPreferencesString(this, AppConstants.USER_ID).toInt() != senderId) {
                val reportIntent = Intent(this, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID, senderId)
                reportIntent.putExtra(AppConstants.USER_NAME, userName)
                startActivity(reportIntent)
            }
        }
        llParentChat.setOnClickListener {
            llParentChat.isEnabled = false
            Handler().postDelayed(Runnable {
                llParentChat.isEnabled = true
            }, 1000)
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra(AppConstants.ITEM_ID, itemId)
            startActivity(intent)
        }
        ivSend.setOnClickListener {
            if (!TextUtils.isEmpty(etMssg.text.toString().trim())) {
                if (socket!!.connected()) {
                    sendMssgEvent(etMssg.text.toString().trim(), 0)
                } else {
                    Utils.showSimpleMessage(this, "Socket Disconnected.").show()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.enter_mssg)).show()
            }
        }
        tvMakeOffer.setOnClickListener {
            if (tvMakeOffer.text.toString().equals(getString(R.string.make_offer), ignoreCase = true)) {
                /*0=Make an offer*/
                if (categoryId == 24 || categoryId == 85) {
                    makeOffer(1, 0, maxPrice, 0.0, minPrice)
                } else {
                    makeOffer(0, 0, price, 0.0, 0.0)
                }

            } else if (tvMakeOffer.text.toString().equals(getString(R.string.accept_offer), ignoreCase = true)) {
                val lastOffer = dbHelper!!.getOfferPriceLast(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId, itemId, 2)
                acceptOfferDialogBox(lastOffer!!.message!!)
            } else if (tvMakeOffer.text.toString().equals(getString(R.string.edit_offer), ignoreCase = true)) {
                /*1=Edit an offer*/
                val lastOffer = dbHelper!!.getOfferPriceLast(Utils.getPreferencesString(this@ChatActivity, AppConstants.USER_ID).toInt(), senderId, itemId, 2)
                if (categoryId == 24 || categoryId == 85) {
                    makeOffer(1, 1, maxPrice, lastOffer!!.message!!.toDouble(), minPrice)
                } else {
                    makeOffer(0, 1, price, lastOffer!!.message!!.toDouble(), 0.0)
                }

            }
        }

        tvViewProfile.setOnClickListener {
            if (tvViewProfile.text.toString().equals(getString(R.string.view_seller), ignoreCase = true)) {
                tvViewProfile.isEnabled = false
                Handler().postDelayed(Runnable {
                    tvViewProfile.isEnabled = true
                }, 1000)
                val reportIntent = Intent(this, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID, senderId)
                reportIntent.putExtra(AppConstants.USER_NAME, userName)
                startActivity(reportIntent)
            } else if (tvViewProfile.text.toString().equals(getString(R.string.decline_offer), ignoreCase = true)) {
                val lastOffer = dbHelper!!.getOfferPriceLast(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId, itemId, 2)
                declineOfferDialogBox(lastOffer!!.message!!)
            } else if (tvViewProfile.text.toString().equals(getString(R.string.cancel_offer), ignoreCase = true)) {
                val lastOffer = dbHelper!!.getOfferPriceLast(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId, itemId, 2)

                cancelOfferDialogBox(lastOffer!!.message!!)
            } else if (tvViewProfile.text.toString().equals(getString(R.string.leave_review), ignoreCase = true)) {
                openRatingDialog(getString(R.string.buyer))
            } else if (tvViewProfile.text.toString().equals(getString(R.string.leave_review_seller), ignoreCase = true)) {
                openRatingDialog(getString(R.string.seller))
            }
        }
        ivRightProduct.setOnClickListener {
            ivRightProduct.isEnabled = false
            Handler().postDelayed(Runnable {
                ivRightProduct.isEnabled = true
            }, 1000)
//            Utils.showToast(this, "work in Progress")
            openPowerMenu()
        }
        ivUpload.setOnClickListener {
            addProfileImage()
        }
    }

    private fun addProfileImage() {
        if (checkPermission()) {
            openPopUp()
        }
    }

    private fun checkPermission(): Boolean {
        val permissionCheck = PermissionCheck(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck.checkGalleryPermission())
                return true
        } else
            return true
        return false
    }

    private fun openPopUp() {
        val shareDialog = SelectOptionDialog(this, object : SelectionOptionListener {
            override fun leaveClick(option: String) {
                if (option.equals(getString(R.string.camera), ignoreCase = true)) {
                    takePicture()
                } else if (option.equals(getString(R.string.gallery), ignoreCase = true)) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Pick From"), EditProfileActivity.REQUEST_GET_PHOTO)
                }
            }
        })
        shareDialog.show()
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(this,
                                applicationContext.packageName + ".provider", photoFile))
                startActivityForResult(takePictureIntent, EditProfileActivity.REQUEST_TAKE_PHOTO)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                EditProfileActivity.REQUEST_TAKE_PHOTO -> {
                    val f = File(mCurrentPhotoPath!!)
                    uriTemp = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", f)
                    CropImage.activity(uriTemp)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)
                }

                EditProfileActivity.REQUEST_GET_PHOTO -> {
                    uriTemp = data?.data

                    mCurrentPhotoPath = Utils.getRealPathFromURI(this, uriTemp!!)
                    val file = File(mCurrentPhotoPath!!)

                    /*uploadImageApi*/
                    if (Utils.isOnline(this)) {
                        updateChatImageService(mCurrentPhotoPath!!)
                    } else {
                        Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                    }
//                    CropImage.activity(uriTemp)
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .setAspectRatio(1, 1)
//                            .start(this)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    mCurrentPhotoPath = Utils.getRealPathFromURI(this, result.uri)
                    val file = File(mCurrentPhotoPath!!)

                    /*uploadImageApi*/
                    if (Utils.isOnline(this)) {
                        updateChatImageService(mCurrentPhotoPath!!)
                    } else {
                        Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                    }
                }
            }
        }
    }

    private fun updateChatImageService(imagePath: String) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressChat.visibility = View.VISIBLE
        ServiceHelper().uploadImageService(imagePath, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProgressChat.visibility = View.GONE
                val imageUrl = response.body() as ChatImageResponse
                sendMssgEvent(imageUrl.data.image, 1)
            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProgressChat.visibility = View.GONE
                Utils.showSimpleMessage(this@ChatActivity, msg!!).show()
            }
        })

    }

    private fun openRatingDialog(rateTo: String) {
        val ratingDialog = RatingBarDialog(this, rateTo, object : RateUserListener {
            override fun rateUserClick(rating: Float, type: String) {
                //rateUserService(rating, itemId)
                rateUserEvent(rating, itemId)
            }

        })
        ratingDialog.show()
    }

    /* private fun rateUserService(rating: Float, itemId: Int) {
         window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                 WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
         // pbProgressAddMoney.visibility = View.VISIBLE
         ServiceHelper().rateUserService(RateUserRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId, itemId, rating),
                 object : ServiceHelper.OnResponse {
                     override fun onSuccess(response: Response<*>) {
                         window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                         //    pbProgressAddMoney.visibility = View.GONE
                         val loginResponse = response.body() as UpdateWalletResponse
                         llViewOffer.visibility = View.GONE
                         Utils.showToast(this@ChatActivity, getString(R.string.review_submitted))
                     }

                     override fun onFailure(msg: String?) {
                         window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                         Utils.showSimpleMessage(this@ChatActivity, msg!!).show()
                     }
                 })
     }*/

    private fun openPowerMenu() {
        var blockTitle = ""
        if (blockedId > 0) {
            blockTitle = "Unblock user"
        } else {
            blockTitle = "Block user"
        }
        powerMenu = PowerMenu.Builder(this)
                .addItem(PowerMenuItem(blockTitle, false)) // add an item.
                .addItem(PowerMenuItem("Report user", false)) // aad an item list.

                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT).
                .setMenuRadius(1f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
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

        if (position == 1) {
            if (reportsList.size > 0) {
                val reportIntent = Intent(this, ReportUserActivity::class.java)
                reportIntent.putParcelableArrayListExtra(AppConstants.REPORT_LIST, reportsList)
                reportIntent.putExtra(AppConstants.COMING_FROM, 1)
                reportIntent.putExtra(AppConstants.ITEM_ID, userId)
                startActivity(reportIntent)
            } else {
                if (Utils.isOnline(this)) {
                    getReportList()
                } else {
                    Utils.showSimpleMessage(this@ChatActivity, getString(R.string.check_internet)).show()
                }
            }
        } else if (position == 0) {
            if (!isBlockClick) {
                if (item.getTitle().equals("Block user", ignoreCase = true)) {
                    blockUserDialog()
                } else if (item.getTitle().equals("Unblock user", ignoreCase = true)) {
                    if (blockedId > 0) {
                        unBlockUserDialog()
                    }
                }
            }
        }
    }

    private fun blockUserDialog() {
        val shareDialog = DeleteCommentDialog(this, getString(R.string.block_user), getString(R.string.block_sure), object : SelectionListener {
            override fun leaveClick() {
                isBlockClick = true
                blockUserApi()
            }
        })
        shareDialog.show()
    }

    private fun unBlockUserDialog() {
        val shareDialog = DeleteCommentDialog(this, getString(R.string.unblock_user), getString(R.string.unblock_sure), object : SelectionListener {
            override fun leaveClick() {
                isBlockClick = true
                unBockUserApi()
            }
        })
        shareDialog.show()
    }


    private fun blockUserApi() {
        val blockUserRequest = BlockUserRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), senderId)
        ServiceHelper().blockUserService(blockUserRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isBlockClick = false
                        val blockResponse = response.body() as BlockUserResponse
                        if (blockResponse.data != null) {
                            Utils.showSimpleMessage(this@ChatActivity, "This user has been blocked.").show()
                            blockedId = blockResponse.data.id
//                            etMssg.isEnabled = false
//                            etMssg.setText("")
//                            ivUpload.isEnabled = false
//                            ivSend.isEnabled = false
//                            etMssg.setHint(getString(R.string.unblock_this_user))
                            getChatHistory()
//                            typeMenu = 1

                        }
                    }

                    override fun onFailure(msg: String?) {
                        isBlockClick = false
                        //    pbProgressReport.visibility=View.GONE
                        //    Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })

    }

    private fun unBockUserApi() {
        ServiceHelper().unBlockUserService(blockedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isBlockClick = false
                        val likeResponse = response.body() as DisLikeResponse
                        Utils.showSimpleMessage(this@ChatActivity, "This user has been unblocked.").show()
                        blockedId = 0
//                        etMssg.isEnabled = true
//                        ivUpload.isEnabled = true
//                        ivSend.isEnabled = true
//                        etMssg.setHint(getString(R.string.enter_here))
                        getChatHistory()
                    }

                    override fun onFailure(msg: String?) {
                        isBlockClick = false
                        Utils.showSimpleMessage(this@ChatActivity, msg!!).show()
                    }
                })
    }

    private fun declineOfferDialogBox(price: String) {
        val shareDialog = DeleteCommentDialog(this, "Decline offer?", "Are you sure you want to decline the offer?", object : SelectionListener {
            override fun leaveClick() {
                if (socket!!.connected()) {
                    declineOfferEvent(price)
                }
            }
        })
        shareDialog.show()
    }

    private fun cancelOfferDialogBox(price: String) {
        val shareDialog = DeleteCommentDialog(this, getString(R.string.cancel_offer), "Are you sure you want to cancel your offer?", object : SelectionListener {
            override fun leaveClick() {
                if (socket!!.connected()) {
                    cancelOfferEvent(price)
                }
            }
        })
        shareDialog.show()
    }

    private fun acceptOfferDialogBox(price: String) {
        val shareDialog = DeleteCommentDialog(this, getString(R.string.confirm_accpt_offer), "Once the offer is accepted, the product will be marked as sold.", object : SelectionListener {
            override fun leaveClick() {
                if (socket!!.connected()) {
                    acceptOfferEvent(price)
                }
            }
        })
        shareDialog.show()
    }


    private fun makeOffer(productType: Int, type: Int, originalPrice: Double, offeredPrice: Double, minPrice: Double) {
        createOfferDialog = CreatOfferDialog(productType, minPrice, type, offeredPrice, originalPrice, this, object : MakeOfferListener {
            override fun makeOfferClick(offerPrice: Double) {
                if (socket!!.connected()) {
                    if (type != 1) {
                        makeOfferEvent(offerPrice)
                    } else {
                        editOfferEvent(offerPrice)
                    }
                }
            }

        })
        createOfferDialog!!.show()
    }

    private fun declineOfferEvent(price: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", price)
            jsonObject.put("type", 4)
            jsonObject.put("chat_user_id", chatUserId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            if (socket!!.connected()) {
                socket.emit(AppConstants.DECLINE_OFFER_EVENT, jsonObject, Ack {
                    Log.e("<<<Response>>>", Gson().toJson(it[0]))
                    val data = it[0] as JSONObject
                    runOnUiThread {
                        if (data.getString("status") == "1") {
                            runOnUiThread {
                                saveDataToDb(data)
                                if (data.getInt("type") == 4) {
                                    makeOfferManage(2, 0)
                                }
                                try {
                                    val chatData = ChatData(data.getInt("message_id"),
                                            data.getInt("sender_id"),
                                            data.getInt("receiver_id"),
                                            data.getString("message"),
                                            data.getString("timestamp"),
                                            data.getInt("type"),
                                            data.getInt("item_id"),
                                            data.getInt("chat_user_id"), 0)
                                    chatList.add(chatData)
                                    chatAdapter.setData(chatList)
                                    chatAdapter.notifyDataSetChanged()
                                    rvChatList.scrollToPosition(chatList.size - 1)
                                    etMssg.text = null
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        } else {

                        }
                    }
                })
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun cancelOfferEvent(price: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", price)
            jsonObject.put("type", 5)
            jsonObject.put("chat_user_id", chatUserId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            if (socket!!.connected()) {
                socket.emit(AppConstants.CANCEL_OFFER_EVENT, jsonObject, Ack {
                    Log.e("<<<Response>>>", Gson().toJson(it[0]))
                    val data = it[0] as JSONObject
                    runOnUiThread {
                        if (data.getString("status") == "1") {
                            runOnUiThread {
                                saveDataToDb(data)
                                if (data.getInt("type") == 5) {
                                    makeOfferManage(5, 0)
                                }
                                try {
                                    val chatData = ChatData(data.getInt("message_id"),
                                            data.getInt("sender_id"),
                                            data.getInt("receiver_id"),
                                            data.getString("message"),
                                            data.getString("timestamp"),
                                            data.getInt("type"),
                                            data.getInt("item_id"),
                                            data.getInt("chat_user_id"), 0)
                                    chatList.add(chatData)
                                    chatAdapter.setData(chatList)
                                    chatAdapter.notifyDataSetChanged()
                                    rvChatList.scrollToPosition(chatList.size - 1)
                                    etMssg.text = null
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        } else {

                        }
                    }
                })
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun editOfferEvent(price: Double) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", price.toString())
            jsonObject.put("type", 2)
            jsonObject.put("price", price)
            jsonObject.put("chat_user_id", chatUserId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            if (socket!!.connected()) {
                socket.emit(AppConstants.EDIT_OFFER_EVENT, jsonObject, Ack {
                    Log.e("<<<Response>>>", Gson().toJson(it[0]))
                    val data = it[0] as JSONObject
                    runOnUiThread {
                        if (data.getString("status") == "1") {
                            runOnUiThread {
                                saveDataToDb(data)
                                if (data.getInt("type") == 2) {
                                    makeOfferManage(0, 0)
                                }
                                try {
                                    val chatData = ChatData(data.getInt("message_id"),
                                            data.getInt("sender_id"),
                                            data.getInt("receiver_id"),
                                            data.getString("message"),
                                            data.getString("timestamp"),
                                            data.getInt("type"),
                                            data.getInt("item_id"),
                                            data.getInt("chat_user_id"), 0)
                                    chatList.add(chatData)
                                    chatAdapter.setData(chatList)
                                    chatAdapter.notifyDataSetChanged()
                                    rvChatList.scrollToPosition(chatList.size - 1)
                                    etMssg.text = null
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        } else {

                        }
                    }
                })
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun acceptOfferEvent(price: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", price)
            jsonObject.put("type", 3)
            jsonObject.put("chat_user_id", chatUserId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            if (socket!!.connected()) {
                socket.emit(AppConstants.ACCEPT_OFFER_EVENT, jsonObject, Ack {
                    Log.e("<<<Response>>>", Gson().toJson(it[0]))
                    val data = it[0] as JSONObject
                    runOnUiThread {
                        if (data.getString("status") == "1") {
                            runOnUiThread {
                                saveDataToDb(data)
                                if (data.getInt("type") == 3) {
                                    makeOfferManage(1, 0)
                                }
                                try {
                                    val chatData = ChatData(data.getInt("message_id"),
                                            data.getInt("sender_id"),
                                            data.getInt("receiver_id"),
                                            data.getString("message"),
                                            data.getString("timestamp"),
                                            data.getInt("type"),
                                            data.getInt("item_id"),
                                            data.getInt("chat_user_id"), 0)
                                    chatList.add(chatData)
                                    chatAdapter.setData(chatList)
                                    chatAdapter.notifyDataSetChanged()
                                    rvChatList.scrollToPosition(chatList.size - 1)
                                    etMssg.text = null
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        } else {

                        }
                    }
                })
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun rateUserEvent(rating: Float, itemId: Int) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", rating.toString())
            jsonObject.put("type", 6)
            jsonObject.put("chat_user_id", chatUserId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            if (socket!!.connected()) {
                socket.emit(AppConstants.RATE_USER_EVENT, jsonObject, Ack {
                    Log.e("<<<Response>>>", Gson().toJson(it[0]))
                    val data = it[0] as JSONObject
                    runOnUiThread {
                        if (data.getString("status") == "1") {
                            runOnUiThread {
                                saveDataToDb(data)
                                if (data.getInt("type") == 3) {
                                    makeOfferManage(6, 0)
                                }
                                try {
                                    val chatData = ChatData(data.getInt("message_id"),
                                            data.getInt("sender_id"),
                                            data.getInt("receiver_id"),
                                            data.getString("message"),
                                            data.getString("timestamp"),
                                            data.getInt("type"),
                                            data.getInt("item_id"),
                                            data.getInt("chat_user_id"), 0)
                                    chatList.add(chatData)
                                    chatAdapter.setData(chatList)
                                    chatAdapter.notifyDataSetChanged()
                                    rvChatList.scrollToPosition(chatList.size - 1)
                                    etMssg.text = null
                                    llViewOffer.visibility = View.GONE
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        } else {

                        }
                    }
                })
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun makeOfferEvent(price: Double) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", price.toString())
            jsonObject.put("type", 2)
            jsonObject.put("price", price)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            if (socket!!.connected()) {
                socket.emit(AppConstants.MAKE_OFFER_EVENT, jsonObject, Ack {
                    Log.e("<<<Response>>>", Gson().toJson(it[0]))
                    val data = it[0] as JSONObject
                    runOnUiThread {
                        if (data.has("status")) {
                            if (data.getString("status") == "1") {
                                runOnUiThread {
                                    saveDataToDb(data)
                                    if (data.getInt("type") == 2) {
                                        makeOfferManage(0, 0)
                                    }
                                    try {
                                        val chatData = ChatData(data.getInt("message_id"),
                                                data.getInt("sender_id"),
                                                data.getInt("receiver_id"),
                                                data.getString("message"),
                                                data.getString("timestamp"),
                                                data.getInt("type"),
                                                data.getInt("item_id"),
                                                data.getInt("chat_user_id"), 0)
                                        chatList.add(chatData)
                                        chatAdapter.setData(chatList)
                                        chatAdapter.notifyDataSetChanged()
                                        rvChatList.scrollToPosition(chatList.size - 1)
                                        etMssg.text = null
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                            } else {

                            }
                        }
                    }
                })
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun sendMssgEvent(message: String, type: Int) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID).toInt())
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", StringEscapeUtils.escapeJava(message))
            jsonObject.put("type", type)
            jsonObject.put("chat_user_id", chatUserId)
            ivSend.isEnabled = false
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            if (socket!!.connected()) {
                socket.emit(AppConstants.SEND_MESSAGES, jsonObject, Ack {
                    Log.e("<<<SendResponse>>>", Gson().toJson(it[0]))
                    val data = it[0] as JSONObject
                    runOnUiThread {
                        ivSend.isEnabled = true
                        try {
                            if (data.getString("status") == "1") {
                                saveDataToDb(data)
                                val chatData = ChatData(data.getInt("message_id"),
                                        data.getInt("sender_id"),
                                        data.getInt("receiver_id"),
                                        data.getString("message"),
                                        data.getString("timestamp"),
                                        data.getInt("type"),
                                        data.getInt("item_id"),
                                        data.getInt("chat_user_id"), 0)
                                chatList.add(chatData)
                                chatAdapter.setData(chatList)
                                chatAdapter.notifyDataSetChanged()
                                rvChatList.scrollToPosition(chatList.size - 1)
                                etMssg.text = null

                            } else {
                                ivSend.isEnabled = true
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
            }
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

    @Subscribe
    fun onEventReceived(model: SocketOnReceiveEvent) {
        val tag = model.tag
        when (tag) {
            AppConstants.RECEIVE_MESSAGE -> {
                val data = model.args[0] as JSONObject
                Log.e("<<<ReceiveResponse>>>", Gson().toJson(model.args[0]))
                runOnUiThread {
                    try {
                        saveDataToDb(data)
                        if (data.getInt("type") == 2) {
                            makeOfferManage(0, 0)
                        } else if (data.getInt("type") == 3) {
                            makeOfferManage(1, 0)
                        } else if (data.getInt("type") == 4) {
                            makeOfferManage(2, 0)
                        } else if (data.getInt("type") == 5) {
                            makeOfferManage(5, 0)
                        }

                        //   val chatHistory = data.getJSONObject("messages")
                        if (data.getInt("sender_id") == senderId) {
                            val chatData = ChatData(data.getInt("message_id"),
                                    data.getInt("sender_id"),
                                    data.getInt("receiver_id"),
                                    data.getString("message"),
                                    data.getString("timestamp"),
                                    data.getInt("type"),
                                    data.getInt("item_id"),
                                    data.getInt("chat_user_id"),
                                    0)
                            chatList.add(chatData)
                            chatAdapter.setData(chatList)
                            chatAdapter.notifyDataSetChanged()
                            rvChatList.scrollToPosition(chatList.size - 1)

                        } else {
                            /*     val mNotificationManager = KSMNotificationManager(applicationContext)
                                 val intent = Intent(this, SplashActivity::class.java)
                                 intent.putExtra("type", "1")
                                 mNotificationManager.showSmallNotification(chatHistory.getString("senderName"), StringEscapeUtils.unescapeJava(chatHistory.getString("message")), intent)*/
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
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

            @RequiresApi(Build.VERSION_CODES.CUPCAKE)
            override fun onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
                val visibleDecorViewHeight = windowVisibleDisplayFrame.height()

                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        Log.e("Pasha", "SHOW")
                        llParentChat.visibility = View.GONE
                        if (createOfferDialog != null) {
                            createOfferDialog!!.showDialog(1, this@ChatActivity)
                        }

                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        Log.e("Pasha", "HIDE")
                        llParentChat.visibility = View.VISIBLE
                        if (createOfferDialog != null) {
                            createOfferDialog!!.showDialog(0, this@ChatActivity)
                        }
                    }
                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                "." +
                        "", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }
}
private fun Socket?.io(socketUrl: String) {

}
