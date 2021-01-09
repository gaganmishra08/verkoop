package com.verkoopapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.verkoopapp.R
import com.verkoopapp.models.AddItemResponse
import com.verkoopapp.models.ForgotPasswordRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.forgot_password_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_activity)
        setData()
    }

    private fun setData() {
        tvHeaderLoc.text=getString(R.string.forgot_pass)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        etEmailFor.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivEmailFor.setImageResource(R.mipmap.email_enable)
                    vEmailFor.setBackgroundColor(ContextCompat.getColor(this@ForgotPasswordActivity, R.color.colorPrimary))
                } else {
                    ivEmailFor.setImageResource(R.mipmap.email_disable)
                    vEmailFor.setBackgroundColor(ContextCompat.getColor(this@ForgotPasswordActivity, R.color.light_gray))
                }
            }
        })

        tvSent.setOnClickListener {
            if (Utils.isOnline(this)) {
                if (isValidate()) {
                    callForgotPasswordApi()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
    }

    private fun callForgotPasswordApi() {
        pbProgressFor.visibility= View.VISIBLE
        ServiceHelper().forgotPasswordService(ForgotPasswordRequest(etEmailFor.text.toString().trim()),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressFor.visibility= View.GONE
                        val loginResponse = response.body() as AddItemResponse
                        Utils.showToast(this@ForgotPasswordActivity,loginResponse.message)
                           onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressFor.visibility= View.GONE
                        Utils.showSimpleMessage(this@ForgotPasswordActivity, msg!!).show()
                    }
                })
    }

    private fun isValidate(): Boolean {
        return if (TextUtils.isEmpty(etEmailFor.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_email)).show()
            false
        } else if (!Utils.emailValidator(etEmailFor.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_valid_email)).show()
            false
        } else {
            true
        }
    }
}