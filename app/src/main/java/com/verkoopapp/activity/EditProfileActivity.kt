package com.verkoopapp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.verkoopapp.R
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.*
import kotlinx.android.synthetic.main.edit_profile_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class EditProfileActivity : AppCompatActivity() {
    private var uriTemp: Uri? = null
    private var mCurrentPhotoPath: String? = null
    private var cityName: String = ""
    private var stateName: String = ""
    private var countryName: String = ""
    private var gender: String = ""
    private var stateId: Int = 0
    private var cityId: Int = 0
    private var countryId: Int = 0
    private var country_Id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_activity)
        setData()
        if (Utils.isOnline(this)) {
            getProfileDataApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
        ccp.setOnCountryChangeListener {
            country_Id=ccp.selectedCountryNameCode

        }
    }

    private fun getProfileDataApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProfileProgress.visibility = View.VISIBLE
        ServiceHelper().getMyProfileInfoService(Utils.getPreferencesString(this, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProfileProgress.visibility = View.GONE
                val myProfileResponse = response.body() as MyProfileIngoResponse
                if (myProfileResponse.data != null) {
                    etUserName.setText(myProfileResponse.data.username)
                    etFirstName.setText(myProfileResponse.data.first_name)
                    etLastName.setText(myProfileResponse.data.last_name)
                    etWebsite.setText(myProfileResponse.data.website)
                    etBio.setText(myProfileResponse.data.bio)
                    if (myProfileResponse.data.city.isEmpty()){
                        etMyCity.setText(Utils.getPreferencesString(this@EditProfileActivity,AppConstants.CITY_NAME))
                    }
                    else{
                        etMyCity.setText(myProfileResponse.data.city)
                    }
                    ccp.setCountryForNameCode(myProfileResponse.data.country_code)
                    ccp.setCountryForNameCode(myProfileResponse.data.country_code)
                    country_Id=myProfileResponse.data.country_code
//                    Log.e("TAG", "onSuccesscountry_code: "+myProfileResponse.data.country_code)
                    if (!TextUtils.isEmpty(myProfileResponse.data.mobile_no)) {
                        tvPhoneNo.text = myProfileResponse.data.mobile_no
                        tvUpdate.text = "Change"
                    } else {
                        tvUpdate.text = "Update"
                    }

                    if (!TextUtils.isEmpty(myProfileResponse.data.DOB)) {
                        tvDate.text = myProfileResponse.data.DOB
                    }

                    when {
                        myProfileResponse.data.gender.equals("Male", ignoreCase = true) -> spinner1.setSelection(2)
                        myProfileResponse.data.gender.equals("Female", ignoreCase = true) -> spinner1.setSelection(1)
                        else -> spinner1.setSelection(0)
                    }
                    if (!TextUtils.isEmpty(myProfileResponse.data.profile_pic)) {
                        Picasso.with(this@EditProfileActivity).load(AppConstants.IMAGE_URL + myProfileResponse.data.profile_pic)
                                .resize(720, 720)
                                .centerInside()
                                .error(R.mipmap.gallery_place)
                                .placeholder(R.mipmap.gallery_place)
                                .into(ivProfileImage)
                    }
                    if (!myProfileResponse.data.city.isNullOrEmpty())
                        etMyCity.setText(myProfileResponse.data.city)

//                    if (!TextUtils.isEmpty(myProfileResponse.data.state) && !TextUtils.isEmpty(myProfileResponse.data.city)) {
//                        etMyCity.text = StringBuilder().append(myProfileResponse.data.state).append(", ").append(myProfileResponse.data.city)
//                        cityId = myProfileResponse.data.city_id
//                        stateId = myProfileResponse.data.state_id
//                        cityName = myProfileResponse.data.city
//                        stateName = myProfileResponse.data.state
//                        countryName = myProfileResponse.data.country
//                    }
                }
            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProfileProgress.visibility = View.GONE
                Utils.showSimpleMessage(this@EditProfileActivity, msg!!).show()
            }
        })
    }


    private fun setData() {
        etEmail.setText(Utils.getPreferencesString(this, AppConstants.USER_EMAIL_ID))
        ivRight.visibility = View.VISIBLE
        ivLeftLocation.setOnClickListener { onBackPressed() }
        ivRight.setOnClickListener {
            if (Utils.isOnline(this)) {
                KeyboardUtil.hideKeyboard(this)
                updateProfileData()
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
        tvHeaderLoc.text = getString(R.string.my_profile)
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Utils.hideKeyboardOnOutSideTouch(spinner1, this@EditProfileActivity)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Utils.hideKeyboardOnOutSideTouch(spinner1, this@EditProfileActivity)
                gender = when (position) {

                    1 -> "Female"
                    2 -> "Male"
                    else -> ""
                }
            }
        }
        tvDate.setOnClickListener {
            setDateDialog()
        }
        ivProfileImage.setOnClickListener {
            addProfileImage()
        }
        llCity.setOnClickListener {

            val intent = Intent(this, RegionActivity::class.java)
            intent.putExtra(AppConstants.CITY_ID, cityId)
            intent.putExtra(AppConstants.STATE_ID, stateId)
            intent.putExtra(AppConstants.COUNTRY_CODE, country_Id)
            startActivityForResult(intent, 3)
        }
        tvUpdate.setOnClickListener {
            tvUpdate.isEnabled = false
            val intent = Intent(this, VerifyNumberActivity::class.java)
            startActivityForResult(intent, VERIFY_OTP_RETURN)
            Handler().postDelayed(Runnable {
                tvUpdate.isEnabled = true
            }, 2000)
        }
    }

    private fun updateProfileData() {
        val updateCategoryRequest = ProfileUpdateRequest(
                Utils.getPreferencesString(this, AppConstants.USER_ID),
                if (etUserName.text?.toString() != null) etUserName.text.toString() else "",
                if (etFirstName.text?.toString() != null) etFirstName.text.toString() else "",
                if (etLastName.text?.toString() != null) etLastName.text.toString() else "",
                if (etMyCity.text?.toString() != null) etMyCity.text.toString() else "",
                 stateName, ccp.selectedCountryName, ccp.selectedCountryNameCode, cityId.toString(), stateId.toString(), countryId.toString(),
                if (etWebsite.text?.toString() != null) etWebsite.text.toString() else "",
                if (etBio.text?.toString() != null) etBio.text.toString() else "",
                if (mCurrentPhotoPath != null) mCurrentPhotoPath!! else "",
                if (tvPhoneNo.text?.toString() != null) tvPhoneNo.text.toString() else "",
                gender, tvDate.text.toString())
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProfileProgress.visibility = View.VISIBLE
        ServiceHelper().updateProfileService(updateCategoryRequest, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProfileProgress.visibility = View.GONE
                val homeDataResponse = response.body() as ProfileUpdateResponse
                if (homeDataResponse.data != null) {
                    if (!TextUtils.isEmpty(homeDataResponse.data.mobile_no)) {
                        Utils.savePreferencesString(this@EditProfileActivity, AppConstants.MOBILE_NO, homeDataResponse.data.mobile_no)
                    }
                }
//                Log.e("TAG", "onSuccess: "+ccp.selectedCountryNameCode)
                Utils.savePreferencesString(this@EditProfileActivity, AppConstants.CURRENCY, homeDataResponse.data!!.currency)
                Utils.savePreferencesString(this@EditProfileActivity, AppConstants.COUNTRY_ID, homeDataResponse.data!!.currency)
                Utils.savePreferencesString(this@EditProfileActivity, AppConstants.CURRENCY_SYMBOL, homeDataResponse.data!!.currency_symbol)
                Utils.savePreferencesString(this@EditProfileActivity, AppConstants.COUNTRY_CODE, ccp.selectedCountryNameCode)
                Utils.showToast(this@EditProfileActivity, getString(R.string.profile_updated))
                EventBus.getDefault().post(MessageEvent("update"))
                onBackPressed()
            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProfileProgress.visibility = View.GONE
                Utils.showSimpleMessage(this@EditProfileActivity, msg!!).show()
            }
        })

    }


    private fun addProfileImage() {
        if (checkPermission()) {
            openPopUp()
        }
    }

    private fun setDateDialog() {
        Utils.setDatePicker(this, object : Utils.CurrentDate {
            override fun getSelectedDate(date: String) {
                tvDate.text = date
            }
        })
    }

    private fun checkPermission(): Boolean {
        val permissionCheck = PermissionCheck(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck.checkGalleryPermission())
                return true
        } else
            return true
        return false
    }

    private fun openPopUp() {
        val shareDialog = SelectOptionDialog(this, object : SelectionOptionListener {
            override fun leaveClick(option: String) {
                if (option.equals(getString(R.string.camera), ignoreCase = true)) {
                    takePicture()
                } else if (option.equals(getString(R.string.gallery), ignoreCase = true)) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Pick From"), REQUEST_GET_PHOTO)
                }
            }
        })
        shareDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    val f = File(mCurrentPhotoPath!!)
                    uriTemp = FileProvider.getUriForFile(this, "com.verkoopapp" + ".provider", f)
                    CropImage.activity(uriTemp)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)
                }

                REQUEST_GET_PHOTO -> {
                    uriTemp = data?.data
                    CropImage.activity(uriTemp)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    mCurrentPhotoPath = Utils.getRealPathFromURI(this@EditProfileActivity, result.uri)
                    val file = File(mCurrentPhotoPath!!)
                    Picasso.with(this).load(file)
                            .resize(720, 720)
                            .centerInside()
                            .error(R.mipmap.gallery_place)
                            .placeholder(R.mipmap.gallery_place)
                            .into(ivProfileImage)
                }
            }
            if (requestCode === 3) {
                if (resultCode === Activity.RESULT_OK) {
                    cityName = data!!.getStringExtra(AppConstants.CITY_NAME).toString()
                    cityId = data.getIntExtra(AppConstants.CITY_ID, 0)
                    stateName = data.getStringExtra(AppConstants.STATE_NAME).toString()
                    stateId = data.getIntExtra(AppConstants.STATE_ID, 0)
                    countryId = data.getIntExtra(AppConstants.COUNTRY_ID, 0)
                    countryName = data.getStringExtra(AppConstants.COUNTRY_NAME).toString()
                    etMyCity.text = cityName
                    Utils.savePreferencesString(this@EditProfileActivity, AppConstants.CITY_NAME, cityName)
                    Utils.savePreferencesString(this@EditProfileActivity, AppConstants.STATE_NAME, stateName)
                }
                if (resultCode === Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }

            if (requestCode === VERIFY_OTP_RETURN) {
                if (resultCode === Activity.RESULT_OK) {
                    val phoneNo = data!!.getStringExtra(AppConstants.PHONE_NO)
                    tvPhoneNo.text = phoneNo
                    tvUpdate.text = "Change"
                    if (!TextUtils.isEmpty(phoneNo)) {
                        if (phoneNo != null) {
                            Utils.savePreferencesString(this@EditProfileActivity, AppConstants.MOBILE_NO, phoneNo)
                        }
                    }
                }
                if (resultCode === Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }
        }


    }

    companion object {
        internal const val REQUEST_TAKE_PHOTO = 1
        internal const val REQUEST_GET_PHOTO = 2
        internal const val VERIFY_OTP_RETURN = 4
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(this,
                                applicationContext.packageName + ".provider", photoFile))
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                "." +
                        "", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

}