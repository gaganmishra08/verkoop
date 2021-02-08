package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse
import com.google.api.services.vision.v1.model.Feature
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.adapter.HomePagerAdapter
import com.verkoopapp.fragment.*
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.GPSTracker
import com.verkoopapp.utils.PermissionCheck
import com.verkoopapp.utils.Utils
import io.socket.client.Ack
import io.socket.client.Socket
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.home_activity.tvSell
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.toolbar_home.*
import kotlinx.android.synthetic.main.toolbar_home.ivChat
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity() {
    private var homeFragment: HomeFragment? = null
    private var profileFragment: ProfileFragment? = null
//    private var activitiesFragment: ActivitiesFragment? = null
    private var favouritesFragment: FavouritesFragment? = null
    private var vervoFragment: VervoFragment? = null
    private var fragmentList = ArrayList<Fragment>()
    private var doubleBackToExitPressedOnce = false
    private var comingFrom: Int = 0
    private var visionData: String = ""
    private var socket: Socket? = VerkoopApplication.getAppSocket()
    private lateinit var inputStream: InputStream;
    private lateinit var photoData: ByteArray
    private var featureList: MutableList<Feature> = ArrayList()
    private lateinit var batchResponse: BatchAnnotateImagesResponse
    private lateinit var vision: Vision
    private var mCurrentPhotoPath: String? = null
    private val CAMERA_REQUEST = 1888
    private var uriTemp: Uri? = null
    private var deviceId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        checkIfCurrencyIsNull()
        callCurrencyList()
        Log.e("TAG", "onCreate: "+Utils.getPreferencesString(this@HomeActivity,AppConstants.CURRENCY))
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        homeFragment = HomeFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()
        favouritesFragment = FavouritesFragment.newInstance()
        vervoFragment = VervoFragment.newInstance()
        fragmentList.add(homeFragment!!)
        fragmentList.add(favouritesFragment!!)
        fragmentList.add(vervoFragment!!)
        fragmentList.add(profileFragment!!)
        setData()
        callInit()
        val picOption = intent.getIntExtra(AppConstants.PICK_OPTION, 0)
        if (picOption == 1) {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivityForResult(intent, 2)
        }
        setBranchIdData()
        setIntentData()
        if (Utils.isOnline(this)) {
            firebaseDeviceToken()
        }
//        setVisionData()
    }

