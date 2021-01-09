package com.verkoopapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.github.florent37.viewanimator.ViewAnimator

import com.verkoopapp.R
import com.verkoopapp.models.AddMoneyRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.KeyboardUtil
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.add_money_dialog_activity.*
import retrofit2.Response
import android.app.Activity
import android.content.Intent
import com.verkoopapp.models.UpdateWalletResponse


class AddMoneyDialogActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_money_dialog_activity)
        KeyboardUtil(this, llParentAddMoney)
        setData()
        setAnimation()
    }
    private fun setAnimation() {
        ViewAnimator
                .animate(flParentAdd)
                .translationY(1000f, 0f)
                .duration(400)
                .start()
    }
    private fun setData() {
        currency.text = Utils.getPreferencesString(this@AddMoneyDialogActivity, AppConstants.CURRENCY_SYMBOL)
        pbProgressAddMoney.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        tvProceed.setOnClickListener {
            if (Utils.isOnline(this)) {
          if(!TextUtils.isEmpty(etAmount.text.toString())) {
              KeyboardUtil.hideKeyboard(this)
              val returnIntent = Intent()
              returnIntent.putExtra(AppConstants.INTENT_RESULT, "success")
              returnIntent.putExtra(AppConstants.AMOUNT,(etAmount.text.toString()).toLong())
              setResult(Activity.RESULT_OK, returnIntent)
              finish()
           //   callUpdateWalletApi()
          }
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
        }
        cancel.setOnClickListener { onBackPressed() }
    }

    private fun callUpdateWalletApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressAddMoney.visibility = View.VISIBLE
        ServiceHelper().addMoneyService(AddMoneyRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), (etAmount.text.toString()).toDouble(), "12345878632","USD"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressAddMoney.visibility = View.GONE
                        val loginResponse = response.body() as UpdateWalletResponse
                        Utils.showToast(this@AddMoneyDialogActivity,loginResponse.message)
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.INTENT_RESULT, "success")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                      //  onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressAddMoney.visibility = View.GONE
                        Utils.showSimpleMessage(this@AddMoneyDialogActivity, msg!!).show()
                    }
                })

    }



    override fun onBackPressed() {
        ViewAnimator
                .animate(flParentAdd)
                .translationY(0f, 2000f)
                .duration(400)
                .andAnimate(llParentAddMoney)
                .alpha(1f, 0f)
                .duration(500)
                .onStop {
                    llParentAddMoney.visibility = View.GONE
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_CANCELED, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()
    }
}