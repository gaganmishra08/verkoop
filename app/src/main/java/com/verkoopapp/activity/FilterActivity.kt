package com.verkoopapp.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.verkoopapp.R
import com.verkoopapp.adapter.FilterAdapter
import com.verkoopapp.models.FilterRequest
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.PermissionCheck
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.add_details_activity.*
import kotlinx.android.synthetic.main.filter_activity.*
import kotlinx.android.synthetic.main.toolbar_filter.*


class FilterActivity : AppCompatActivity() {
    private var sortNumber = 0
    private val REQUEST_CODE = 12
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 50000
    private val FASTEST_INTERVAL: Long = 1000
    private lateinit var mLocationRequest: LocationRequest
    private var lat = ""
    private var lng = ""
    private var condition: String = ""
    private var name: String = ""
    private var itemType: String = ""
    private var meetUp: Int = 0
    private var isFocusMax: Boolean = false
    private var isFocus: Boolean = false
    private var filterRequest: FilterRequest? = null
    private lateinit var filterAdapter: FilterAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_activity)
        setItemList()
        setIntentData()
        setData()

    }

    private fun setIntentData() {
        filterRequest = intent.getParcelableExtra(AppConstants.POST_DATA)
        if (filterRequest != null) {
            if (!TextUtils.isEmpty(filterRequest!!.latitude)) {
                lat = filterRequest!!.latitude
            }
            if (!TextUtils.isEmpty(filterRequest!!.longitude)) {
                lng = filterRequest!!.longitude
            }
            if (!TextUtils.isEmpty(filterRequest!!.min_price)) {
                etMinPrice.setText(StringBuilder().append(Utils.getPreferencesString(this@FilterActivity,AppConstants.CURRENCY_SYMBOL)).append(filterRequest!!.min_price))
            }
            if (!TextUtils.isEmpty(filterRequest!!.max_price)) {
                etMaxPrice.setText(StringBuilder().append(Utils.getPreferencesString(this@FilterActivity,AppConstants.CURRENCY_SYMBOL)).append(filterRequest!!.max_price))
            }
            if (!TextUtils.isEmpty(filterRequest!!.meet_up)) {
                if (filterRequest!!.meet_up.equals("1", ignoreCase = true)) {
                    meetUp = 1
                    cbNearByFilter.isChecked = true
                } else {
                    meetUp = 0
                    cbNearByFilter.isChecked = false
                }
            } else {
                meetUp = 0
                cbNearByFilter.isChecked = false
            }
            if (!TextUtils.isEmpty(filterRequest!!.item_type)) {
                when {
                    filterRequest!!.item_type.equals("1", ignoreCase = true) -> setSelectNew()
                    filterRequest!!.item_type.equals("2", ignoreCase = true) -> setSelectUsed()
                    else -> {
                        setSelectionCondition()
                    }

                }
            } else {
                setSelectionCondition()
            }
            if (!TextUtils.isEmpty(filterRequest!!.sort_no)) {
                when {
                    filterRequest!!.sort_no.equals("1", ignoreCase = true) -> {
                        sortNumber = 1
                        rbNearBy.isChecked = true
                    }
                    filterRequest!!.sort_no.equals("2", ignoreCase = true) -> {
                        sortNumber = 2
                        rbPopular.isChecked = true
                    }
                    filterRequest!!.sort_no.equals("3", ignoreCase = true) -> {
                        sortNumber = 3
                        rbRecentlyAdded.isChecked = true
                    }
                    filterRequest!!.sort_no.equals("4", ignoreCase = true) -> {
                        sortNumber = 4
                        rbPriceHigh.isChecked = true
                    }
                    filterRequest!!.sort_no.equals("5", ignoreCase = true) -> {
                        sortNumber = 5
                        rbPriceLow.isChecked = true
                    }
                    else -> {
                        sortNumber = 2
                        rbPopular.isChecked = true
                    }
                }
            } else {
                sortNumber = 2
                rbPopular.isChecked = true
            }
        }
    }

    private fun setData() {
        val font = Typeface.createFromAsset(assets, "fonts/gothic.ttf")
        rbNearBy.typeface = font
        rbPopular.typeface = font
        rbRecentlyAdded.typeface = font
        rbPriceHigh.typeface = font
        rbPriceLow.typeface = font
        iv_leftGallery.setOnClickListener { onBackPressed() }
        llNew.setOnClickListener {
            if (TextUtils.isEmpty(condition)) {
                setSelectionCondition()
                setSelectNew()
            } else {
                if (condition.equals("New", ignoreCase = true)) {
                    setSelectionCondition()
                } else {
                    setSelectionCondition()
                    setSelectNew()
                }
            }
        }

        llUsed.setOnClickListener {
            if (TextUtils.isEmpty(condition)) {
                setSelectionCondition()
                setSelectUsed()
            } else {
                if (condition.equals(getString(R.string.used), ignoreCase = true)) {
                    setSelectionCondition()
                } else {
                    setSelectionCondition()
                    setSelectUsed()
                }
            }

        }
        cbNearByFilter.setOnCheckedChangeListener { _, isChecked ->
            meetUp = if (isChecked) {
                1

            } else {
                0

            }
        }
        rbGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbNearBy -> {
                    checkLocationOption()
                    sortNumber = 1
                }
                R.id.rbPopular -> {
                    sortNumber = 2
                }
                R.id.rbRecentlyAdded -> {
                    sortNumber = 3
                }
                R.id.rbPriceHigh -> {
                    sortNumber = 4
                }
                R.id.rbPriceLow -> {
                    sortNumber = 5
                }
            }
        }
        etMinPrice.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocus = true
                if (etMinPrice.text.toString().isEmpty()) {
                    etMinPrice.setText(Utils.getPreferencesString(this@FilterActivity,AppConstants.CURRENCY_SYMBOL))
                    etMinPrice.setSelection(1)
                }
            } else {
                isFocus = false
                if (etMinPrice.length() == 1) {
                    etMinPrice.setSelection(0)
                    etMinPrice.setText("")
                }
            }
        }
        etMinPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (isFocus) {
                    if (etMinPrice.length() == 0) {
                        etMinPrice.setText(Utils.getPreferencesString(this@FilterActivity,AppConstants.CURRENCY_SYMBOL))
                        etMinPrice.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {

            }
        })
        etMaxPrice.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocusMax = true
                if (etMaxPrice.text.toString().isEmpty()) {
                    etMaxPrice.setText(Utils.getPreferencesString(this@FilterActivity,AppConstants.CURRENCY_SYMBOL))
                    etMaxPrice.setSelection(1)
                }
            } else {
                isFocusMax = false
                if (etMaxPrice.length() == 1) {

                    etMaxPrice.setSelection(0)
                    etMaxPrice.setText("")
                }
            }
        }
        etMaxPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (isFocusMax) {
                    if (etMaxPrice.length() == 0) {
                        etMaxPrice.setText(Utils.getPreferencesString(this@FilterActivity,AppConstants.CURRENCY_SYMBOL))
                        etMaxPrice.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {

            }
        })
        tvChatGallery.setOnClickListener {
            resetData()
        }
        tvApply.setOnClickListener {
            itemType = when {
                condition.equals("New", ignoreCase = true) -> "1"
                condition.equals(getString(R.string.used), ignoreCase = true) -> "2"
                else -> ""
            }
            val filterRequestSend = FilterRequest(filterRequest!!.category_id, filterRequest!!.type, filterRequest!!.userId, sortNumber.toString(), lat, lng, itemType, meetUp.toString(),etMinPrice.text.toString().replace(Utils.getPreferencesString(this@FilterActivity,AppConstants.CURRENCY_SYMBOL),""), etMaxPrice.text.toString().replace(Utils.getPreferencesString(this@FilterActivity,AppConstants.CURRENCY_SYMBOL),""),filterRequest!!.search, filterRequest!!.country_code)
            val returnIntent = Intent()
            returnIntent.putExtra(AppConstants.POST_DATA, filterRequestSend)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
       /* val filterModal = FilterModal("Sort :", "Popular", false, 1)
        filterList.add(filterModal)
        filterAdapter.showData(filterList)*/

    }

    private fun resetData() {
        rbPopular.isChecked = true
        sortNumber = 2
        setSelectionCondition()
        meetUp = 0
        cbNearByFilter.isChecked = false
        isFocus = false
        isFocusMax = false
        etMaxPrice.setSelection(0)
        etMaxPrice.setText("")
        etMinPrice.setSelection(0)
        etMinPrice.setText("")
        etMinPrice.clearFocus()
        etMaxPrice.clearFocus()
    }

    private fun setSelectNew() {
        ivNew.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_new_item_white))
        tvNew.setTextColor(ContextCompat.getColor(this, R.color.white))
        llNew.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
        condition = "New"
    }

    private fun setSelectUsed() {
        ivUsed.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_used_item_white))
        tvUsed.setTextColor(ContextCompat.getColor(this, R.color.white))
        llUsed.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
        condition = getString(R.string.used)
    }

    private fun checkLocationOption() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            if (checkPermission()) {
                if (Utils.isOnline(this@FilterActivity)) {
                    getLocation()
                } else {
                    Utils.showSimpleMessage(this@FilterActivity, getString(R.string.check_internet)).show()
                }
            }
        }
    }

    private fun setSelectionCondition() {
        ivNew.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_new_item))
        tvNew.setTextColor(ContextCompat.getColor(this, R.color.black))
        llNew.background = ContextCompat.getDrawable(this, R.drawable.item_type)
        ivUsed.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_used_item))
        tvUsed.setTextColor(ContextCompat.getColor(this, R.color.black))
        llUsed.background = ContextCompat.getDrawable(this, R.drawable.item_type)
        condition = ""
    }

    private fun setItemList() {
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilter.layoutManager = mManager
        filterAdapter = FilterAdapter(this)
        rvFilter.adapter = filterAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == 0) {
            val provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
            if (!TextUtils.isEmpty(provider)) {
                if (checkPermission()) {
                    if (Utils.isOnline(this@FilterActivity)) {
                        getLocation()
                    } else {
                        Utils.showSimpleMessage(this@FilterActivity, getString(R.string.check_internet)).show()
                    }
                }
            } else {
                cbNearBy.isChecked = false
            }
        }
    }

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            29 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (Utils.isOnline(this@FilterActivity)) {
                            getLocation()
                        } else {
                            Utils.showSimpleMessage(this@FilterActivity, getString(R.string.check_internet)).show()
                        }

                    } else {
                        rbNearBy.isChecked = false
                        PermissionCheck(this).showPermissionDialog()

                    }
                }
            }
        }
    }

    private fun getLocation() {
        pbPrgFilter.visibility = View.VISIBLE
        mLocationRequest = LocationRequest()
        // Create the location request to start receiving updates
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL
        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            lat = locationResult.lastLocation.latitude.toString()
            lng = locationResult.lastLocation.longitude.toString()
         //   setSortFilter(getString(R.string.nearby))
            rbNearBy.isChecked=true
            Log.e("LatLng", lat + "" + lng)
            pbPrgFilter.visibility = View.GONE

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

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            , REQUEST_CODE)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                    cbNearBy.isChecked = false
                }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    override fun onStop() {
        super.onStop()
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
        }
    }
}