//    private fun setVisionData() {
//        val visionBuilder = Vision.Builder(
//                NetHttpTransport(),
//                AndroidJsonFactory(),
//                null)
//
//        visionBuilder.setVisionRequestInitializer(
//                VisionRequestInitializer("AIzaSyDjTWXzAS6IvhCf7bscIyKYZXOUKsy4Tms"))
//
//        vision = visionBuilder.build()
//    }

    private fun checkIfCurrencyIsNull() {
        if (Utils.getPreferencesString(this@HomeActivity, AppConstants.CURRENCY).isNullOrEmpty() ||
                Utils.getPreferencesString(this@HomeActivity, AppConstants.CURRENCY_SYMBOL).isNullOrEmpty()) {
            if (Utils.isOnline(this@HomeActivity)) {
                socket?.emit(AppConstants.SOCKET_DISCONNECT, getObj(), Ack {
                    Log.e("<<<DisConnectLogOut>>>", Gson().toJson(it[0]))
                })
//                callLogOutApi()
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        if (checkPermission()){
//
//            getgpslocation()
//        }
//        else{
//            checkPermission()
//        }
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
        val gpsTracker = GPSTracker(this@HomeActivity)

        if (gpsTracker.canGetLocation()) {
            val latitude: Double = gpsTracker.location.latitude
            val longitude: Double = gpsTracker.location.longitude
            val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            val cityName: String = addresses[0].getLocality()
            val stateName: String = addresses[0].getAdminArea()
            val countryName: String = addresses[0].countryCode
            Utils.savePreferencesString(this@HomeActivity, AppConstants.CITY_NAME, cityName)
            Utils.savePreferencesString(this@HomeActivity, AppConstants.STATE_NAME, stateName)
//            Utils.savePreferencesString(this@HomeActivity, AppConstants.COUNTRY_CODE, countryName)
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
//                                    gpsTracker.showSettingsAlert()
        }

    }

    private fun callLogOutApi() {
        ServiceHelper().getLogOutService(LogOutRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val responseWallet = response.body() as AddItemResponse
                if (responseWallet.message.equals("Logged out successfully")) {
                    Utils.clearPreferences(this@HomeActivity)
                    val intent = Intent(this@HomeActivity, WalkThroughActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }

            override fun onFailure(msg: String?) {
                Utils.showSimpleMessage(this@HomeActivity, msg!!).show()
            }
        })
    }

    private fun callCurrencyList() {
        ServiceHelper().getCurrencyRateList(
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val currencyResponse = response.body() as CurrencyResponse
                        if (currencyResponse.data != null) {
                            if (currencyResponse.data.currency != null) {
                                VerkoopApplication.instance.currencies.clear()
                                VerkoopApplication.instance.currencies.addAll(currencyResponse.data.currency)
                            }
                        }
                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(this@HomeActivity, msg!!).show()
                    }
                })
    }


    private fun setIntentData() {
        val result = intent!!.getIntExtra(AppConstants.TRANSACTION, 0)
        if (result == 1) {
            when {
                viewPager.currentItem == 0 -> {
                    viewPager.currentItem = 3
                    profileFragment!!.refreshUI(0)
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_button3)
                    },100)
                }
                viewPager.currentItem == 1 -> {
                    viewPager.currentItem = 3
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_button3)
                    },100)
                }
                viewPager.currentItem == 2 -> {
                    viewPager.currentItem = 3
                    Handler().postDelayed(Runnable {
                        bottomTabLayout.selectTab(R.id.menu_button3)
                    },100)
                }
                else -> {
                    profileFragment!!.refreshUI(1)
                }
            }

        }
    }

    private fun setBranchIdData() {
        val type = intent.getIntExtra(AppConstants.TYPE, 0)
        val id = intent.getIntExtra(AppConstants.ID, 0)
        if (type == 1) {
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra(AppConstants.ITEM_ID, id)
            startActivity(intent)
        } else if (type == 2) {
            if (id != Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
                val reportIntent = Intent(this, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID, id)
                startActivity(reportIntent)
            }
        } else if (type == 3) {
            val walletIntent = Intent(this, MyWalletstripeActivity::class.java)
            startActivity(walletIntent)
        } else if (type == 4) {
            val walletIntent = Intent(this, ChatInboxActivity::class.java)
            startActivity(walletIntent)
        }
    }

    private fun setTabLayout() {
        bottomTabLayout.setButtonTextStyle(R.style.TextGray12)
        // set buttons from menu resource
        bottomTabLayout.setItems(R.menu.menu_bottom_layout)
        //set on selected tab listener.
        bottomTabLayout.setListener { id ->
            switchFragment(id)
        }
        /* bottomTabLayout.setListener { position ->
             viewPager.currentItem = position
         }*/
        bottomTabLayout.setSelectedTab(R.id.menu_button1)
        //enable indicator
        bottomTabLayout.setIndicatorVisible(true)
        //indicator height
        bottomTabLayout.setIndicatorHeight(resources.getDimension(R.dimen.dp_2))
        //indicator color
        bottomTabLayout.setIndicatorColor(R.color.white)
        //indicator line color
        bottomTabLayout.setIndicatorLineColor(R.color.white)
        //bottomTabLayout.setSelectedTab(R.id.menu_button5)
    }

    private fun switchFragment(id: Int) {
        when (id) {
            R.id.menu_button1 -> viewPager.currentItem = 0
            R.id.menu_button2 -> viewPager.currentItem = 1
            R.id.vervo_button-> viewPager.currentItem = 2
            R.id.menu_button3 -> viewPager.currentItem = 3
        }
    }

    private fun setData() {
        viewPager.offscreenPageLimit=0
        val adapter = HomePagerAdapter(supportFragmentManager, 4, fragmentList)
        viewPager.adapter = adapter
        // viewPager.offscreenPageLimit = 2
        setTabLayout()
        ivChat.setOnClickListener {
            ivChat.isEnabled = false
            val intent = Intent(this, ChatInboxActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                ivChat.isEnabled = true
            }, 700)
        }
        ivFavourite.setOnClickListener {
            ivFavourite.isEnabled = false
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                ivFavourite.isEnabled = true
            }, 700)
        }
        tvSell.setOnClickListener {
            tvSell.isEnabled = false
            val intent = Intent(this, GalleryActivity::class.java)
            startActivityForResult(intent, 2)
            Handler().postDelayed(Runnable {
                tvSell.isEnabled = true
            }, 1000)
        }
        if (comingFrom == 1) {
            when {
                viewPager.currentItem == 0 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                    profileFragment!!.refreshUI(0)
                }
                viewPager.currentItem == 1 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                }
                viewPager.currentItem == 2 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                }
                else -> {
                    profileFragment!!.refreshUI(1)
                }
            }
        }
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION, 0)
                if (result == 1) {
                    when {
                        viewPager.currentItem == 0 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
                            profileFragment!!.refreshUI(0)

                        }
                        viewPager.currentItem == 1 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
                        }
                        viewPager.currentItem == 2 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
                        }
                        else -> {
                            profileFragment!!.refreshUI(1)
                        }
                    }

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
//        if (requestCode == 1888 ) {
//            if (resultCode == Activity.RESULT_OK ) {
////                VerkoopApplication.instance.loader.show(this)
////                val currentImage = data.extras!!.get("data") as Bitmap
////                val baos = ByteArrayOutputStream()
////                currentImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
////                inputStream = ByteArrayInputStream(baos.toByteArray())
////                imageToVision()
//
//                VerkoopApplication.instance.loader.show(this)
//                val f = File(mCurrentPhotoPath!!)
//                uriTemp = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", f)
//                val currentImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uriTemp) as Bitmap;
//
////                val currentImage = data.extras!!.get("data") as Bitmap
//                val baos = ByteArrayOutputStream()
//                currentImage.compress(Bitmap.CompressFormat.JPEG, 50, baos)
//                inputStream = ByteArrayInputStream(baos.toByteArray())
//                imageToVision()
//            }
//        }
    }

