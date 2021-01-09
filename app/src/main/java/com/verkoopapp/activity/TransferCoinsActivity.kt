package com.verkoopapp.activity


import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.viewanimator.ViewAnimator
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.KeyboardUtil
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.transfer_coin_dialog_activity.*
import retrofit2.Response


class TransferCoinsActivity : AppCompatActivity() {
    var userName = ""
    var imageUrl = ""
    var qrCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transfer_coin_dialog_activity)
        KeyboardUtil(this, llParentTrans)
        userName = intent.getStringExtra(AppConstants.USER_NAME).toString()
        imageUrl = intent.getStringExtra(AppConstants.IMAGE_URL).toString()
        qrCode = intent.getStringExtra(AppConstants.QR_CODE).toString()
        setData()
        setAnimation()
    }

    private fun setData() {
        tvTransferMssg.text = StringBuffer().append(getString(R.string.send_money)).append(" ").append(userName)
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(this@TransferCoinsActivity).load(AppConstants.IMAGE_URL + imageUrl)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivUserPicSend)
        }
        tvSaveYTransfer.setOnClickListener {
            if (Utils.isOnline(this@TransferCoinsActivity)) {
                if (!TextUtils.isEmpty(etAmountTrans.text.toString())) {
                    tvSaveYTransfer.isEnabled = false
//                    getUserInfo()
                    sendMoneyApi()
                } else {
                    Utils.showSimpleMessage(this@TransferCoinsActivity, "Please enter amount.").show()
                }
            } else {
                Utils.showSimpleMessage(this@TransferCoinsActivity, getString(R.string.check_internet)).show()
            }
        }
        ivFinishTrans.setOnClickListener {
            onBackPressed()
        }
    }

    private fun sendMoneyApi() {
        pbProgressTransfer.visibility = View.VISIBLE
        ServiceHelper().sendMoneyService(SendMoneyRequest(qrCode, Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), (etAmountTrans.text.toString())), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val responseBanner = response.body() as TransferCoinResponse?
                if (responseBanner != null) {
                    if (responseBanner.message.equals("Amount sent successfully.")) {
                        Utils.showToast(this@TransferCoinsActivity, responseBanner.message)
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.INTENT_RESULT, "success")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
//                        onBackPressed()
                    } else {
                        Utils.showSimpleMessage(this@TransferCoinsActivity, responseBanner.message).show()
                    }
                } else {
                    Utils.showSimpleMessage(this@TransferCoinsActivity, "No data found.").show()
                }
                tvSaveYTransfer.isEnabled = true
                pbProgressTransfer.visibility = View.GONE
            }

            override fun onFailure(msg: String?) {
                Utils.showSimpleMessage(this@TransferCoinsActivity, msg!!).show()
                tvSaveYTransfer.isEnabled = true
                pbProgressTransfer.visibility = View.GONE
            }
        })

    }

    private fun getUserInfo() {
        ServiceHelper().sendQrService(SendQrCodeRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), (etAmountTrans.text.toString()).toInt(), qrCode), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val responseBanner = response.body() as DisLikeResponse?
                if (responseBanner != null) {
                    Utils.showToast(this@TransferCoinsActivity, responseBanner.message)
                    onBackPressed()
                } else {
                    Utils.showSimpleMessage(this@TransferCoinsActivity, "No data found.").show()
                }
            }

            override fun onFailure(msg: String?) {
                Utils.showSimpleMessage(this@TransferCoinsActivity, msg!!).show()
            }
        })

    }

    private fun setAnimation() {
        ViewAnimator
                .animate(flParentTrans)
                .translationY(1000f, 0f)
                .duration(400)
                .start()
    }

    override fun onBackPressed() {
        ViewAnimator
                .animate(flParentTrans)
                .translationY(0f, 2000f)
                .duration(400)
                .andAnimate(llParentTrans)
                .alpha(1f, 0f)
                .duration(500)
                .onStop {
                    llParentTrans.visibility = View.GONE
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()
    }
}