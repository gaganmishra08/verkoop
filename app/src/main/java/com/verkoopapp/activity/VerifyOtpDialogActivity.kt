package com.verkoopapp.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import com.verkoopapp.R
import com.verkoopapp.models.VerifyNumberResponse
import com.verkoopapp.models.VerifyOtpRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.verify_otp_dialog.*
import retrofit2.Response
import android.app.Activity
import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.github.florent37.viewanimator.ViewAnimator
import com.verkoopapp.models.VerifyNumberRequest

class VerifyOtpDialogActivity : AppCompatActivity() {
    internal var number1 = ""
    internal var number2 = ""
    internal var number3 = ""
    internal var number4 = ""
    private var phoneNo: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_otp_dialog)
        phoneNo = intent.getStringExtra(AppConstants.PHONE_NO).toString()
        setFirstTextWatcher()
        setSecondTextWatcher()
        setThirdTextWatcher()
        setFouthTextWatcher()
        setData()
        setAnimation()
    }

    private fun setAnimation() {
        ViewAnimator
                .animate(flParentOtp)
                .translationY(1000f, 0f)
                .duration(400)
                .start()
    }

    private fun setData() {
//        pbProgressOtp.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        pbProgressOtp.indeterminateDrawable.setColorFilter(resources.getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        tvSubmitOtp.setOnClickListener {
            if (Utils.isOnline(this)) {
                tvSubmitOtp.isEnabled = false
                sumbitResult()
                Handler().postDelayed(Runnable {
                    tvSubmitOtp.isEnabled = true
                }, 1500)
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }

        }
        ivCloseDialog.setOnClickListener {
            onBackPressed()

        }

        tvResend.setOnClickListener {
            if (Utils.isOnline(this)) {
                tvResend.isEnabled = false
                verifyPhoneNoApi()
                Handler().postDelayed(Runnable {
                    tvResend.isEnabled = true
                }, 1500)
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }

    }

    private fun verifyPhoneNoApi() {
        pbProgressOtp.visibility = View.VISIBLE
        ServiceHelper().verifyMobileNo(VerifyNumberRequest(phoneNo), Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val loginResponse = response.body() as VerifyNumberResponse
                        pbProgressOtp.visibility = View.GONE
                        Utils.showSimpleMessage(this@VerifyOtpDialogActivity, "OTP resend successfully.").show()
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressOtp.visibility = View.GONE
                        Utils.showSimpleMessage(this@VerifyOtpDialogActivity, msg!!).show()
                    }
                })
    }

    private fun sumbitResult() {
        if (!number1.isEmpty() && !number2.isEmpty()
                && !number3.isEmpty() && !number4.isEmpty()) {
            val enteredOTP = (number1 + number2
                    + number3 + number4)
            if (Utils.isOnline(this)) {
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                otpVerificationService(enteredOTP)

            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }

        } else {
            etFirstNumber.setText("")
            etSecondNumber.setText("")
            etThirdNumber.setText("")
            etFourthNumber.setText("")
            etFirstNumber.requestFocus()
            Utils.showSimpleMessage(this, "please enter 4 digit pin no.").show()
        }
    }

    private fun otpVerificationService(enteredOTP: String) {
        pbProgressOtp.visibility = View.VISIBLE
        ServiceHelper().verifyOtpNo(VerifyOtpRequest(enteredOTP.toInt(), Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), phoneNo),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val loginResponse = response.body() as VerifyNumberResponse
                        Utils.showToast(this@VerifyOtpDialogActivity, "Phone number updated successfully.")
                        successPress()
                        pbProgressOtp.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressOtp.visibility = View.GONE
                        Utils.showSimpleMessage(this@VerifyOtpDialogActivity, msg!!).show()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                })

    }

    private fun successPress() {
        ViewAnimator
                .animate(flParentOtp)
                .translationY(0f, 2000f)
                .duration(400)
                .andAnimate(llParentOtp)
                .alpha(1f, 0f)
                .duration(500)
                .onStop {
                    llParentOtp.visibility = View.GONE
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.PHONE_NO, phoneNo)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()
    }

    private fun setFouthTextWatcher() {
        etFourthNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if ((s.length == 1) and (etFourthNumber.length() == 1)) {
                    number4 = s.toString()
                    etFourthNumber.clearFocus()
                    etFourthNumber.requestFocus()
                    etFourthNumber.setCursorVisible(true)
                } else {
                    number4 = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int,
                                           after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun setThirdTextWatcher() {
        etThirdNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if ((s.length == 1) and (etThirdNumber.length() == 1)) {
                    number3 = s.toString()
                    etThirdNumber.clearFocus()
                    etThirdNumber.requestFocus()
                    etThirdNumber.setCursorVisible(true)
                } else {
                    number3 = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    etFourthNumber.requestFocus()
                }
            }
        })
    }

    private fun setSecondTextWatcher() {
        etSecondNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if ((s.length == 1) and (etSecondNumber.length() == 1)) {
                    number2 = s.toString()
                    etSecondNumber.clearFocus()
                    etSecondNumber.requestFocus()
                    etSecondNumber.setCursorVisible(true)
                } else {
                    number2 = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    etThirdNumber.requestFocus()
                }
            }
        })
    }

    private fun setFirstTextWatcher() {
        etFirstNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if ((s.length == 1) and (etFirstNumber.length() == 1)) {
                    number1 = s.toString()
                    etFirstNumber.clearFocus()
                    etFirstNumber.requestFocus()
                    etFirstNumber.setCursorVisible(true)
                } else {
                    number1 = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    etSecondNumber.requestFocus()
                }
            }
        })
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }
}