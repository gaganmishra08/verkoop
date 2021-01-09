package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.verkoopapp.R
import com.verkoopapp.models.VerifyNumberRequest
import com.verkoopapp.models.VerifyNumberResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.verify_number_activity.*
import retrofit2.Response
import android.app.Activity
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.forgot_password_activity.*


class VerifyNumberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_number_activity)
        ccpPhone.setCountryForPhoneCode(27)
        setData()
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener {
           finish()
        }
        tvHeaderLoc.text = getString(R.string.verify_phone_no)
        tvGetCode.setOnClickListener {
            if (isValidate()) {
                if (Utils.isOnline(this)) {
                    tvGetCode.isEnabled=false
                    verifyPhoneNoApi()
                    Handler().postDelayed(Runnable {
                        tvGetCode.isEnabled=true
                    },1500)
                } else {
                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                }

            }
        }
        etPhoneNo.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    vPhoneFor.setBackgroundColor(ContextCompat.getColor(this@VerifyNumberActivity, R.color.colorPrimary))
                } else {
                    vPhoneFor.setBackgroundColor(ContextCompat.getColor(this@VerifyNumberActivity, R.color.light_gray))
                }
            }
        })
    }

    private fun isValidate(): Boolean {
        return if (TextUtils.isEmpty(etPhoneNo.text.toString())) {
            Utils.showSimpleMessage(this, "Please enter phone number.").show()
            false
        } else if (etPhoneNo.text.toString().length < 10) {
            Utils.showSimpleMessage(this, "Please enter valid phone number.").show()
            false
        } else {
            true
        }
    }

    private fun verifyPhoneNoApi() {
        pbProgressPhone.visibility = View.VISIBLE
        ServiceHelper().verifyMobileNo(VerifyNumberRequest(ccpPhone.selectedCountryCodeWithPlus + etPhoneNo.text.toString().trim()), Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val loginResponse = response.body() as VerifyNumberResponse
                        pbProgressPhone.visibility = View.GONE
                        val intent = Intent(this@VerifyNumberActivity, VerifyOtpDialogActivity::class.java)
                            intent.putExtra(AppConstants.PHONE_NO,ccpPhone.selectedCountryCodeWithPlus + etPhoneNo.text.toString().trim())
                        startActivityForResult(intent, 2)
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressPhone.visibility = View.GONE
                        Utils.showSimpleMessage(this@VerifyNumberActivity, msg!!).show()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra(AppConstants.PHONE_NO)
                val returnIntent = Intent()
                returnIntent.putExtra(AppConstants.PHONE_NO,result)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
                overridePendingTransition(0, 0)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}