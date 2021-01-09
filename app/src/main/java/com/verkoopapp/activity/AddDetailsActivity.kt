package com.verkoopapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.android.gms.common.util.IOUtils
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.GenericData
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import com.google.gson.Gson
import id.zelory.compressor.BuildConfig
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.adapter.PropertyTypeAdapter
import com.verkoopapp.adapter.SelectedImageAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.*
import id.zelory.compressor.Compressor
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.add_details_activity.*
import kotlinx.android.synthetic.main.details_toolbar.*
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class AddDetailsActivity : AppCompatActivity(), SelectedImageAdapter.SelectedImageCount {
    private val REQUEST_CODE = 11
    private var imageList = ArrayList<SelectedImage>()
    private var addItemRequest: AddItemRequest? = null
    private var additionalInfo: AdditionalInfo? = null
    private var additionalEditInfo: AdditionalInfoResponse? = null
    private var dataIntent: DataItems? = null
    private var selectedImageList = ArrayList<ImageModal>()
    private val realPath = java.util.ArrayList<String>()
    private val realBitmap = java.util.ArrayList<Bitmap>()
    //    private val visionDataList = java.util.ArrayList<String>()
    private val visionDataList = java.util.ArrayList<labelText>()
    private var uri: Uri? = null
    private var imageCount = 0
    private var comingFrom = 0
    private var categoryId = 0
    private var parentId = 0
    private var itemId = 0
    private var parkingType = 1
    private var furnished = 1
    private var categoryName = ""
    private var labelArrayString = ""
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var address = ""
    private var itemType = 1
    private var carBrandName = ""
    private var carType = ""
    private var carModel = ""
    private var carModelId = 0
    private var carBrandId = 0
    private var carTypeId = 0
    private var zoneName = ""
    private var zoneId = 0
    private var isFocus: Boolean = false
    private var rejectImageList = ArrayList<Int>()
    private var imageIdList = ArrayList<String>()
    private var propertyTypeList = ArrayList<PropertyTypeRequest>()
    private lateinit var propertyTypeAdapter: PropertyTypeAdapter
    private var screenType = 0
    private var directOwner = 1
    private var transmissionType = 1
    private var totalBadRoom: Int = 0
    private var totalBatchRoom: Int = 0
    private lateinit var inputStream: InputStream;
    private var featureList: MutableList<Feature> = ArrayList()
    private lateinit var batchResponse: BatchAnnotateImagesResponse
    private lateinit var vision: Vision
    private lateinit var photoData: ByteArray
    private var visionData: String = ""
    private var uriTemp: Uri? = null
    private var shareDialogFacebook: ShareDialog? = null


    override fun selectDetailCount(count: Int, position: Int, imageId: Int) {
        rejectImageList.add(position)
        if (imageId > 0) {
            imageIdList.add(imageId.toString())
        }
        if (count > 0) {
            tvCountDetail.text = StringBuilder().append(" ").append(count.toString()).append(" ").append("/ 10")
        } else {
            tvCountDetail.text = StringBuilder().append(" ").append(getString(R.string.multiple))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_details_activity)
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        setData()
        setSelect()
        setPropertyAdapter()
        if (comingFrom == 1) {
            setEditItemDetail()
        } else {
            setIntentData()
        }
        setVisionData()
        shareDialogFacebook = ShareDialog(this)
        //  currency.text = Utils.getPreferencesString(this@AddDetailsActivity,AppConstants.CURRENCY_SYMBOL)

    }

    private fun setPropertyAdapter() {
        // propertyTypeList=ArrayList<PropertyTypeRequest>()
        val nameList = arrayOf("House", "Flat", "Townhouse", "Land", "Farm")
        var modal: PropertyTypeRequest?
        for (i in nameList.indices) {
            modal = if (i == 0) {
                PropertyTypeRequest(nameList[i], true)
            } else {
                PropertyTypeRequest(nameList[i], false)
            }
            propertyTypeList.add(modal)
        }
        val mManager = GridLayoutManager(this, 3)
        rvPropertyType.layoutManager = mManager
        propertyTypeAdapter = PropertyTypeAdapter(this, propertyTypeList)
        rvPropertyType.adapter = propertyTypeAdapter

    }


    private fun setEditItemDetail() {
        dataIntent = intent.getParcelableExtra(AppConstants.PRODUCT_DETAIL)
        tvCountDetail.text = StringBuilder().append(" ").append(dataIntent!!.items_image.size.toString()).append(" ").append("/ 10")
        //setListData(imageList)
        itemId = dataIntent!!.id

        if (dataIntent != null) {
            if (!TextUtils.isEmpty(dataIntent!!.name)) {
                etNameDetail.setText(dataIntent!!.name)
            }
            if (!TextUtils.isEmpty(dataIntent!!.price.toString())) {

                etPrice.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(dataIntent!!.price))
            }
            if (!TextUtils.isEmpty(dataIntent!!.description)) {
                etDescriptionDetail.setText(dataIntent!!.description)
            }
            if (!TextUtils.isEmpty(dataIntent!!.item_type.toString())) {
                itemType = dataIntent!!.item_type
                if (itemType == 1) {
                    setSelect()
                } else {
                    setSelection()
                    itemType = 2
                    ivUsedDetail.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_used_item_white))
                    tvUsedDetail.setTextColor(ContextCompat.getColor(this, R.color.white))
                    llUsedDetail.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
                }
            }
            if (dataIntent!!.latitude != 0.0) {
                lat = dataIntent!!.latitude


            }
            if (dataIntent!!.longitude != 0.0) {
                lng = dataIntent!!.longitude
            }
            if (!TextUtils.isEmpty(dataIntent!!.address)) {
                address = dataIntent!!.address!!
                tvPlaceAddress.text = dataIntent!!.address
            }
            if (!TextUtils.isEmpty(dataIntent!!.category_id.toString())) {
                categoryId = dataIntent!!.category_id
            }
            if (!TextUtils.isEmpty(dataIntent!!.category_name)) {
                categoryName = dataIntent!!.category_name
                tvCategory.text = categoryName
                tvCategory.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }

            if (!TextUtils.isEmpty(dataIntent!!.meet_up.toString())) {
                val meetUp = dataIntent!!.meet_up
                if (meetUp == 1) {
                    cbNearBy.isChecked = true
                    tvPlaceAddress.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    expansionLayout.expand(true)
                } else {
                    cbNearBy.isChecked = false
                    expansionLayout.collapse(true)
                }
            }

            //  screenType = dataIntent!!.type
            //   setScreenType(screenType)
            if (dataIntent!!.type == 1) {
                carBrandId = dataIntent!!.brand_id
                carTypeId = dataIntent!!.car_type_id
                additionalEditInfo = dataIntent!!.additional_info
                carModelId = additionalEditInfo!!.model_id
                if (!TextUtils.isEmpty(additionalEditInfo!!.model_name)) {
                    carModel = additionalEditInfo!!.model_name!!
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.brand_name)) {
                    carBrandName = additionalEditInfo!!.brand_name!!
                    //  carType = additionalEditInfo!!.car_type!!
                    if (!TextUtils.isEmpty(additionalEditInfo!!.model_name)) {
                        tvBrand.text = carBrandName + ", " + carModel
                    } else {
                        tvBrand.text = carBrandName
                    }

                    tvBrand.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                }
                directOwner = additionalEditInfo!!.direct_owner

                if (directOwner == 2) {
                    rbDealership.isChecked = true
                } else {
                    rbPrivate.isChecked = true
                }
                transmissionType = additionalEditInfo!!.transmission_type
                if (transmissionType == 2) {
                    rbAutomatic.isChecked = true
                } else {
                    rbManual.isChecked = true
                }


                if (!TextUtils.isEmpty(additionalEditInfo!!.location)) {
                    etLocation.setText(additionalEditInfo!!.location)
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.from_year.toString())) {
                    etRegFrom.setText(additionalEditInfo!!.from_year.toString())
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.to_year.toString())) {
                    etRegTo.setText(additionalEditInfo!!.to_year.toString())
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.min_price.toString())) {
                    etMinPriceCar.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(additionalEditInfo!!.min_price.toString()))
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.max_price.toString())) {
                    etMaxPriceCar.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(additionalEditInfo!!.max_price.toString()))
                }

            } else if (dataIntent!!.type == 2 || dataIntent!!.type == 3) {
                zoneId = dataIntent!!.zone_id
                additionalEditInfo = dataIntent!!.additional_info
                if (!TextUtils.isEmpty(additionalEditInfo!!.location)) {
                    zoneName = additionalEditInfo!!.location!!
                    tvZone.setText(zoneName)
//                    vZone.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                }

                if (!TextUtils.isEmpty(additionalEditInfo!!.street_name)) {
                    etStreetName.setText(additionalEditInfo!!.street_name)
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.postal_code.toString())) {
                    etPostalCode.setText(additionalEditInfo!!.postal_code.toString())
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.city)) {
                    etArea.setText(additionalEditInfo!!.city)
                }
                totalBadRoom = additionalEditInfo!!.bedroom
                totalBatchRoom = additionalEditInfo!!.bathroom
                tvBathroomCount.text = totalBatchRoom.toString()
                tvBedRoomCount.text = totalBadRoom.toString()
                if (additionalEditInfo!!.parking_type == 1) {
                    rbParking.isChecked = true
                } else {
                    rbGarage.isChecked = true
                }
                if (dataIntent!!.type == 3) {
                    if (additionalEditInfo!!.furnished == 1) {
                        rbFurnish.isChecked = true
                    } else {
                        rbUnFurnish.isChecked = true
                    }
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.min_price.toString())) {
                    etMinPriceAdd.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(additionalEditInfo!!.min_price.toString()))
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.max_price.toString())) {
                    etMaxPriceAdd.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(additionalEditInfo!!.max_price.toString()))
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.property_type)) {
                    if (additionalEditInfo!!.property_type.equals("House", ignoreCase = true)) {
                        setRemaining(0)
                    } else if (additionalEditInfo!!.property_type.equals("Flat", ignoreCase = true)) {
                        setRemaining(1)
                    } else if (additionalEditInfo!!.property_type.equals("Townhouse", ignoreCase = true)) {
                        setRemaining(2)
                    } else if (additionalEditInfo!!.property_type.equals("Land", ignoreCase = true)) {
                        setRemaining(3)
                    } else if (additionalEditInfo!!.property_type.equals("Farm", ignoreCase = true)) {
                        setRemaining(4)
                    }
                }
            }

            if (dataIntent!!.items_image.isNotEmpty()) {
                val imageList = ArrayList<SelectedImage>()
                for (i in dataIntent!!.items_image.indices) {
                    val selectedImage = SelectedImage(dataIntent!!.items_image[i].url, 0, false, dataIntent!!.items_image[i].image_id)
                    imageList.add(selectedImage)
                }
                setListData(imageList, 1)
            }
            screenType = dataIntent!!.type
            setScreenType(screenType)
        }
    }

    private fun setIntentData() {
        imageList = intent.getParcelableArrayListExtra(AppConstants.SELECTED_LIST)!!
        tvCountDetail.text = StringBuilder().append(" ").append(imageList.size.toString()).append(" ").append("/ 10")
        setListData(imageList, 0)
        addItemRequest = intent.getParcelableExtra(AppConstants.POST_DATA)
        if (addItemRequest != null) {
            if (!TextUtils.isEmpty(addItemRequest!!.name)) {
                etNameDetail.setText(addItemRequest!!.name)
            }
            if (!TextUtils.isEmpty(addItemRequest!!.price)) {
                etPrice.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(addItemRequest!!.price))
            }
            if (!TextUtils.isEmpty(addItemRequest!!.description)) {
                etDescriptionDetail.setText(addItemRequest!!.description)
            }
            if (!TextUtils.isEmpty(addItemRequest!!.item_type)) {
                itemType = addItemRequest!!.item_type.toInt()
                if (itemType == 1) {
                    setSelect()
                } else {
                    setSelection()
                    itemType = 2
                    ivUsedDetail.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_used_item_white))
                    tvUsedDetail.setTextColor(ContextCompat.getColor(this, R.color.white))
                    llUsedDetail.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
                }
            }
            if (!TextUtils.isEmpty(addItemRequest!!.Latitude)) {
                lat = addItemRequest!!.Latitude!!.toDouble()


            }
            if (!TextUtils.isEmpty(addItemRequest!!.Longitude)) {
                lng = addItemRequest!!.Longitude!!.toDouble()
            }
            if (!TextUtils.isEmpty(addItemRequest!!.Address)) {
                address = addItemRequest!!.Address!!
                tvPlaceAddress.text = addItemRequest!!.Address
            }
            if (!TextUtils.isEmpty(addItemRequest!!.categoriesId)) {
                categoryId = addItemRequest!!.categoriesId.toInt()
            }
            if (!TextUtils.isEmpty(addItemRequest!!.categoryName)) {
                categoryName = addItemRequest!!.categoryName
                tvCategory.text = categoryName
                tvCategory.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

            }

            if (!TextUtils.isEmpty(addItemRequest!!.meet_up)) {
                val meetUp = addItemRequest!!.meet_up!!.toInt()
                if (meetUp == 1) {
                    cbNearBy.isChecked = true
                    tvPlaceAddress.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    expansionLayout.expand(true)
                } else {
                    cbNearBy.isChecked = false
                    expansionLayout.collapse(true)
                }
            }
            screenType = addItemRequest!!.type
            setScreenType(screenType)
            if (addItemRequest!!.type == 1) {
                additionalInfo = addItemRequest!!.additional_info
                carModelId = additionalInfo!!.model_id
                if (!TextUtils.isEmpty(additionalInfo!!.model_name)) {
                    carModel = additionalInfo!!.model_name!!
                }
                if (!TextUtils.isEmpty(additionalInfo!!.brand_name)) {
                    carBrandName = additionalInfo!!.brand_name!!
                    // carType = additionalInfo!!.car_type!!
                    if (!TextUtils.isEmpty(additionalInfo!!.model_name)) {
                        tvBrand.text = carBrandName + ", " + carModel
                    } else {
                        tvBrand.text = carBrandName
                    }

                    carBrandId = additionalInfo!!.car_brand_id
                    carTypeId = additionalInfo!!.car_type_id
                    tvBrand.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

                }

                directOwner = additionalInfo!!.direct_owner
                if (directOwner == 2) {
                    rbDealership.isChecked = true
                } else {
                    rbPrivate.isChecked = true
                }
                transmissionType = additionalInfo!!.transmission_type
                if (transmissionType == 2) {
                    rbAutomatic.isChecked = true
                } else {
                    rbManual.isChecked = true
                }

                if (!TextUtils.isEmpty(additionalInfo!!.location)) {
                    etLocation.setText(additionalInfo!!.location)
                }
                if (!TextUtils.isEmpty(additionalInfo!!.from_year.toString()) && !additionalInfo!!.from_year.toString().equals("0", ignoreCase = true)) {
                    etRegFrom.setText(additionalInfo!!.from_year.toString())
                }
                if (!TextUtils.isEmpty(additionalInfo!!.to_year.toString()) && !additionalInfo!!.from_year.toString().equals("0", ignoreCase = true)) {
                    etRegTo.setText(additionalInfo!!.to_year.toString())
                }
                if (!TextUtils.isEmpty(additionalInfo!!.min_price.toString())) {
                    etMinPriceCar.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(additionalInfo!!.min_price.toString()))
                }
                if (!TextUtils.isEmpty(additionalInfo!!.max_price.toString())) {
                    etMaxPriceCar.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(additionalInfo!!.max_price.toString()))
                }

            } else if (addItemRequest!!.type == 2 || addItemRequest!!.type == 3) {
                additionalInfo = addItemRequest!!.additional_info
                if (!TextUtils.isEmpty(additionalInfo!!.location)) {
                    zoneName = additionalInfo!!.location!!
                    tvZone.setText(zoneName)
                    zoneId = additionalInfo!!.zone_id
                    //  tvZone.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                    vZone.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                }

                if (!TextUtils.isEmpty(additionalInfo!!.street_name)) {
                    etStreetName.setText(additionalInfo!!.street_name)
                }
                if (!TextUtils.isEmpty(additionalInfo!!.postal_code.toString()) && additionalInfo!!.postal_code != 0) {
                    etPostalCode.setText(additionalInfo!!.postal_code.toString())
                }
                if (!TextUtils.isEmpty(additionalInfo!!.city)) {
                    etArea.setText(additionalInfo!!.city)
                }
                totalBadRoom = additionalInfo!!.bedroom
                totalBatchRoom = additionalInfo!!.bathroom
                tvBathroomCount.text = totalBatchRoom.toString()
                tvBedRoomCount.text = totalBadRoom.toString()
                if (additionalInfo!!.parking_type == 1) {
                    rbParking.isChecked = true
                } else {
                    rbGarage.isChecked = true
                }
                if (addItemRequest!!.type == 3) {
                    if (additionalInfo!!.furnished == 1) {
                        rbFurnish.isChecked = true
                    } else {
                        rbUnFurnish.isChecked = true
                    }
                }
                if (!TextUtils.isEmpty(additionalInfo!!.min_price.toString())) {
                    etMinPriceAdd.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(additionalInfo!!.min_price.toString()))
                }
                if (!TextUtils.isEmpty(additionalInfo!!.max_price.toString())) {
                    etMaxPriceAdd.setText(StringBuilder().append(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL)).append(additionalInfo!!.max_price.toString()))
                }
                if (!TextUtils.isEmpty(additionalInfo!!.property_type)) {
                    if (additionalInfo!!.property_type.equals("House", ignoreCase = true)) {
                        setRemaining(0)
                    } else if (additionalInfo!!.property_type.equals("Flat", ignoreCase = true)) {
                        setRemaining(1)
                    } else if (additionalInfo!!.property_type.equals("Townhouse", ignoreCase = true)) {
                        setRemaining(2)
                    } else if (additionalInfo!!.property_type.equals("Land", ignoreCase = true)) {
                        setRemaining(3)
                    } else if (additionalInfo!!.property_type.equals("Farm", ignoreCase = true)) {
                        setRemaining(4)
                    }
                }
            }
        } else {
            val intent = Intent(this, SelectCategoryDialogActivity::class.java)
            startActivityForResult(intent, 1)
            overridePendingTransition(0, 0)
        }

    }

    private fun setRemaining(position: Int) {
        for (i in propertyTypeList.indices) {
            propertyTypeList[i].isSelected = i == position
        }
        propertyTypeAdapter.notifyDataSetChanged()
    }

    private fun setData() {
        tvUpdateAdd.setOnClickListener {
            tvUpdateAdd.isEnabled = false
            Handler().postDelayed(Runnable {
                tvUpdateAdd.isEnabled = true
            }, 700)
            val intent = Intent(this, VerifyNumberActivity::class.java)
            startActivityForResult(intent, 15)
        }
        if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.MOBILE_NO))) {
            tvMobileNo.text = Utils.getPreferencesString(this, AppConstants.MOBILE_NO)
            tvMobileNo.setTextColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))

            tvUpdateAdd.text = "Change"
        } else {
            tvUpdateAdd.text = "Update"
        }
        ivMinusBedroom.setOnClickListener {
            if (totalBadRoom != 0) {
                totalBadRoom -= 1
                tvBedRoomCount.text = totalBadRoom.toString()
            }

        }
        ivAddBedRoom.setOnClickListener {
            if (totalBadRoom <= 9) {
                totalBadRoom += 1
                tvBedRoomCount.text = totalBadRoom.toString()
            }
        }
        ivMinusBathRoom.setOnClickListener {
            if (totalBatchRoom != 0) {
                totalBatchRoom -= 1
                tvBathroomCount.text = totalBatchRoom.toString()
            }

        }

        ivAddBathRoom.setOnClickListener {
            if (totalBatchRoom <= 9) {
                totalBatchRoom += 1
                tvBathroomCount.text = totalBatchRoom.toString()
            }
        }
        /*llSelectZone.setOnClickListener {
            val intent = Intent(this, CarBrandActivity::class.java)
            intent.putExtra(AppConstants.TYPE, 3)
            intent.putExtra(AppConstants.CAR_TYPE, carType)
            intent.putExtra(AppConstants.CAR_TYPE_ID, carTypeId)
            intent.putExtra(AppConstants.CAR_BRAND_NAME, carBrandName)
            intent.putExtra(AppConstants.CAR_BRAND_ID, carBrandId)
            intent.putExtra(AppConstants.ZONE, zoneName)
            intent.putExtra(AppConstants.ZONE_ID, zoneId)
            startActivityForResult(intent, 14)
        }*/
        rgPrivate.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbPrivate -> {
                    directOwner = 1
                }
                R.id.rbDealership -> {
                    directOwner = 2
                }
            }
        }
        rgManual.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbManual -> {
                    transmissionType = 1
                }
                R.id.rbAutomatic -> {
                    transmissionType = 2
                }
            }
        }

        val font = Typeface.createFromAsset(assets, "fonts/Poppins-Regular.ttf")
        cbNearBy.typeface = font
        rbAutomatic.typeface = font
        rbManual.typeface = font
        rbDealership.typeface = font
        rbPrivate.typeface = font
        rbParking.typeface = font
        rbGarage.typeface = font
        rbFurnish.typeface = font
        rbUnFurnish.typeface = font
        llSelectBrand.setOnClickListener {
            val intent = Intent(this, CarBrandActivity::class.java)
            intent.putExtra(AppConstants.TYPE, 0)
            intent.putExtra(AppConstants.CAR_MODEL, carModel)
            intent.putExtra(AppConstants.CAR_MODEL_ID, carModelId)
            intent.putExtra(AppConstants.CAR_BRAND_NAME, carBrandName)
            intent.putExtra(AppConstants.CAR_BRAND_ID, carBrandId)
            startActivityForResult(intent, 13)
        }
        llLocation.setOnClickListener {
            val intent = Intent(this, SearchLocationActivity::class.java)
            startActivityForResult(intent, 12)
        }
        cbNearBy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkLocationOption()
            } else {
                expansionLayout.collapse(true)
            }
        }

        tvSave.setOnClickListener {
            if (Utils.isOnline(this@AddDetailsActivity)) {
                if (isValidate()) {
                    KeyboardUtil.hideKeyboard(this)
                    if (comingFrom != 1) {
                        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        imageCount = 0
                        realPath.clear()
                        grabImage(comingFrom, screenType)
                    } else {
                        updateProductDetail()
                    }
                }
            } else {
                Utils.showSimpleMessage(this@AddDetailsActivity, getString(R.string.check_internet)).show()
            }

        }
        iv_left.setOnClickListener { onBackPressed() }
        llSelectCategory.setOnClickListener {
            val intent = Intent(this, SelectCategoryDialogActivity::class.java)
            startActivityForResult(intent, 1)
            overridePendingTransition(0, 0)
        }
        llNewDetails.setOnClickListener {
            setSelection()
            setSelect()
        }
        llUsedDetail.setOnClickListener {
            setSelection()
            itemType = 2
            ivUsedDetail.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_used_item_white))
            tvUsedDetail.setTextColor(ContextCompat.getColor(this, R.color.white))
            llUsedDetail.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
        }
        etNameDetail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
