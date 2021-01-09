package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.AdvertPackageAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.*
import kotlinx.android.synthetic.main.advert_package_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class AdvertPackagesActivity : AppCompatActivity(), AdvertPackageAdapter.SubmitBannerCallBack {
    private lateinit var advertPackageAdapter: AdvertPackageAdapter
    private var imageUrl = ""
    private var categoryId: Int = 0
    private var comingFor = ""
    private var bannerID: Int = 0
    override fun planSelectionClick(planId: Int, totalCoin: Int) {
        submitDialog(planId, totalCoin)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advert_package_activity)
        Log.e("<<TotalCoin>>", Utils.getPreferencesInt(this, AppConstants.COIN).toString())
        tvCoinPackage.text = Utils.getPreferencesInt(this, AppConstants.COIN).toString()
        if (intent.getStringExtra(AppConstants.COMING_FROM) != null) {
            comingFor = intent.getStringExtra(AppConstants.COMING_FROM)!!
        }
        if (comingFor.equals("forRenewPackage")) {
            bannerID = intent.getIntExtra(AppConstants.BANNERID, 0)
        } else {
            imageUrl = intent.getStringExtra(AppConstants.IMAGE_URL).toString()
            categoryId = intent.getIntExtra(AppConstants.CATEGORY_ID, 0)
        }
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        setData()
        setAdapter(width)
        if (Utils.isOnline(this)) {
            getAdvertPlanApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }

    }

    private fun setData() {
        tvHeaderLoc.text = getString(R.string.add)
        ivLeftLocation.setOnClickListener { onBackPressed() }
    }

    private fun setAdapter(width: Int) {
        val mLayoutManager = GridLayoutManager(this, 3)
        rvAdvertPackages.layoutManager = mLayoutManager
        advertPackageAdapter = AdvertPackageAdapter(this, width)
        rvAdvertPackages.adapter = advertPackageAdapter
    }

    private fun getAdvertPlanApi() {
        pbAdvertPackage.visibility = View.VISIBLE
        ServiceHelper().getAdvertisementPlanService(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbAdvertPackage.visibility = View.GONE
                val responseWallet = response.body() as AdvertPlanActivity
                if (responseWallet.data.isNotEmpty()) {
                    advertPackageAdapter.setData(responseWallet.data)
                    advertPackageAdapter.notifyDataSetChanged()

                } else {
                    Utils.showSimpleMessage(this@AdvertPackagesActivity, "No data found.").show()
                }
            }

            override fun onFailure(msg: String?) {
                pbAdvertPackage.visibility = View.GONE
                Utils.showSimpleMessage(this@AdvertPackagesActivity, msg!!).show()
            }
        })

    }

    private fun submitDialog(planId: Int, coin: Int) {
        val proceedDialog = ProceedDialog(this, "", object : SelectionListener {
            override fun leaveClick() {
                if (comingFor.equals("forRenewPackage")) {
                    renewBannerApi(planId)
                } else {
                    purchaseAdvertisementApi(planId, coin)
                }
            }
        })
        proceedDialog.show()
    }

    private fun renewBannerApi(planId: Int) {
        ServiceHelper().renewBannerService(RenewBannerRequest(bannerID, planId),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val likeResponse = response.body() as DisLikeResponse
                        if (likeResponse.message.equals("Banner renewed successfully.")) {
                            val returnIntent = Intent()
                            setResult(Activity.RESULT_OK, returnIntent)
                            Utils.showSimpleMessage(this@AdvertPackagesActivity, getString(R.string.banner_renewed_successfully)).show()
                            finish()
                        } else {
                            Utils.showSimpleMessage(this@AdvertPackagesActivity, likeResponse.message!!).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(this@AdvertPackagesActivity, msg!!).show()
                    }
                })
    }

    private fun purchaseAdvertisementApi(planId: Int, coin: Int) {
        val uploadBannerRequest = UploadBannerRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), categoryId, planId, imageUrl)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbAdvertPackage.visibility = View.VISIBLE
        ServiceHelper().updateBannerService(uploadBannerRequest, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbAdvertPackage.visibility = View.GONE
                val homeDataResponse = response.body() as ProfileUpdateResponse
                Utils.showToast(this@AdvertPackagesActivity, homeDataResponse.message)
                setDialogBox()
                if (coin <= Utils.getPreferencesInt(this@AdvertPackagesActivity, AppConstants.COIN)) {
                    val remainingCoin = Utils.getPreferencesInt(this@AdvertPackagesActivity, AppConstants.COIN) - coin
                    Utils.saveIntPreferences(this@AdvertPackagesActivity, AppConstants.COIN, remainingCoin)
                }

            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbAdvertPackage.visibility = View.GONE
                Utils.showSimpleMessage(this@AdvertPackagesActivity, msg!!).show()
            }
        })

    }

    private fun setDialogBox() {
        val shareDialog = WarningDialog(this, object : SelectionListener {
            override fun leaveClick() {
                val intentBack = Intent(this@AdvertPackagesActivity, ViewAllBannerActivity::class.java)
                intentBack.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentBack)
            }
        })
        shareDialog.show()

    }
    /* override fun onBackPressed() {
         val returnIntent = Intent()
         setResult(Activity.RESULT_CANCELED, returnIntent)
         finish()
     }*/
}