//    private fun imageToVision() {
//        visionData = ""
//        try {
//            photoData = IOUtils.toByteArray(inputStream)
//            inputStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            VerkoopApplication.instance.loader.hide(this)
//        }
//
//        val inputImage = Image()
//        inputImage.encodeContent(photoData)
//
//        val labelDetection = Feature()
//        labelDetection.type = "LABEL_DETECTION"
//        labelDetection.setMaxResults(2)
//        featureList.add(labelDetection)
//
//        val webDetection = Feature()
//        webDetection.type = "WEB_DETECTION"
//        webDetection.setMaxResults(2)
//        featureList.add(webDetection)
//
//        val request = AnnotateImageRequest()
//        request.image = inputImage
//        request.features = featureList
//
//        val batchRequest = BatchAnnotateImagesRequest()
//
//        batchRequest.requests = Arrays.asList(request)
//
//        val thread = Thread(Runnable {
//            try {
//                batchResponse = vision.images().annotate(batchRequest).execute()
//                VerkoopApplication.instance.loader.hide(this@HomeActivity)
//
//                if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).size > 0) {
//                    if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(0).description != null) {
//                        visionData = ((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(0).description
//                    }
//                    if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).size > 1) {
//                        if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(1).description != null) {
//                            visionData = visionData + "," + ((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(1).description
//                        }
//                    }
//                }
//                if (batchResponse.responses.get(0).labelAnnotations.size > 0) {
//                    if (batchResponse.responses.get(0).labelAnnotations.get(0).description != null) {
//                        visionData = visionData + "," + batchResponse.responses.get(0).labelAnnotations.get(0).description
//                    }
//                    if (batchResponse.responses.get(0).labelAnnotations.size > 1) {
//                        if (batchResponse.responses.get(0).labelAnnotations.get(1).description != null) {
//                            visionData = visionData + "," + batchResponse.responses.get(0).labelAnnotations.get(1).description
//                        }
//                    }
//                }
//
//                if ((batchResponse.responses.get(0).get("webDetection") as GenericData).get("bestGuessLabels") as ArrayList<String> != null) {
//                    val listData = (batchResponse.responses.get(0).get("webDetection") as GenericData).get("bestGuessLabels") as ArrayList<String>
//                    if (listData.get(0) as com.google.api.client.util.ArrayMap<String, String> != null) {
//                        val arrayMap = listData.get(0) as com.google.api.client.util.ArrayMap<String, String>
//                        if (arrayMap.get("label") != null) {
//                            visionData = visionData + "," + visionData + arrayMap.get("label")
//                        }
//                    }
//                }
//                val intent = Intent(this@HomeActivity, SearchActivity::class.java)
//                intent.putExtra("visionData", visionData)
//                startActivity(intent)
//            } catch (e: Exception) {
//                VerkoopApplication.instance.loader.hide(this@HomeActivity)
//                e.printStackTrace()
//            }
//        })
//
//        thread.start()
//    }

    private fun firebaseDeviceToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            deviceId = it.token
            Log.e("newTokena", deviceId)
            updateDeviceInfo()
        }
    }

    private fun updateDeviceInfo() {
        if (Utils.isOnline(this)) {
            callUpdateDeviceInfoApi()
        } else {
            updateDeviceInfo()
        }
    }

    private fun callUpdateDeviceInfoApi() {
        ServiceHelper().updateDeviceInfo(UpdateDeviceInfoRequest(Utils.getPreferences(this@HomeActivity, AppConstants.USER_ID), deviceId, "1"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@HomeActivity)
                        val loginResponse = response.body() as DisLikeResponse
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@HomeActivity)
                        Utils.showSimpleMessage(this@HomeActivity, msg!!).show()
                    }
                })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SocketCheckConnectionEvent) {
        /* Do something */
        callInit()

    }

    public fun showLoader(){
        progressBarHome.visibility = View.VISIBLE
    }

    public fun hideLoader(){
        progressBarHome.visibility = View.GONE
    }

    private fun callInit() {
        socket?.emit(AppConstants.INIT_USER_ID, getObj(), Ack {
            Log.e("<<<ACKRESPONSE--5>>>", Gson().toJson(it[0]))
        })

    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun getObj(): Any {
        val jsonObject: JSONObject?
        jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", Utils.getPreferencesString(applicationContext, AppConstants.USER_ID).toInt())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

//    fun startVison() {
//                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                if (takePictureIntent.resolveActivity(packageManager) != null) {
//                    var photoFile: File? = null
//                    try {
//                        photoFile = createImageFile()
//                    } catch (ex: IOException) {
//                        // Error occurred while creating the File
//                    }
//
//                    // Continue only if the File was successfully created
//                    if (photoFile != null) {
////                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                FileProvider.getUriForFile(this,
//                                        applicationContext.packageName + ".provider", photoFile))
//                        startActivityForResult(takePictureIntent, CAMERA_REQUEST)
//                    }
//                }
////        startActivityForResult(takePictureIntent, CAMERA_REQUEST)
//    }

//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_" + timeStamp + "_"
//        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//                imageFileName, /* prefix */
//                "." +
//                        "", /* suffix */
//                storageDir      /* directory */
//        )
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.absolutePath
//        return image
//    }
}