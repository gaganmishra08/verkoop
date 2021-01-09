package com.verkoopapp.activity

import android.content.Intent
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.iid.FirebaseInstanceId
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.*
import kotlinx.android.synthetic.main.dialog_update_country.*
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.login_activity.back_button
import kotlinx.android.synthetic.main.signup_activity.*
import org.json.JSONException
import retrofit2.Response
import java.util.*


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val TAG = MainActivity::class.java.simpleName
    private val RC_SIGN_IN = 7
    private var mGoogleApiClient: GoogleSignInClient? = null
    private var callbackManager: CallbackManager? = null
    private var fbAccessToken: AccessToken? = null
    private var url: String? = null
    private var loginType: String = ""
    private var deviceId: String = ""
    private var countryName: String = ""
    private var id = 0
    private var type = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        back_button.setOnClickListener {
            onBackPressed()
        }
        type = intent.getIntExtra(AppConstants.TYPE, 0)
        id = intent.getIntExtra(AppConstants.ID, 0)
        setData()
        googleLoginSetUp()
        firebaseDeviceToken()


//        CommonUtils.printKeyHash(this)
       // YIoi6SUsG3ukhAjn77nu1JgKvKE=
//        0a:d8:a0:75:f1:dc:09:98:1e:cc:05:9a:36:cc:7c:54:6f:7f:4f:0c
//         val sha1KeyHash = byteArrayOf(0x0a.toByte(), 0xd8.toByte(), 0xa0.toByte(), 0x75.toByte(), 0xf1.toByte(), 0xdc.toByte(), 0x09.toByte(),
//                 0x98.toByte(), 0x1e.toByte(), 0xcc.toByte(), 0x05.toByte(), 0x9a.toByte(), 0x36.toByte(),
//                 0xcc.toByte(), 0x7c.toByte(), 0x54.toByte(), 0x6f.toByte(), 0x7f.toByte(), 0x4f.toByte(), 0x0c.toByte())
//        println("keyHashForFacebookLogin New: " + android.util.Base64.encodeToString(sha1KeyHash, android.util.Base64.NO_WRAP))
    }

    override fun onResume() {
        super.onResume()
        runOnUiThread {
            try {
                if (checkPermission()){

                    getgpslocation()
                }
                else{
                    checkPermission()
                }
            }
            catch (exc:Exception){
                Log.e(TAG, "onResume: "+exc.message)
            }


        }

    }

    private fun checkPermission(): Boolean {
        val permissionCheck = PermissionCheck(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck.checkLocationPermission())
                return true
        } else
            return true
        return false
    }

    private fun getgpslocation() {
        val gpsTracker = GPSTracker(this@LoginActivity)

        if (gpsTracker.canGetLocation()) {
            val latitude: Double = gpsTracker.location.latitude
            val longitude: Double = gpsTracker.location.longitude
            val geocoder = Geocoder(this@LoginActivity, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            val cityName: String = addresses[0].getLocality()
            val stateName: String = addresses[0].getAdminArea()
            countryName= addresses[0].countryCode
            Log.e(TAG, "getgpslocation: "+countryName)
            Utils.savePreferencesString(this@LoginActivity, AppConstants.CITY_NAME, cityName)
            Utils.savePreferencesString(this@LoginActivity, AppConstants.STATE_NAME, stateName)
            Utils.savePreferencesString(this@LoginActivity, AppConstants.COUNTRY_CODE, countryName)
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
//                                    gpsTracker.showSettingsAlert()
        }

    }

    private fun firebaseDeviceToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            deviceId = it.token
            Log.e("newToken", deviceId)
        }
    }

    private fun googleLoginSetUp() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

