package com.verkoopapp.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.verkoopapp.R
import com.verkoopapp.adapter.UserProfileItemAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.*
import kotlinx.android.synthetic.main.buy_cars_activity.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.user_profile_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Response

class UserProfileActivity:AppCompatActivity() {
    private var powerMenu: PowerMenu? = null
    private lateinit var myProfileItemAdapter: UserProfileItemAdapter
    private var itemsList = ArrayList<ItemHome>()
    private var reportsList=ArrayList<ReportResponse>()
    private var isBlockClick: Boolean = false
    private var userId:Int=0
    private var blockedId:Int=0
    private var typeMenu:Int=0
    private var userName:String=""
    private var fromLikeEvent: Boolean = false
    private var positionFromLikeEvent: Int = 0
    private var typeForEventBus: Int = 0
    private var comingFrom: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile_activity)
        userId=intent.getIntExtra(AppConstants.USER_ID,0)
        if(intent.getStringExtra(AppConstants.USER_NAME)!=null) {
            userName = intent.getStringExtra(AppConstants.USER_NAME)!!
        }
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        setData()
        setAdapter(width)
        if (Utils.isOnline(this)) {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            pbProgressUser.visibility = View.VISIBLE
            myProfileInfoApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }
    private fun setAdapter(width: Int) {
        val linearLayoutManager = GridLayoutManager(this, 2)
        linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (myProfileItemAdapter.getItemViewType(position)) {
                    myProfileItemAdapter.PROFILE_DETAILS -> 2
                    else ->
                        1
                }
            }
        }
        rvUserPostsList.layoutManager = linearLayoutManager
        rvUserPostsList.addItemDecoration(GridSpacingProfileDecorate(2,Utils.dpToPx(this,2F).toInt(),false))
        myProfileItemAdapter = UserProfileItemAdapter(this, width,userId)
        rvUserPostsList.isNestedScrollingEnabled = false
        rvUserPostsList.isFocusable = false
        rvUserPostsList.adapter = myProfileItemAdapter
    }
    private fun setData() {
        scUserProfile.setOnRefreshListener {
            if (Utils.isOnline(this)) {
                if (Utils.isOnline(this)) {
                    myProfileInfoApi()
                } else {
                    scUserProfile.isRefreshing = false
                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                }
            } else {
                scUserProfile.isRefreshing = false
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
        scUserProfile.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary)
        ivRight.setImageResource(R.drawable.menu_icone)
        ivRight.visibility= View.VISIBLE
        tvHeaderLoc.text=userName
        ivLeftLocation.setOnClickListener { onBackPressed() }
        ivRight.setOnClickListener {
            if(blockedId>0){
                openPowerMenu(1)
            }else{
                openPowerMenu(0)
            }

        }
    }



    private fun openPowerMenu(type:Int) {
        var option="Block user"
        if(type==1){
            option="Unblock user"
        }
        powerMenu = PowerMenu.Builder(this)
                .addItem(PowerMenuItem(option, false)) // add an item.
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
        powerMenu!!.showAsAnchorRightTop(ivRight)
    }

    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, item ->
        powerMenu!!.selectedPosition = position
        powerMenu!!.dismiss()

        if (position == 1) {
            val reportIntent = Intent(this, ReportUserActivity::class.java)
            reportIntent.putParcelableArrayListExtra(AppConstants.REPORT_LIST, reportsList)
            reportIntent.putExtra(AppConstants.COMING_FROM,1)
            reportIntent.putExtra(AppConstants.ITEM_ID,userId)
            startActivity(reportIntent)
        } else {
            if(!isBlockClick) {
                if (item.getTitle().equals("Block user", ignoreCase = true)) {
                    blockUserDialog()
                } else if (item.getTitle().equals("Unblock user", ignoreCase = true)) {
                    unBlockUserDialog()
                }
            }
        }
    }

    private fun unBlockUserDialog() {
        val shareDialog = DeleteCommentDialog(this,getString(R.string.unblock_user),getString(R.string.unblock_sure),object : SelectionListener {
            override fun leaveClick() {
                isBlockClick=true
                unBockUserApi()
            }
        })
        shareDialog.show()
    }



    private fun blockUserDialog() {
        val shareDialog = DeleteCommentDialog(this,getString(R.string.block_user),getString(R.string.block_sure),object : SelectionListener {
            override fun leaveClick() {
                isBlockClick=true
               blockUserApi()
            }
        })
        shareDialog.show()
    }



    private fun myProfileInfoApi() {
        val userRequest=FollowRequest(Utils.getPreferencesString(this,AppConstants.USER_ID),userId)
        ServiceHelper().userProfileService(userRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        scUserProfile.isRefreshing = false
                        pbProgressUser.visibility = View.GONE
                     window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val myProfileResponse = response.body() as UserProfileResponse
                        if (myProfileResponse.data != null) {
                            rvUserPostsList.visibility=View.VISIBLE
                            itemsList.clear()
                            itemsList = myProfileResponse.data.items
                            myProfileItemAdapter.setData(itemsList)
                            myProfileItemAdapter.setProfileData(myProfileResponse.data)
                            myProfileItemAdapter.notifyDataSetChanged()
                            if(!TextUtils.isEmpty(myProfileResponse.data.username)) {
                                tvHeaderLoc.text = myProfileResponse.data.username
                            }
                            blockedId=myProfileResponse.data.block_id
                            reportsList=myProfileResponse.data.reports
                        } else {
//                            Utils.showSimpleMessage(homeActivity, myProfileResponse.message).show()
                        }

                        if (fromLikeEvent == true) {
                            if (comingFrom.equals("UserProfileAdapter")) {
                                rvUserPostsList.scrollToPosition(positionFromLikeEvent)
                            }
                        }
                    }

                    override fun onFailure(msg: String?) {
                       window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        scUserProfile.isRefreshing = false
                        pbProgressUser.visibility = View.GONE

                        Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })
    }


    override fun onBackPressed() {
        if (powerMenu!=null&&powerMenu!!.isShowing) {
            powerMenu!!.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    private fun unBockUserApi() {
        ServiceHelper().unBlockUserService(blockedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isBlockClick=false
                        val likeResponse = response.body() as DisLikeResponse
                        Utils.showSimpleMessage(this@UserProfileActivity, "This user has been unblocked.").show()
                        blockedId=0
                    }
                    override fun onFailure(msg: String?) {
                        isBlockClick=false
                        Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })
    }

    private fun blockUserApi() {
        val blockUserRequest= BlockUserRequest(Utils.getPreferencesString(this,AppConstants.USER_ID), userId)
        ServiceHelper().blockUserService(blockUserRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isBlockClick=false
                        val blockResponse = response.body() as BlockUserResponse
                        if(blockResponse.data!=null) {
                            Utils.showSimpleMessage(this@UserProfileActivity, "This user has been blocked.").show()
                            blockedId = blockResponse.data.id
                            typeMenu = 1
                        }
                    }

                    override fun onFailure(msg: String?) {
                        isBlockClick=false
                        //    pbProgressReport.visibility=View.GONE
                        //    Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })

    }


    @Subscribe
    fun MessageEventOnLikeUserProfile(event: MessageEventOnLikeUserProfile) {
        fromLikeEvent = true
        comingFrom = event.comingFrom
        positionFromLikeEvent = event.position
        if (Utils.isOnline(this)) {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            pbProgressUser.visibility = View.VISIBLE
            myProfileInfoApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
        Handler().postDelayed(Runnable {
            fromLikeEvent = false
        }, 2500)
    }

    override fun onStart() {
        super.onStart()
        try {
            EventBus.getDefault().register(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}