//                if (arg0.isNotEmpty()) {
//                    vNameDetail.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
//                } else {
//                    vNameDetail.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
//                }
            }
        })


        etStreetName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
//                if (arg0.isNotEmpty()) {
//                    vStreetName.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
//                } else {
//                    vStreetName.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
//                }
            }
        })
        etPostalCode.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
//                if (arg0.isNotEmpty()) {
//                    vPostalCode.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
//                } else {
//                    vPostalCode.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
//                }
            }
        })
        etArea.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
//                if (arg0.isNotEmpty()) {
//                    vArea.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
//                } else {
//                    vArea.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
//                }
            }
        })
        etLocation.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
//                if (arg0.isNotEmpty()) {
//                    vLocation.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
//                } else {
//                    vLocation.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
//                }
            }
        })
        tvZone.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
//                if (arg0.isNotEmpty()) {
//                    vZone.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
//                } else {
//                    vZone.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
//                }
            }
        })
        etPrice.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocus = true
                if (etPrice.text.toString().isEmpty()) {
                    etPrice.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                    etPrice.setSelection(1)
                }
            } else {
                isFocus = false
                if (etPrice.length() == 1) {
                    etPrice.setSelection(0)
                    etPrice.setText("")
                    etPrice.hint = getString(R.string.price)
                }
            }
        }
        etPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (isFocus) {
                    if (etPrice.length() == 0) {
                        etPrice.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                        etPrice.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {
//                if (arg0.isNotEmpty()) {
//                    vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
//                } else {
//                    vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
//                }
            }
        })

        etMinPriceAdd.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocus = true
                if (etMinPriceAdd.text.toString().isEmpty()) {
                    etMinPriceAdd.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                    etMinPriceAdd.setSelection(1)
                }
            } else {
                isFocus = false
                if (etMinPriceAdd.length() == 1) {
                    etMinPriceAdd.setSelection(0)
                    etMinPriceAdd.setText("")
                    etMinPriceAdd.hint = getString(R.string.price)
                }
            }
        }
        etMinPriceAdd.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (isFocus) {
                    if (etMinPriceAdd.length() == 0) {
                        etMinPriceAdd.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                        etMinPriceAdd.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {
                /*  if (arg0.isNotEmpty()) {
                      vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                  } else {
                      vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                  }*/
            }
        })
        etMaxPriceAdd.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocus = true
                if (etMaxPriceAdd.text.toString().isEmpty()) {
                    etMaxPriceAdd.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                    etMaxPriceAdd.setSelection(1)
                }
            } else {
                isFocus = false
                if (etMaxPriceAdd.length() == 1) {
                    etMaxPriceAdd.setSelection(0)
                    etMaxPriceAdd.setText("")
                    etMaxPriceAdd.hint = getString(R.string.price)
                }
            }
        }
        etMaxPriceAdd.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (isFocus) {
                    if (etMaxPriceAdd.length() == 0) {
                        etMaxPriceAdd.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                        etMaxPriceAdd.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {
                /*  if (arg0.isNotEmpty()) {
                      vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                  } else {
                      vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                  }*/
            }
        })
        etMinPriceCar.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocus = true
                if (etMinPriceCar.text.toString().isEmpty()) {
                    etMinPriceCar.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                    etMinPriceCar.setSelection(1)
                }
            } else {
                isFocus = false
                if (etMinPriceCar.length() == 1) {
                    etMinPriceCar.setSelection(0)
                    etMinPriceCar.setText("")
                    etMinPriceCar.hint = getString(R.string.price)
                }
            }
        }
        etMinPriceCar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (isFocus) {
                    if (etMinPriceCar.length() == 0) {
                        etMinPriceCar.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                        etMinPriceCar.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {
                /*  if (arg0.isNotEmpty()) {
                      vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                  } else {
                      vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                  }*/
            }
        })
        etMaxPriceCar.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocus = true
                if (etMaxPriceCar.text.toString().isEmpty()) {
                    etMaxPriceCar.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                    etMaxPriceCar.setSelection(1)
                }
            } else {
                isFocus = false
                if (etMaxPriceCar.length() == 1) {
                    etMaxPriceCar.setSelection(0)
                    etMaxPriceCar.setText("")
                    etMaxPriceCar.hint = getString(R.string.price)
                }
            }
        }
        etMaxPriceCar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (isFocus) {
                    if (etMaxPriceCar.length() == 0) {
                        etMaxPriceCar.setText(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL))
                        etMaxPriceCar.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {
                /*  if (arg0.isNotEmpty()) {
                      vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                  } else {
                      vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                  }*/
            }
        })

        etDescriptionDetail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
