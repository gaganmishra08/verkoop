package com.verkoopapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.github.florent37.viewanimator.ViewAnimator
import com.verkoopapp.R
import com.verkoopapp.models.DisLikeResponse
import com.verkoopapp.models.UpdatePasswordRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.KeyboardUtil
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.change_password.*
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var ivLeftLocation: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)
        ivLeftLocation=findViewById(R.id.ivLeftLocation)
//        KeyboardUtil(this, llParentPass)
        setData()
    }

    private fun setData() {
        pbProgressChange.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        tvSavePass.setOnClickListener {
            if (Utils.isOnline(this)) {
                //   Utils.hideKeyboardOnOutSideTouch(tvSavePass, this@ChangePasswordActivity)
                if (isValidate()) {
                    KeyboardUtil.hideKeyboard(this)
                    callChangePasswordApi()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
    }

    private fun callChangePasswordApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressChange.visibility = View.VISIBLE
        ServiceHelper().updatePasswordService(UpdatePasswordRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), etCurrentPassword.text.toString(), etNewPassword.text.toString()),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressChange.visibility = View.GONE
                        val loginResponse = response.body() as DisLikeResponse
                        Utils.showToast(this@ChangePasswordActivity, getString(R.string.changed))
                        onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressChange.visibility = View.GONE
                        Utils.showSimpleMessage(this@ChangePasswordActivity, msg!!).show()
                    }
                })

    }



    private fun isValidate(): Boolean {
        return when {
            TextUtils.isEmpty(etCurrentPassword.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter current password.").show()
                false
            }
            etCurrentPassword.text.toString().trim().length < 6 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_password_length)).show()
                false
            }
            TextUtils.isEmpty(etNewPassword.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter new password.").show()
                false
            }
            etNewPassword.text.toString().trim().length < 6 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_password_length)).show()
                false
            }
            TextUtils.isEmpty(etConfirmPassword.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter confirm password.").show()
                false
            }
            etNewPassword.text.toString().trim() != etConfirmPassword.text.toString().trim() -> {
                Utils.showSimpleMessage(this, getString(R.string.match_password)).show()
                false
            }
            else -> true
        }
    }

}