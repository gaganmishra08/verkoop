package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.qr_scann_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder
import com.verkoopapp.models.UserInfoResponse
import com.verkoopapp.network.ServiceHelper
import kotlinx.android.synthetic.main.my_profile_details_row.*
import retrofit2.Response


class QRScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_scann_activity)
        setData()
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text = getString(R.string.qr_code)
        ivScanCode.setOnClickListener {
            ivScanCode.isEnabled = false
            Handler().postDelayed(Runnable {
                ivScanCode.isEnabled = true
            }, 1000)
            startScan()
        }
        if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.QR_CODE))) {
            Picasso.with(this).load(AppConstants.IMAGE_URL + Utils.getPreferencesString(this, AppConstants.QR_CODE))
                    .resize(1024, 1024)
                    .centerCrop()
                    .error(R.mipmap.post_placeholder)
                    .placeholder(R.mipmap.post_placeholder)
                    .into(ivQrCode)
        } else {
            Picasso.with(this).load(AppConstants.IMAGE_URL + Utils.getPreferencesString(this, AppConstants.QR_CODE))
                    .resize(1024, 1024)
                    .centerCrop()
                    .error(R.mipmap.post_placeholder)
                    .placeholder(R.mipmap.post_placeholder)
                    .into(ivQrCode)
        }
    }

    private fun startScan() {
        val materialBarcodeScanner = MaterialBarcodeScannerBuilder()
                .withActivity(this@QRScannerActivity)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withResultListener { barcode ->
                    if (Utils.isOnline(this@QRScannerActivity)) {
                        getUserInfo(barcode.rawValue)
                    } else {
                        Utils.showSimpleMessage(this@QRScannerActivity, getString(R.string.check_internet)).show()
                    }
                }
                .build()
        materialBarcodeScanner.startScan()
    }

    private fun getUserInfo(token: String) {
        ServiceHelper().getAllUserInfoService(token, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val responseBanner = response.body() as UserInfoResponse
                if (responseBanner.data != null) {
                    val intent = Intent(this@QRScannerActivity, TransferCoinsActivity::class.java)
                    intent.putExtra(AppConstants.USER_NAME, responseBanner.data!!.username)
                    intent.putExtra(AppConstants.IMAGE_URL, responseBanner.data!!.profile_pic)
                    intent.putExtra(AppConstants.QR_CODE, token)
                    startActivityForResult(intent, 2)

                } else {
                    Utils.showSimpleMessage(this@QRScannerActivity, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                Utils.showSimpleMessage(this@QRScannerActivity, msg!!).show()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==2){
            if(resultCode==Activity.RESULT_OK){
                finish()
            }
        }
    }

}