//                if (arg0.isNotEmpty()) {
//                    vDescription.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
//                } else {
//                    vDescription.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
//                }
            }
        })
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbParking -> {
                    parkingType = 1
                }
                R.id.rbGarage -> {
                    parkingType = 2
                }
            }
        }
        rgFurnish.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbFurnish -> {
                    furnished = 1
                }
                R.id.rbUnFurnish -> {
                    furnished = 2
                }
            }
        }

    }

    private fun updateProductDetail() {

        val sampleList = ArrayList<ImageModal>()
        for (i in selectedImageList.indices) {
            if (TextUtils.isEmpty(selectedImageList[i].imageUrl) || selectedImageList[i].iseditable) {
                sampleList.add(selectedImageList[i])
            }
        }
        selectedImageList.clear()
        selectedImageList.addAll(sampleList)
        Log.e("<<UpLoadList>>", selectedImageList.size.toString())
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (selectedImageList.size == 1) {
            pbProgressAdd.visibility = View.VISIBLE
            val realPath = ArrayList<String>()
            updateProductApi(realPath)
        } else {
            imageCount = 0
            realPath.clear()
            realBitmap.clear()
            grabImage(comingFrom, screenType)
        }
    }

    private fun checkLocationOption() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            if (checkPermission()) {
                expansionLayout.expand(true)
            }
        }
    }

    private fun isValidate(): Boolean {
        return when {
            TextUtils.isEmpty(etNameDetail.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_item_name)).show()
                false
            }
            categoryId == 0 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_category_name)).show()
                false
            }
            screenType == 1 && carBrandId == 0 && carTypeId == 0 -> {
                Utils.showSimpleMessage(this, "Please select car brand and type.").show()
                false
            }
            screenType == 1 && TextUtils.isEmpty(etLocation.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter location").show()
                false
            }
            (screenType == 2 || screenType == 3) && TextUtils.isEmpty(tvZone.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter province.").show()
                false
            }
            (screenType == 2 || screenType == 3) && TextUtils.isEmpty(etStreetName.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter street name.").show()
                false
            }
            (screenType == 2 || screenType == 3) && TextUtils.isEmpty(etPostalCode.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter postal code.").show()
                false
            }
            screenType != 2 && screenType != 3 && screenType != 1 && TextUtils.isEmpty(etPrice.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_price)).show()
                false
            }
            screenType != 2 && screenType != 3 && screenType != 1 && etPrice.text.toString().trim().length <= 1 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_price)).show()
                false
            }
            (screenType == 2 || screenType == 3) && TextUtils.isEmpty(etArea.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter area.").show()
                false
            }
            (screenType == 2 || screenType == 3) && totalBadRoom <= 0 -> {
                Utils.showSimpleMessage(this, "Please enter bedroom count.").show()
                false
            }
            (screenType == 2 || screenType == 3) && totalBatchRoom <= 0 -> {
                Utils.showSimpleMessage(this, "Please enter bathroom count.").show()
                false
            }
            screenType == 2 && TextUtils.isEmpty(etMinPriceAdd.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_min)).show()
                false
            }
            (screenType == 2 || screenType == 3) && etMinPriceAdd.text.toString().trim().length <= 1 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_min)).show()
                false
            }
            (screenType == 2 || screenType == 3) && TextUtils.isEmpty(etMaxPriceAdd.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_max)).show()
                false
            }
            (screenType == 2 || screenType == 3) && etMaxPriceAdd.text.toString().trim().length <= 1 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_max)).show()
                false
            }
            screenType == 1 && TextUtils.isEmpty(etRegFrom.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter registration year.").show()
                false
            }
            screenType == 1 && TextUtils.isEmpty(etRegTo.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter registration year.").show()
                false
            }
            screenType == 1 && TextUtils.isEmpty(etMinPriceCar.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_min)).show()
                false
            }
            screenType == 1 && etMinPriceCar.text.toString().trim().length <= 1 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_min)).show()
                false
            }
            screenType == 1 && TextUtils.isEmpty(etMaxPriceCar.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_max)).show()
                false
            }
            screenType == 1 && etMaxPriceCar.text.toString().trim().length <= 1 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_max)).show()
                false
            }
            TextUtils.isEmpty(etDescriptionDetail.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_description)).show()
                false
            }
            selectedImageList.size < 2 -> {
                Utils.showSimpleMessage(this, getString(R.string.upload_image)).show()
                false
            }
            else -> true
        }
    }

    private fun shareDialog() {
        try {
            val shareDialog = ShareProductDialog(this, etNameDetail.text.toString(), categoryName, etPrice.text.toString(), object : SharePostListener {
                override fun finishDialog() {
                    val intent = Intent(this@AddDetailsActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(AppConstants.TRANSACTION, 1)
                    startActivity(intent)
                }

                override fun onWhatAppClick() {
                    val installed = Utils.appInstalledOrNot(this@AddDetailsActivity, "com.whatsapp")
                    if (installed) {
                        sharedDetails(2)/*WhatsApp Share*/
                    } else {
                        Utils.showSimpleMessage(this@AddDetailsActivity, getString(R.string.not_installed)).show()

                    }
                    //                Utils.showToast(this@AddDetailsActivity, "whatApp")
                }

                override fun onFacebookClick() {
                    sharedDetails(1)/*facebook Share*/
                    //                Utils.showToast(this@AddDetailsActivity, "facebook")
                }

                override fun onShareClick() {
                    sharedDetails(0)/*open Share*/
                    //                Utils.showToast(this@AddDetailsActivity, "share")
                }

            })
            shareDialog.show()
        } catch (e: Exception) {
        }
    }

    private fun sharedDetails(type: Int) {

        val buo = BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                //.setCanonicalIdentifier("content/12345")
                .setTitle(etNameDetail.text.toString())
                .setContentDescription(etDescriptionDetail.text.toString())
//                .setContentImageUrl(AppConstants.IMAGE_URL + productImage)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(ContentMetadata()
                        .addCustomMetadata("product_id", itemId.toString())
                        .addCustomMetadata("type", 1.toString()))
        val lp = LinkProperties()
                .setChannel("sms")
                .setFeature("sharing")

        buo.generateShortUrl(this, lp) { url, error ->
            if (error == null) {
                when (type) {
                    1 -> faceBookShareDialog(url)
                    2 -> {
                        val sendIntent = Intent("android.intent.action.MAIN")
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.type = "text/plain"
                        sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                        sendIntent.`package` = "com.whatsapp"
                        startActivity(sendIntent)
                    }
                    else -> {
                        val sharingIntent = Intent(Intent.ACTION_SEND)
                        sharingIntent.type = "text/html"
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Verkoop")
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, url)
                        startActivity(Intent.createChooser(sharingIntent, "Share using"))
                    }
                }
            }
        }

    }

    private fun faceBookShareDialog(url: String?) {
        val content = ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url))
                .build()
        shareDialogFacebook!!.show(content)
    }

    private fun setSelection() {
        ivNewDetails.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_new_item))
        tvNewDetails.setTextColor(ContextCompat.getColor(this, R.color.black))
        llNewDetails.background = ContextCompat.getDrawable(this, R.drawable.item_type)
        ivUsedDetail.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_used_item))
        tvUsedDetail.setTextColor(ContextCompat.getColor(this, R.color.black))
        llUsedDetail.background = ContextCompat.getDrawable(this, R.drawable.item_type)
    }

    private fun setSelect() {
        itemType = 1
        ivNewDetails.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_new_item_white))
        tvNewDetails.setTextColor(ContextCompat.getColor(this, R.color.white))
        llNewDetails.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
    }

    private fun setAdapter(comingFrom: Int) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        rvImageList.layoutParams.height = width / 3
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvImageList.layoutManager = mManager
        val selectedImageAdapter = SelectedImageAdapter(this, selectedImageList, flList, imageList.size, comingFrom)
        rvImageList.adapter = selectedImageAdapter
    }

    private fun setListData(imageList: ArrayList<SelectedImage>, comingFrom: Int) {
        selectedImageList.clear()
        for (i in imageList.indices) {
            val imageModal = ImageModal(imageList[i].imageUrl, false, false, 0, imageList[i].adapterPosition, imageList[i].isEditable, imageList[i].imageId)
            selectedImageList.add(imageModal)
        }
        if (selectedImageList.size < 10) {
            val imageModal = ImageModal("", false, true, 0, 0, false, 0)
            selectedImageList.add(imageModal)
        }
        setAdapter(comingFrom)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === 1) {
            if (resultCode === Activity.RESULT_OK) {
                categoryName = data!!.getStringExtra(AppConstants.CATEGORY_NAME).toString()
                categoryId = data.getIntExtra(AppConstants.SUB_CATEGORY_ID, 0)
                parentId = data.getIntExtra(AppConstants.CATEGORY_ID, 0)
                val typeSelection = data.getIntExtra(AppConstants.SCREEN_TYPE, 0)
                tvCategory.text = categoryName
                tvCategory.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                vSelectCategory.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                when (typeSelection) {
                    1 -> {
                        screenType = 1

                        llCarFields.visibility = View.VISIBLE


                        llProperties.visibility = View.GONE

                        llPrice.visibility = View.GONE
                        llItemCondition.visibility = View.GONE
                        tvItemCondition.visibility = View.GONE

                        tvDealOption.visibility = View.GONE
                        exLayout.visibility = View.GONE

                    }
                    2 -> {
                        screenType = 2
                        llProperties.visibility = View.VISIBLE
                        rgFurnish.visibility = View.GONE
//                        viewFurnish.visibility = View.GONE

                        llItemCondition.visibility = View.GONE
                        tvItemCondition.visibility = View.GONE

                        llPrice.visibility = View.GONE
                        llCarFields.visibility = View.GONE

                        tvDealOption.visibility = View.GONE
                        exLayout.visibility = View.GONE
                    }
                    3 -> {
                        screenType = 3
                        llProperties.visibility = View.VISIBLE
                        rgFurnish.visibility = View.VISIBLE
//                        viewFurnish.visibility = View.VISIBLE


                        llItemCondition.visibility = View.GONE
                        tvItemCondition.visibility = View.GONE

                        llPrice.visibility = View.GONE
                        llCarFields.visibility = View.GONE

                        tvDealOption.visibility = View.GONE
                        exLayout.visibility = View.GONE
                    }
                    else -> {
                        screenType = 0

                        llCarFields.visibility = View.GONE

                        llProperties.visibility = View.GONE

                        llPrice.visibility = View.VISIBLE
                        llItemCondition.visibility = View.VISIBLE
                        tvItemCondition.visibility = View.VISIBLE

                        tvDealOption.visibility = View.VISIBLE
                        exLayout.visibility = View.VISIBLE
                    }
                }
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }

        if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                address = data!!.getStringExtra(AppConstants.ADDRESS).toString()
                lat = data.getDoubleExtra(AppConstants.LATITUDE, 0.0)
                lng = data.getDoubleExtra(AppConstants.LONGITUDE, 0.0)
                Log.e("<<lat>>", lat.toString())
                Log.e("<<lng>>", lng.toString())
                cbNearBy.isChecked = true
                tvPlaceAddress.text = address
                tvPlaceAddress.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                expansionLayout.collapse(true)
                cbNearBy.isChecked = false
            }
        }
        if (requestCode == 13) {
            if (resultCode == Activity.RESULT_OK) {
                carBrandName = data!!.getStringExtra(AppConstants.CAR_BRAND_NAME).toString()
                carModel = data.getStringExtra(AppConstants.CAR_MODEL).toString()
                carBrandId = data.getIntExtra(AppConstants.CAR_BRAND_ID, 0)
                carModelId = data.getIntExtra(AppConstants.CAR_MODEL_ID, 0)
                if (!TextUtils.isEmpty(carModel)) {
                    tvBrand.text = carBrandName + ", " + carModel
                } else {
                    tvBrand.text = carBrandName
                }
                tvBrand.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                vBrand.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                expansionLayout.collapse(true)
                cbNearBy.isChecked = false
            }
        }
        if (requestCode == 14) {
            if (resultCode == Activity.RESULT_OK) {
                zoneName = data!!.getStringExtra(AppConstants.ZONE).toString()
                zoneId = data.getIntExtra(AppConstants.ZONE_ID, 0)
                tvZone.setText(zoneName)
                // tvZone.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                vZone.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                expansionLayout.collapse(true)
                cbNearBy.isChecked = false
            }
        }
        if (requestCode === REQUEST_CODE && resultCode === 0) {
            val provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
            if (!TextUtils.isEmpty(provider)) {
                if (checkPermission()) {
                    expansionLayout.expand(true)
                }

            } else {
                expansionLayout.collapse(true)
                cbNearBy.isChecked = false
            }
        }
        if (requestCode === 2) {
            if (resultCode === Activity.RESULT_OK) {
                imageList.clear()
                imageList = data!!.getParcelableArrayListExtra(AppConstants.SELECTED_LIST)!!
                tvCountDetail.text = StringBuilder().append(" ").append(imageList.size.toString()).append(" ").append("/ 10")
                setListData(imageList, 1)
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }
        if (requestCode === 15) {
            if (resultCode === Activity.RESULT_OK) {
                val phoneNo = data!!.getStringExtra(AppConstants.PHONE_NO)
                tvMobileNo.text = phoneNo
                tvUpdateAdd.text = "Change"
                if (!TextUtils.isEmpty(phoneNo)) {
                    if (phoneNo != null) {
                        Utils.savePreferencesString(this@AddDetailsActivity, AppConstants.MOBILE_NO, phoneNo)
                    }
                }
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private fun setScreenType(parentId: Int) {
        when (parentId) {
            1 -> {
                screenType = 1

                llCarFields.visibility = View.VISIBLE

                llPrice.visibility = View.GONE
                llProperties.visibility = View.GONE

                llItemCondition.visibility = View.GONE
                tvItemCondition.visibility = View.GONE

                tvDealOption.visibility = View.GONE
                exLayout.visibility = View.GONE
                llLocation.visibility = View.GONE

            }
            2 -> {
                screenType = 2
                llProperties.visibility = View.VISIBLE
                rgFurnish.visibility = View.GONE
//                viewFurnish.visibility = View.GONE

                llPrice.visibility = View.GONE
                llItemCondition.visibility = View.GONE
                tvItemCondition.visibility = View.GONE


                llCarFields.visibility = View.GONE

                tvDealOption.visibility = View.GONE
                exLayout.visibility = View.GONE
                llLocation.visibility = View.GONE
            }
            3 -> {
                screenType = 3
                llProperties.visibility = View.VISIBLE
                rgFurnish.visibility = View.VISIBLE
//                viewFurnish.visibility = View.VISIBLE

                llPrice.visibility = View.GONE
                llItemCondition.visibility = View.GONE
                tvItemCondition.visibility = View.GONE


                llCarFields.visibility = View.GONE

                tvDealOption.visibility = View.GONE
                exLayout.visibility = View.GONE
                llLocation.visibility = View.GONE
            }
            else -> {
                screenType = 0

                llCarFields.visibility = View.GONE


                llProperties.visibility = View.GONE


                llPrice.visibility = View.VISIBLE
                llItemCondition.visibility = View.VISIBLE
                tvItemCondition.visibility = View.VISIBLE

                tvDealOption.visibility = View.VISIBLE
                exLayout.visibility = View.VISIBLE
                llLocation.visibility = View.VISIBLE
            }
        }
    }

    fun grabImage(comingFrom: Int, screenType: Int) {
        class Converter : AsyncTask<Void, Void, Bitmap>() {

            override fun onPreExecute() {
                super.onPreExecute()
                pbProgressAdd.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg params: Void): Bitmap? {
                val uriTemp = FileProvider.getUriForFile(this@AddDetailsActivity, "com.verkoopapp" + ".provider", File(Utils.getRealPathFromUri(this@AddDetailsActivity, Uri.parse(selectedImageList[imageCount].imageUrl))))
                contentResolver.notifyChange(uriTemp, null)
                val cr = contentResolver
                var bmp: Bitmap? = null
                try {
                    bmp = android.provider.MediaStore.Images.Media.getBitmap(cr, uriTemp)
                    val scaledBitmap = Utils.scaleDown(bmp, 1024f, true)
                    val compressedBitmap = Utils.scaleDown(bmp, 300f, true)
                    //  uri = Utils.getImageUri(this@AddDetailsActivity,CommonUtils.rotateImageIfRequired(this@AddDetailsActivity,scaledBitmap,Uri.parse(selectedImageList[imageCount].imageUrl)))
                    uri = Uri.parse(Utils.saveTempBitmap(this@AddDetailsActivity, CommonUtils.rotateImageIfRequired(this@AddDetailsActivity, scaledBitmap, Uri.parse(selectedImageList[imageCount].imageUrl))))
                    realPath.add(Utils.getRealPathFromURI(this@AddDetailsActivity, uri!!))
                    val baos = ByteArrayOutputStream()
                    compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                    //realBitmap.add(bmp)
                    realBitmap.add(compressedBitmap)
                } catch (e: Exception) {
                    Log.e("<<<LOG>>>", "Failed to load", e)
                }
                return bmp
            }

            override fun onPostExecute(aVoid: Bitmap?) {
                if (aVoid != null) {
                    super.onPostExecute(aVoid)
                }
                if (imageCount == selectedImageList.size - 2) {
                    //   pbProgress.setVisibility(View.GONE)
                    imageCount = 0
                    Log.e("<<RealImagePath>>", realPath.toString())
                    if (Utils.isOnline(this@AddDetailsActivity)) {
                        if (comingFrom != 1) {
                            /*Api call*/imageToVision(realPath, screenType)
//                            uploadImageItem(realPath, screenType)
                        } else {
                            updateProductApi(realPath)
                        }
                    } else {
                        Utils.showSimpleMessage(this@AddDetailsActivity, getString(R.string.check_internet)).show()
                    }

                } else if (imageCount < selectedImageList.size - 1) {
                    imageCount++
                    grabImage(comingFrom, screenType)
                }
            }

        }
        Converter().execute()
    }

    private fun updateProductApi(realPath: java.util.ArrayList<String>) {
        var meetUp = "0"
        if (cbNearBy.isChecked && lat != 0.0 && lng != 0.0) {
            meetUp = "1"
        }
        val editItemRequest: EditItemRequest?
        if (screenType == 1) {
            val additionalInfo = AdditionalInfo(car_brand_id = carBrandId, brand_name = carBrandName, car_type = carType, car_type_id = carTypeId, direct_owner = directOwner, location = etLocation.text.toString(), from_year = (etRegFrom.text.toString()).toInt(), to_year = (etRegTo.text.toString()).toInt(), min_price = etMinPriceCar.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), ""), max_price = etMaxPriceCar.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), ""), transmission_type = transmissionType, model_id = carModelId, model_name = carModel)
            editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etMaxPriceCar.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(),  Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "0", itemId, screenType, additionalInfo, zoneId, carBrandId, carTypeId)

        } else if (screenType == 2) {
            var property_ = ""
            for (i in propertyTypeList.indices) {
                if (propertyTypeList[i].isSelected) {
                    property_ = propertyTypeList[i].name
                }
            }
            val additionalInfo = AdditionalInfo(location = tvZone.text.toString(), zone_id = zoneId, street_name = etStreetName.text.toString(), postal_code = (etPostalCode.text.toString()).toInt(), city = etArea.text.toString(), bedroom = totalBadRoom, bathroom = totalBatchRoom, min_price = (etMinPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), max_price = (etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), property_type = property_, parking_type = parkingType)
            editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "0", itemId, screenType, additionalInfo, zoneId)

        } else if (screenType == 3) {
            var property_ = ""
            for (i in propertyTypeList.indices) {
                if (propertyTypeList[i].isSelected) {
                    property_ = propertyTypeList[i].name
                }
            }
            val additionalInfo = AdditionalInfo(location = tvZone.text.toString(), zone_id = zoneId, street_name = etStreetName.text.toString(), postal_code = (etPostalCode.text.toString()).toInt(), city = etArea.text.toString(), bedroom = totalBadRoom, bathroom = totalBatchRoom, min_price = (etMinPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), max_price = (etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), property_type = property_, parking_type = parkingType, furnished = furnished)
            editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "0", itemId, screenType, additionalInfo, zoneId)

        } else {
            if (cbNearBy.isChecked) {
                val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, "", directOwner)
                editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "1", itemId, screenType, additionalInfo)
            } else {
                val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, "", directOwner)
                editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), "0.0", "0.0", "", "0", itemId, screenType, additionalInfo)
            }
        }
        //  dasds
        //  val editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity,AppConstants.CURRENCY_SYMBOL), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, meetUp, itemId,type)

        for (i in 0 until editItemRequest.imageList.size) {
            if (!TextUtils.isEmpty(editItemRequest.imageList[i])) {
                val file1 = File(editItemRequest.imageList[i])
                if (file1.length() > AppConstants.MAX_FILE_SIZE) {
                    editItemRequest.imageList[i] = Compressor(this@AddDetailsActivity).compressToFile(file1).absolutePath
                }
            }
        }
        ServiceHelper().editItemsApi(editItemRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
//                        Log.e("TAG", "onSuccess: "+editItemRequest.toString())
//                        Log.e("TAG", "onSuccesseditItemRequest: "+editItemRequest.country_code)
                        pbProgressAdd.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val categoriesResponse = response.body() as AddItemResponse
                        // Utils.showToast(this@AddDetailsActivity, categoriesResponse.message)
                        // shareDialog()
                        Utils.showToast(this@AddDetailsActivity, categoriesResponse.message)
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.TYPE, "UpdateItem")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressAdd.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Utils.showSimpleMessage(this@AddDetailsActivity, msg!!).show()
                    }
                })

    }

    private fun uploadImageItem(realPath: java.util.ArrayList<String>, screenType: Int) {
//            imageToVision()
        var addItemRequest: AddItemRequest? = null
        if (screenType == 1) {
            //val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, etRegistrationYear.text.toString(), directOwner)
            val additionalInfo = AdditionalInfo(car_brand_id = carBrandId, brand_name = carBrandName, car_type = carType, car_type_id = carTypeId, direct_owner = directOwner, location = etLocation.text.toString(), from_year = (etRegFrom.text.toString()).toInt(), to_year = (etRegTo.text.toString()).toInt(), min_price = etMinPriceCar.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), ""), max_price = etMaxPriceCar.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), ""), transmission_type = transmissionType, model_id = carModelId, model_name = carModel)
            addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etMaxPriceCar.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(),  Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, lat.toString(), lng.toString(), address, "0", screenType, additionalInfo, zoneId, carBrandId, carTypeId)

        } else if (screenType == 2) {
            var property_ = ""
            for (i in propertyTypeList.indices) {
                if (propertyTypeList[i].isSelected) {
                    property_ = propertyTypeList[i].name
                }
            }
            val additionalInfo = AdditionalInfo(location = tvZone.text.toString(), zone_id = 0, street_name = etStreetName.text.toString(), postal_code = (etPostalCode.text.toString()).toInt(), city = etArea.text.toString(), bedroom = totalBadRoom, bathroom = totalBatchRoom, min_price = (etMinPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), max_price = (etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), property_type = property_, parking_type = parkingType)
            addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(),  Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, lat.toString(), lng.toString(), address, "0", screenType, additionalInfo, zoneId)
        } else if (screenType == 3) {
            var property_ = ""
            for (i in propertyTypeList.indices) {
                if (propertyTypeList[i].isSelected) {
                    property_ = propertyTypeList[i].name
                }
            }
            val additionalInfo = AdditionalInfo(location = tvZone.text.toString(), zone_id = 0, street_name = etStreetName.text.toString(), postal_code = (etPostalCode.text.toString()).toInt(), city = etArea.text.toString(), bedroom = totalBadRoom, bathroom = totalBatchRoom, min_price = (etMinPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), max_price = (etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), property_type = property_, parking_type = parkingType, furnished = furnished)
            addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(),  Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, lat.toString(), lng.toString(), address, "0", screenType, additionalInfo, zoneId)
        } else {
            if (cbNearBy.isChecked) {
                val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, "", directOwner)
                addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(),  Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, lat.toString(), lng.toString(), address, "1", screenType, additionalInfo)
            } else {
                val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, "", directOwner)
                addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, "0.0", "0.0", "", "0", screenType, additionalInfo
                )
            }
        }

        for (i in 0 until addItemRequest.imageList.size) {
            if (!TextUtils.isEmpty(addItemRequest.imageList[i])) {
                val file1 = File(addItemRequest.imageList[i])
                if (file1.length() > AppConstants.MAX_FILE_SIZE) {
                    addItemRequest.imageList[i] = Compressor(this@AddDetailsActivity).compressToFile(file1).absolutePath
                }
            }
        }
        ServiceHelper().addItemsApi(addItemRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressAdd.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val categoriesResponse = response.body() as AddItemResponse
                        Utils.showToast(this@AddDetailsActivity, categoriesResponse.message)
                        shareDialog()

//                        val intent = Intent(this@AddDetailsActivity, HomeActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent.putExtra(AppConstants.TRANSACTION, 1)
//                        startActivity(intent)

                        /*  val returnIntent = Intent()
                          returnIntent.putExtra(AppConstants.TRANSACTION, 1)
                          setResult(Activity.RESULT_OK, returnIntent)
                          finish()*/
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressAdd.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Utils.showSimpleMessage(this@AddDetailsActivity, msg!!).show()
                    }
                })
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

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            29 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        expansionLayout.expand(true)
                        Utils.showToast(this, "Permission")
                    } else {
                        cbNearBy.isChecked = false
                        expansionLayout.collapse(true)
                        PermissionCheck(this).showPermissionDialog()

                    }
                }
            }
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.gps_permission))
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

    override fun onBackPressed() {
        val sendList = ArrayList<String>()
        for (i in selectedImageList.indices) {
            if (!TextUtils.isEmpty(selectedImageList[i].imageUrl)) {
                sendList.add(selectedImageList[i].imageUrl)
            }
        }
        // val additionalInfo=AdditionalInfo(carBrandId,carBrandName,carType,carTypeId,etRegistrationYear.text.toString(),directOwner)


        //  val addItemRequest: AddItemRequest
        /*if (screenType != 0) {
            if (!TextUtils.isEmpty(etPostalCode.text.toString())) {
                postalCode = (etPostalCode.text.toString()).toInt()
            }
            var property_ = ""
            for (i in propertyTypeList.indices) {
                if (propertyTypeList[i].isSelected) {
                    property_ = propertyTypeList[i].name
                }
            }
            val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, etRegistrationYear.text.toString(), directOwner, tvZone.text.toString(), zoneId, etStreetName.text.toString(), postalCode, etArea.text.toString(), totalBadRoom, totalBatchRoom, (etMinPriceAdd.text.toString().replace("R", "")), (etMaxPriceAdd.text.toString().replace("$", "")), property_, parkingType)
            addItemRequest = AddItemRequest(sendList, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity,AppConstants.CURRENCY_SYMBOL), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "1", screenType, additionalInfo)

        }*/
        val addItemRequest: AddItemRequest?
        if (screenType == 1) {
            var fromYear = 0
            if (!TextUtils.isEmpty(etRegFrom.text.toString())) {
                fromYear = (etRegFrom.text.toString()).toInt()
            }
            var toYear = 0
            if (!TextUtils.isEmpty(etRegTo.text.toString())) {
                toYear = (etRegTo.text.toString()).toInt()
            }
            //val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, etRegistrationYear.text.toString(), directOwner)
            val additionalInfo = AdditionalInfo(car_brand_id = carBrandId, brand_name = carBrandName, car_type = carType, car_type_id = carTypeId, direct_owner = directOwner, location = etLocation.text.toString(), from_year = fromYear, to_year = toYear, min_price = etMinPriceCar.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), ""), max_price = etMaxPriceCar.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), ""), transmission_type = transmissionType, model_id = carModelId, model_name = carModel)
            addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etMaxPriceCar.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, lat.toString(), lng.toString(), address, "0", screenType, additionalInfo, zoneId, carBrandId, carTypeId)

        } else if (screenType == 2) {
            var property_ = ""
            for (i in propertyTypeList.indices) {
                if (propertyTypeList[i].isSelected) {
                    property_ = propertyTypeList[i].name
                }
            }
            var postalCode = 0
            if (!TextUtils.isEmpty(etPostalCode.text.toString()) && !etPostalCode.text.toString().equals("null", ignoreCase = true)) {
                postalCode = (etPostalCode.text.toString()).toInt()
            }
            val additionalInfo = AdditionalInfo(location = tvZone.text.toString(), zone_id = 0, street_name = etStreetName.text.toString(), postal_code = postalCode, city = etArea.text.toString(), bedroom = totalBadRoom, bathroom = totalBatchRoom, min_price = (etMinPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), max_price = (etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), property_type = property_, parking_type = parkingType)
            addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(),  Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, lat.toString(), lng.toString(), address, "0", screenType, additionalInfo, zoneId)
        } else if (screenType == 3) {
            var property_ = ""
            for (i in propertyTypeList.indices) {
                if (propertyTypeList[i].isSelected) {
                    property_ = propertyTypeList[i].name
                }
            }
            var postalCode = 0
            if (!TextUtils.isEmpty(etPostalCode.text.toString()) && !etPostalCode.text.toString().equals("null", ignoreCase = true)) {
                postalCode = (etPostalCode.text.toString()).toInt()
            }
            val additionalInfo = AdditionalInfo(location = tvZone.text.toString(), zone_id = 0, street_name = etStreetName.text.toString(), postal_code = postalCode, city = etArea.text.toString(), bedroom = totalBadRoom, bathroom = totalBatchRoom, min_price = (etMinPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), max_price = (etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "")), property_type = property_, parking_type = parkingType, furnished = furnished)
            addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etMaxPriceAdd.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(),  Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, lat.toString(), lng.toString(), address, "0", screenType, additionalInfo, zoneId)
        } else {
            if (cbNearBy.isChecked) {
                addItemRequest = AddItemRequest(sendList, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(),  Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, lat.toString(), lng.toString(), address, "1", screenType)
            } else {
                addItemRequest = AddItemRequest(sendList, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.CURRENCY_SYMBOL), "").trim(),  Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.COUNTRY_CODE),itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), labelArrayString, "0.0", "0.0", "", "0", screenType)
            }
        }
        val returnIntent = Intent()
        returnIntent.putExtra(AppConstants.POST_DATA, addItemRequest)
        returnIntent.putIntegerArrayListExtra(AppConstants.REJECT_LIST, rejectImageList)
        returnIntent.putExtra(AppConstants.TRANSACTION, 2)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }


    private fun setVisionData() {
        val visionBuilder = Vision.Builder(
                NetHttpTransport(),
                AndroidJsonFactory(),
                null)

        visionBuilder.setVisionRequestInitializer(
                VisionRequestInitializer(getString(R.string.google_vision_api_key)))

        vision = visionBuilder.build()
    }

    private fun imageToVision(realPath: java.util.ArrayList<String>, screenType: Int) {

        try {
            photoData = IOUtils.toByteArray(inputStream)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            VerkoopApplication.instance.loader.hide(this)
        }
        val labelDetection = Feature()
        labelDetection.type = "LABEL_DETECTION"
        labelDetection.setMaxResults(2)
        featureList.add(labelDetection)

        val webDetection = Feature()
        webDetection.type = "WEB_DETECTION"
        webDetection.setMaxResults(2)
        featureList.add(webDetection)

        val imageList = java.util.ArrayList<AnnotateImageRequest>()
        for (i in realBitmap.indices) {
            val annotateImageRequest = AnnotateImageRequest()
            val base64EncodedImage = getBase64EncodedJpeg(realBitmap.get(i))
            annotateImageRequest.image = base64EncodedImage
            annotateImageRequest.features = featureList
            imageList.add(annotateImageRequest)
        }

        val batchRequest = BatchAnnotateImagesRequest()

        batchRequest.requests = imageList

        val thread = Thread(Runnable {
            try {
                batchResponse = vision.images().annotate(batchRequest).execute()
                VerkoopApplication.instance.loader.hide(this)

                for (i in batchResponse.responses.indices) {
                    visionData = ""
                    if (((batchResponse.responses.get(i).get("webDetection") as WebDetection).webEntities).size > 0) {
                        if (((batchResponse.responses.get(i).get("webDetection") as WebDetection).webEntities).get(0).description != null) {
                            visionData = ((batchResponse.responses.get(i).get("webDetection") as WebDetection).webEntities).get(0).description
                        }
                        if (((batchResponse.responses.get(i).get("webDetection") as WebDetection).webEntities).size > 1) {
                            if (((batchResponse.responses.get(i).get("webDetection") as WebDetection).webEntities).get(1).description != null) {
                                visionData = visionData + "," + ((batchResponse.responses.get(i).get("webDetection") as WebDetection).webEntities).get(1).description
                            }
                        }
                    }
                    if (batchResponse.responses.get(i).labelAnnotations != null) {
                        if (batchResponse.responses.get(i).labelAnnotations.size > 0) {
                            if (batchResponse.responses.get(i).labelAnnotations.get(0).description != null) {
                                visionData = visionData + "," + batchResponse.responses.get(i).labelAnnotations.get(0).description
                            }
                            if (batchResponse.responses.get(i).labelAnnotations.size > 1) {
                                if (batchResponse.responses.get(i).labelAnnotations.get(1).description != null) {
                                    visionData = visionData + "," + batchResponse.responses.get(i).labelAnnotations.get(1).description
                                }
                            }
                        }
                    }

                    if ((batchResponse.responses.get(i).get("webDetection") as GenericData).get("bestGuessLabels") as ArrayList<String> != null) {
                        val listData = (batchResponse.responses.get(i).get("webDetection") as GenericData).get("bestGuessLabels") as ArrayList<String>
                        if (listData.get(0) as com.google.api.client.util.ArrayMap<String, String> != null) {
                            val arrayMap = listData.get(0) as com.google.api.client.util.ArrayMap<String, String>
                            if (arrayMap.get("label") != null) {
                                visionData = visionData + "," + visionData + arrayMap.get("label")
                            }
                        }
                    }
                    val text = labelText(visionData)
                    visionDataList.add(text)
                }

                labelArrayString = Gson().toJson(visionDataList)
                Log.v("VisionData", labelArrayString)
                uploadImageItem(realPath, screenType)
            } catch (e: Exception) {
                VerkoopApplication.instance.loader.hide(this)
                e.printStackTrace()
            }
        })

        thread.start()
    }

    fun getBase64EncodedJpeg(bitmap: Bitmap): Image {
        val image = Image()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        image.encodeContent(imageBytes)
        return image
    }

}