//        mGoogleApiClient = GoogleApiClient.Builder(this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build()
        mGoogleApiClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setData() {
        val typefaceFont = Typeface.createFromAsset(assets, "fonts/Poppins-Regular.ttf")
        etPassword.typeface = typefaceFont

        tvLogin.setOnClickListener {
            if (Utils.isOnline(this)) {
                if (isValidate()) {
                    if (Utils.isOnline(this)) {
                        callLoginApi()
                    } else {
                        Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                    }

                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
        tvFacebook.setOnClickListener {
            tvFacebook.isEnabled = false
            Handler().postDelayed(Runnable {
                tvFacebook.isEnabled = true
            }, 700)
            loginType = getString(R.string.face_log)
            if (Utils.isOnline(this)) {
                loginWithFacebook()
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()

            }
        }
        tvGoogle.setOnClickListener {
            tvGoogle.isEnabled = false
            Handler().postDelayed(Runnable {
                tvGoogle.isEnabled = true
            }, 700)
            loginType = getString(R.string.google_plus)
            if (Utils.isOnline(this)) {
//                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                val signInIntent = mGoogleApiClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            startActivity(intent)
        }
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivEmail.setImageResource(R.mipmap.email_enable)
                } else {
                    ivEmail.setImageResource(R.mipmap.email_disable)
                }
            }
        })
        etPassword.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivPassword.setImageResource(R.mipmap.password_enable)
                } else {
                    ivPassword.setImageResource(R.mipmap.password_disable)
                }
            }
        })
    }

    private fun isValidate(): Boolean {
        return if (TextUtils.isEmpty(etEmail.text.toString())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_email)).show()
            false
        } else if (!Utils.emailValidator(etEmail.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_valid_email)).show()
            false
        } else if (!Utils.isValidPassword(etPassword.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password)).show()
            false
        } else if (etPassword.text.toString().trim().length < 6) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password_length)).show()
            false
        } else {
            true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (loginType.equals(getString(R.string.face_log), ignoreCase = true)) {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        } else {
            if (requestCode == RC_SIGN_IN) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (deviceId.equals("")) {
                    firebaseDeviceToken()
                }
                if (result != null) {
                    handleSignInResult(result)
                }
            }
        }
    }

    private fun showCountryDialog(data: DataGoogle) {
        val shareDialog = countryDialog(this, object : CountryListener {
            override fun onItemClick(code: String, name: String) {
                callUpdateCountryApi(code, name, data)
            }
        })
        shareDialog.show()
    }

    private fun callUpdateCountryApi(code: String, name: String, data: DataGoogle) {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().updateCountry(UpdateCountryRequest(data.userId.toString(), code,name),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        val ciuntryResponse = response.body() as UpdateCountryResponse
                        setResponseData(data.userId.toString(), data.token, data.username, data.email, data.login_type, data.is_use, "", data.qrCode_image, data.coin, data.amount, ciuntryResponse.currency, ciuntryResponse.currency_symbol)
                        updateDeviceInfo()
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        Utils.showSimpleMessage(this@LoginActivity, msg!!).show()
                        updateDeviceInfo()
                    }
                })
    }

    /*Login with Facebook*/
    private fun loginWithFacebook() {
        if (deviceId.equals("")) {
            firebaseDeviceToken()
        }
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                fbAccessToken = loginResult.accessToken
                if (fbAccessToken != null) {
                    val request = GraphRequest.newMeRequest(fbAccessToken) { `object`, response ->
                        val facebookId = response.jsonObject.optString("id")
                        val imageUrl = "https://graph.facebook.com/$facebookId/picture?type=large&redirect=true&width=800&height=800"
                        val email = response.jsonObject.optString("email")
                        val name = response.jsonObject.optString("name")
                        val first_name = response.jsonObject.optString("first_name")
                        val last_name = response.jsonObject.optString("last_name")

                        try {
                            url = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
//                            Log.e(TAG, ":::profile pic url:::$url")
                            LoginManager.getInstance().logOut()
//                            if (!TextUtils.isEmpty(facebookId) && !TextUtils.isEmpty(email)) {
                            if (!TextUtils.isEmpty(facebookId)) {
                                if (Utils.isOnline(this@LoginActivity)) {
//                                    callSocialLoginApi(email, name, facebookId)
                                    callSocialLoginApi(email, first_name, last_name, facebookId)
                                } else {
                                    Utils.showSimpleMessage(this@LoginActivity, getString(R.string.check_internet)).show()
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,first_name,last_name,name,gender,email,picture,friends")
                    request.parameters = parameters
                    request.executeAsync()
                }
                /*  Intent intent= new Intent(context,SignUpActivity.class);
                startActivity(intent);*/
            }

            override fun onCancel() {
                //  Toast.makeText(getApplication(), "Cancelled", Toast.LENGTH_SHORT).show();
            }

            override fun onError(e: FacebookException) {
                Toast.makeText(application, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /*Login with GooglePlus*/
    private fun handleSignInResult(result: GoogleSignInResult) {
        //If the login succeed
        if (result.isSuccess) {
            val acct = result.signInAccount
            var fullName = ""
            var email = ""
            if (!TextUtils.isEmpty(acct!!.displayName)) {
                fullName = acct.displayName!!
            }
            if (!TextUtils.isEmpty(acct.email)) {
                email = acct.email!!
            }
            if (!TextUtils.isEmpty(acct.id) && !TextUtils.isEmpty(acct.email)) {
                if (Utils.isOnline(this@LoginActivity)) {

                    callSocialLoginApi(email, fullName, "", acct.id.toString())
                } else {
                    Utils.showSimpleMessage(this@LoginActivity, getString(R.string.check_internet)).show()
                }

            }
            Handler().postDelayed(Runnable {
                //                mGoogleApiClient!!.maybeSignOut()
//                mGoogleApiClient!!.disconnect()
                mGoogleApiClient!!.signOut()
            }, 1000)

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    private fun callLoginApi() {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().userLoginService(LoginRequest(etEmail.text.toString().trim(), etPassword.text.toString().trim(), "normal", "1"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        val loginResponse = response.body() as LogInResponse
                        if (loginResponse.data != null) {
                            setResponseData(loginResponse.data.userId.toString(), loginResponse.data.token, loginResponse.data.username, loginResponse.data.email, loginResponse.data.login_type, loginResponse.data.is_use, loginResponse.data.mobile_no, loginResponse.data.qrCode_image, loginResponse.data.coin, loginResponse.data.amount, loginResponse.data.currency, loginResponse.data.currency_symbol)
                            updateDeviceInfo()
                        } else {
                            Utils.showSimpleMessage(this@LoginActivity, loginResponse.message).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        Utils.showSimpleMessage(this@LoginActivity, msg!!).show()
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
//        VerkoopApplication.instance.loader.hide(this@LoginActivity)
        ServiceHelper().updateDeviceInfo(UpdateDeviceInfoRequest(Utils.getPreferences(this@LoginActivity, AppConstants.USER_ID), deviceId, "1"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
//                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        val loginResponse = response.body() as DisLikeResponse
                    }

                    override fun onFailure(msg: String?) {
//                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        Utils.showSimpleMessage(this@LoginActivity, msg!!).show()
                    }
                })
    }

    private fun setResponseData(userId: String, api_token: String, firstName: String, email: String, loginType: String, firstTime: Int, mobileNo: String, qr_code: String, coin: Int, amount: Float, currency: String, currency_symbol: String) {
        Utils.savePreferencesString(this@LoginActivity, AppConstants.USER_ID, userId)
        Utils.savePreferencesString(this@LoginActivity, AppConstants.API_TOKEN, "Bearer $api_token")
        Utils.savePreferencesString(this@LoginActivity, AppConstants.USER_NAME, firstName)
        Utils.savePreferencesString(this@LoginActivity, AppConstants.QR_CODE, qr_code)
        Utils.saveIntPreferences(this@LoginActivity, AppConstants.COIN, coin)
        Utils.saveFloatPreferences(this@LoginActivity, AppConstants.AMOUNT, amount)
        Utils.savePreferencesString(this@LoginActivity, AppConstants.CURRENCY, currency)
        Utils.savePreferencesString(this@LoginActivity, AppConstants.CURRENCY_SYMBOL, currency_symbol)
        Utils.savePreferencesString(this@LoginActivity, AppConstants.COUNTRY_ID, currency_symbol)
//        Utils.savePreferencesString(this@LoginActivity, AppConstants.COUNTRY_CODE, ccp.selectedCountryNameCode)

        if (!TextUtils.isEmpty(mobileNo)) {
            Utils.savePreferencesString(this@LoginActivity, AppConstants.MOBILE_NO, mobileNo)
        }
        if (!TextUtils.isEmpty(email)) {
            Utils.savePreferencesString(this@LoginActivity, AppConstants.USER_EMAIL_ID, email)
        }
        Utils.savePreferencesString(this@LoginActivity, AppConstants.LOGIN_TYPE, loginType)
        if (firstTime == 0) {
            val intent = Intent(this@LoginActivity, CategoriesActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            startActivity(intent)
            finish()
        } else {
            Utils.savePreferencesString(this, AppConstants.COMING_FROM, "PickOptionActivity")
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            startActivity(intent)
            finish()
        }
    }


//    private fun callSocialLoginApi(email: String, name: String, loginId: String) {
//        VerkoopApplication.instance.loader.show(this)
//        if (email == "") {
//            email.equals(" ")
//        }
//        ServiceHelper().socialLoginService(LoginSocialRequest(name, email, loginId, "social"),
//                object : ServiceHelper.OnResponse {
//                    override fun onSuccess(response: Response<*>) {
//                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
//                        val loginResponse = response.body() as SocialGoogleResponse
//                        Log.e("<<Log>>", "Login Successfully.")
//                        if (loginResponse.data != null) {
//                            setResponseData(loginResponse.data.userId.toString(), loginResponse.data.token, loginResponse.data.username, loginResponse.data.email, loginResponse.data.login_type, loginResponse.data.is_use, "", loginResponse.data.qrCode_image, loginResponse.data.coin, loginResponse.data.amount)
//                        }
//                    }
//
//                    override fun onFailure(msg: String?) {
//                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
//                        Utils.showSimpleMessage(this@LoginActivity, msg!!).show()
//                    }
//                })
//    }


    private fun
            callSocialLoginApi(email: String, user_name: String, last_name: String, loginId: String) {
        VerkoopApplication.instance.loader.show(this)
//        if (email == "") {
//            email.equals(" ")
//        }
        ServiceHelper().socialLoginService(LoginSocialRequest(user_name, email, loginId, "social", "1"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        val loginResponse = response.body() as SocialGoogleResponse
                        Log.e("<<Log>>", "Login Successfully.")
                        if (loginResponse.data != null) {

                            if (loginResponse.data.currency.isNullOrEmpty() || loginResponse.data.currency_symbol.isNullOrEmpty()) {
                                VerkoopApplication.instance.loader.hide(this@LoginActivity)
                                showCountryDialog(loginResponse.data)
                            } else {
                                setResponseData(loginResponse.data.userId.toString(), loginResponse.data.token, loginResponse.data.username, loginResponse.data.email, loginResponse.data.login_type, loginResponse.data.is_use, "", loginResponse.data.qrCode_image, loginResponse.data.coin, loginResponse.data.amount, loginResponse.data.currency, loginResponse.data.currency_symbol)
                                updateDeviceInfo()
                            }
                        }
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@LoginActivity)
                        Utils.showSimpleMessage(this@LoginActivity, msg!!).show()
                    }
                })
    }

}