package com.verkoopapp.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.models.DisLikeResponse
import com.verkoopapp.models.SignUpRequest
import com.verkoopapp.models.SignUpResponse
import com.verkoopapp.models.UpdateDeviceInfoRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.signup_activity.*
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {
    val TAG = SignUpActivity::class.java.simpleName.toString()
    private var id = 0
    private var type = 0
    private var deviceId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        type = intent.getIntExtra(AppConstants.TYPE, 0)
        id = intent.getIntExtra(AppConstants.ID, 0)
        printHashKey(applicationContext)

        back_button.setOnClickListener {
            onBackPressed()
        }

        setData()
        ccp.setCountryForPhoneCode(1)
        firebaseDeviceToken()
    }

    private fun firebaseDeviceToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            deviceId = it.token
            Log.e("newToken", deviceId)
        }
    }

    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.e(TAG, "printHashKey: " + hashKey)
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }

    private fun setData() {


        val typefaceFont = Typeface.createFromAsset(assets, "fonts/Poppins-Regular.ttf")
        etPasswordS.typeface = typefaceFont
        etConfPassword.typeface = typefaceFont
        ccp.setTypeFace(typefaceFont)
        tvLoginS.setOnClickListener { onBackPressed() }
        tvSignUpS.setOnClickListener {
            if (Utils.isOnline(this)) {
                if (isValidate()) {
                    callSignUpApi()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
        etName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivName.setImageResource(R.mipmap.username_enable)
                } else {
                    ivName.setImageResource(R.mipmap.username_disable)
                }
            }
        })
        etEmailS.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivEmailS.setImageResource(R.mipmap.email_enable)
                } else {
                    ivEmailS.setImageResource(R.mipmap.email_disable)
                }
            }
        })
        etPasswordS.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivPasswordS.setImageResource(R.mipmap.password_enable)
                } else {
                    ivPasswordS.setImageResource(R.mipmap.password_disable)
                }
            }
        })
        etConfPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivConfPassword.setImageResource(R.mipmap.password_enable)
                } else {
                    ivConfPassword.setImageResource(R.mipmap.password_disable)
                }
            }
        })

        val filter = object : InputFilter {
            var canEnterSpace = false

            override fun filter(source: CharSequence, start: Int, end: Int,
                                dest: Spanned, dstart: Int, dend: Int): CharSequence {

                if (etName.text.toString() == "") {
                    canEnterSpace = false
                }

                val builder = StringBuilder()

                for (i in start until end) {
                    val currentChar = source[i]
                    if (!Character.isWhitespace(currentChar)) {
                        builder.append(currentChar)
                    }
                }
                return builder.toString()
            }
        }
        etName.filters = arrayOf<InputFilter>(filter)
    }

    private fun isValidate(): Boolean {
        return if (TextUtils.isEmpty(etName.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_user_name)).show()
            false
        } else if (TextUtils.isEmpty(etEmailS.text.toString())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_email)).show()
            false
        } else if (!Utils.emailValidator(etEmailS.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_valid_email)).show()
            false
        } else if (TextUtils.isEmpty(etPasswordS.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password)).show()
            false
        } else if (etPasswordS.text.toString().trim().length < 6) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password_length)).show()
            false
        } else if (!isValidPassword(etPasswordS.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password_combination)).show()
            false
        }
        else if (TextUtils.isEmpty(etConfPassword.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_con_password)).show()
            false
        } else if (etPasswordS.text.toString().trim() != etConfPassword.text.toString().trim()) {
            Utils.showSimpleMessage(this, getString(R.string.match_password)).show()
            false
        } else {
            true
        }
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun callSignUpApi() {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().userSignUPService(SignUpRequest(etEmailS.text.toString().trim(), etName.text.toString().trim(), etConfPassword.text.toString().trim(), "normal", ccp.selectedCountryName, ccp.selectedCountryNameCode, "1"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            VerkoopApplication.instance.loader.hide(this@SignUpActivity)
                            val loginResponse = response.body() as SignUpResponse
                            if (loginResponse.data != null) {

                                setResponseData(loginResponse.data.userId.toString(), loginResponse.data.token, loginResponse.data.username, loginResponse.data.email, loginResponse.data.login_type, loginResponse.data.qrCode_image, loginResponse.data.coin, loginResponse.data.amount, loginResponse.data.currency, loginResponse.data.currency_symbol)
                                updateDeviceInfo()
                            }
                        } catch (e: Exception) {
                        }
                    }

                    override fun onFailure(msg: String?) {
                        try {
                            VerkoopApplication.instance.loader.hide(this@SignUpActivity)
                            Utils.showSimpleMessage(this@SignUpActivity, msg!!).show()
                        } catch (e: Exception) {
                        }
                    }
                })
    }

    private fun updateDeviceInfo() {
        if (Utils.isOnline(this)) {
            callUpdateDeviceInfoApi()
        } else {
            updateDeviceInfo()
        }
    }

    private fun callUpdateDeviceInfoApi() {
        ServiceHelper().updateDeviceInfo(UpdateDeviceInfoRequest(Utils.getPreferences(this@SignUpActivity, AppConstants.USER_ID), deviceId, "1"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@SignUpActivity)
                        val loginResponse = response.body() as DisLikeResponse
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@SignUpActivity)
                        Utils.showSimpleMessage(this@SignUpActivity, msg!!).show()
                    }
                })
    }

    private fun setResponseData(userId: String, api_token: String, firstName: String, email: String, loginType: String, qrCode: String, coin: Int, amount: Int, currency: String, currency_symbol: String) {
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.USER_ID, userId)
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.API_TOKEN, "Bearer $api_token")
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.USER_NAME, firstName)
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.QR_CODE, qrCode)
        Utils.saveIntPreferences(this@SignUpActivity, AppConstants.COIN, coin)
        Utils.saveIntPreferences(this@SignUpActivity, AppConstants.AMOUNT, amount)
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.CURRENCY, currency)
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.COUNTRY_CODE, ccp.selectedCountryNameCode)
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.CURRENCY_SYMBOL, currency_symbol)
        if (!TextUtils.isEmpty(email)) {
            Utils.savePreferencesString(this@SignUpActivity, AppConstants.USER_EMAIL_ID, email)
        }
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.LOGIN_TYPE, loginType)
        val intent = Intent(this@SignUpActivity, CategoriesActivity::class.java)
        intent.putExtra(AppConstants.ID, id)
        intent.putExtra(AppConstants.TYPE, type)
        startActivity(intent)
        finishAffinity()
    }
}
