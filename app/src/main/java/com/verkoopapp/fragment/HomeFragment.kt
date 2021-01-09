package com.verkoopapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.util.IOUtils
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.GenericData
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.activity.*
import com.verkoopapp.adapter.HomeAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.utils.GridSpacingItemDecoration
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.home_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment() {
    val TAG = HomeFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: HomeActivity
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var linearLayoutManager: GridLayoutManager
    private var itemsList = ArrayList<ItemHome>()
    private var isLoading = false
    private var totalPageCount: Int? = null
    private var currentPage = 0
    private val CAMERA_REQUEST = 1888
    private var mCurrentPhotoPath: String? = null
    private lateinit var inputStream: InputStream;
    private var featureList: MutableList<Feature> = ArrayList()
    private lateinit var batchResponse: BatchAnnotateImagesResponse
    private lateinit var vision: Vision
    private lateinit var photoData: ByteArray
    private var visionData: String = ""
    private var comingFrom: String = ""
    private var uriTemp: Uri? = null
    private var fromLikeEvent: Boolean = false
    private var positionFromLikeEvent: Int = 0


    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeActivity = activity as HomeActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setVisionData()
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val display = homeActivity.windowManager.defaultDisplay
        val window: Window = activity!!.window
        window.statusBarColor = ContextCompat.getColor(activity!!,R.color.gray);
        val size = Point()
        display.getSize(size)
        val width = size.x
        setItemList(width)
        if (Utils.isOnline(homeActivity)) {
            itemsList.clear()
            currentPage = 1
            homeActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (pbProgressHome != null) {
                pbProgressHome.visibility = View.VISIBLE
            }
            getItemService(0)
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
        setData()
    }

    private fun setItemList(width: Int) {
        linearLayoutManager = GridLayoutManager(homeActivity, 2)
        linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (homeAdapter.getItemViewType(position)) {
                    homeAdapter.CATEGORY_LIST_ROW -> 2
                    homeAdapter.YOUR_DAILY_PICKS -> 2
                    homeAdapter.PROPERTIES_ROW -> 2
                    homeAdapter.RECOMMENDED_YOU -> 2
                    homeAdapter.SHOW_LOADER -> 2
                    else ->
                        1
                }
            }
        }
        rvHomeList.layoutManager = linearLayoutManager
//        rvHomeList.addItemDecoration(GridSpacingItemDecoration(2, Utils.dpToPx(homeActivity, 2F).toInt(), false))
        rvHomeList.setHasFixedSize(false)
        homeAdapter = HomeAdapter(homeActivity, width, this)
        rvHomeList.adapter = homeAdapter
        //  rvHomeList.setItemViewCacheSize(5);
        rvHomeList.addOnScrollListener(recyclerViewOnScrollListener)
    }

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val totalItemCount = linearLayoutManager.itemCount
            val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

            if (!isLoading && currentPage != totalPageCount) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    if (Utils.isOnline(homeActivity)) {
                        itemsList.add(ItemHome(isLoading = true))
                        Log.e("AddItemPosition", (itemsList.size - 1).toString())
                        homeAdapter.notifyItemInserted((itemsList.size - 1) + 4)
                        currentPage += 1
                        getItemService(0)
                    } else {
                        //   Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
                    }
                }
            }
        }
    }

    private fun setData() {


        swipeContainer.setOnRefreshListener {
            if (Utils.isOnline(homeActivity)) {
                currentPage = 1
                getItemService(1)
            } else {
                swipeContainer.isRefreshing = false
                Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
            }
        }
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary)

        ivChat.setOnClickListener {
            ivChat.isEnabled = false
            val intent = Intent(homeActivity, ChatInboxActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                ivChat.isEnabled = true
            }, 700)
        }

        tvCategoryHome.setOnClickListener {
            val intent = Intent(homeActivity, FullCategoriesActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        llSearchHome.setOnClickListener {
            val intent = Intent(homeActivity, SearchActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        tvSell.setOnClickListener {
            tvSell.isEnabled = false
            val intent = Intent(homeActivity, GalleryActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
            Handler().postDelayed(Runnable {
                tvSell.isEnabled = true
            }, 1000)
        }
        ivAR.setOnClickListener {
            if (checkAndRequestPermission()) {
                ivAR.isEnabled = false
//                val intent = Intent("android.media.action.IMAGE_CAPTURE")
//                homeActivity.startActivityForResult(intent,CAMERA_REQUEST)
//                homeActivity.startVison()

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
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
                                FileProvider.getUriForFile(activity!!,
                                        activity!!.applicationContext.packageName + ".provider", photoFile))
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                    }
//                }
                Handler().postDelayed(Runnable {
                    ivAR.isEnabled = true
                }, 1000)
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
        val f = File(storageDir.toString())
        if (!f.exists()) {
            f.mkdirs()
        }
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

    private fun checkAndRequestPermission(): Boolean {
        val storage = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val camera = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        val listPermissionsNeeded = java.util.ArrayList<String>()
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toTypedArray(), 596)
            return false
        }
        return true
    }

    companion object {
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }


    private fun setApiData(data: DataHome?, loadMore: Int) {
        if (data!!.categories.size > 0 && data.advertisments!!.size > 0) {
            homeAdapter.setCategoryAndAddsData(data.advertisments, data.categories)
        }
        if (data.daily_pic.size > 0) {
            homeAdapter.updateDailyPicksData(data.daily_pic)
        }
        totalPageCount = data.totalPage
        if (loadMore == 1) {
            itemsList.clear()
            itemsList.addAll(data.items)
        } else {
            itemsList.addAll(data.items)
        }
        homeAdapter.setData(itemsList)
        homeAdapter.notifyDataSetChanged()
    }


    private fun getItemService(loadMore: Int) {
        isLoading = true
        ServiceHelper().getItemsService(HomeRequest(0,Utils.getPreferencesString(homeActivity, AppConstants.COUNTRY_CODE) ,currentPage), Utils.getPreferencesString(homeActivity, AppConstants.USER_ID),
                object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (swipeContainer != null) {
                    swipeContainer.isRefreshing = false
                }
                if (fromLikeEvent == true) {
                    if (comingFrom.equals("RecommendedForYou")) {
                        rvHomeList.scrollToPosition(positionFromLikeEvent + 7)
                    } else {
                        homeAdapter.getpositionFromLike(positionFromLikeEvent, comingFrom)
                    }
                }
                isLoading = false
                if (pbProgressHome != null) {
                    pbProgressHome.visibility = View.GONE
                    rvHomeList.visibility = View.VISIBLE
                }
                if (currentPage > 1 && itemsList.size > 0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    homeAdapter.notifyItemRemoved(scrollPosition + 4)
                }
                val homeDataResponse = response.body() as HomeDataResponse?
                if (homeDataResponse!!.data != null) {
                    setApiData(homeDataResponse.data, loadMore)
                }
            }

            override fun onFailure(msg: String?) {
                homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (swipeContainer != null) {
                    swipeContainer.isRefreshing = false
                }
                isLoading = false
                if (pbProgressHome != null) {
                    pbProgressHome.visibility = View.GONE
                }
                Utils.showSimpleMessage(homeActivity, msg!!).show()
                if (currentPage >= 2) {
                    currentPage -= 1
                }

                if (currentPage > 1 && itemsList.size > 0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    homeAdapter.notifyItemRemoved(scrollPosition)
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 596) {
            if (grantResults.size == 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity!!, "Permission granted", Toast.LENGTH_SHORT).show()
                    val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    homeActivity.startActivityForResult(intent, CAMERA_REQUEST)
                } else {
                    Toast.makeText(activity!!, getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show()
                }
            } else if (grantResults.size == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity!!, "Permission granted", Toast.LENGTH_SHORT).show()
                    val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    homeActivity.startActivityForResult(intent, CAMERA_REQUEST)
                } else {
                    Toast.makeText(activity!!, getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1888) {
            if (resultCode == Activity.RESULT_OK) {
                VerkoopApplication.instance.loader.show(activity!!)
                val f = File(mCurrentPhotoPath!!)
                uriTemp = FileProvider.getUriForFile(activity!!, activity!!.applicationContext.packageName + ".provider", f)
                val currentImage = MediaStore.Images.Media.getBitmap(activity!!.getContentResolver(), uriTemp) as Bitmap;

//                val currentImage = data.extras!!.get("data") as Bitmap
                val baos = ByteArrayOutputStream()
                currentImage.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                inputStream = ByteArrayInputStream(baos.toByteArray())
                imageToVision()
            }
        } else if (requestCode == 1777) {
            if (resultCode == Activity.RESULT_OK) {
//                if (data != null) {
//                    val position = data.getIntExtra(AppConstants.ADAPTER_POSITION, 0)
//                    val comingFrom = data.getStringExtra(AppConstants.COMING_FROM)
//                    val totalLike = data.getIntExtra(AppConstants.TOTAL_LIKE,0)
//                    if (position != null) {
//                        if (comingFrom.equals("YourDailyPicksAdapter")) {
////                         itemsList[posiiton].is_like=data.getBooleanExtra(AppConstants.IS_LIKED,false)
//                        homeAdapter.setLikeOnPostViewDaily(position,data.getBooleanExtra(AppConstants.IS_LIKED,false),totalLike)
//                        }
//                    }
//                }
            }
        }
    }


    @Subscribe
    fun onMessageEventOnLike(event: MessageEventOnLike) {
        fromLikeEvent = true
        comingFrom = event.comingFrom
        positionFromLikeEvent = event.position
        if (Utils.isOnline(homeActivity)) {
            currentPage = 1
            getItemService(1)
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
        Handler().postDelayed(Runnable {
            fromLikeEvent = false
        }, 2500)
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
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

    private fun imageToVision() {
//        visionData = ""
        try {
            photoData = IOUtils.toByteArray(inputStream)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            VerkoopApplication.instance.loader.hide(activity!!)
        }

        val inputImage = Image()
        inputImage.encodeContent(photoData)

        val labelDetection = Feature()
        labelDetection.type = "LABEL_DETECTION"
        labelDetection.setMaxResults(2)
        featureList.add(labelDetection)

        val webDetection = Feature()
        webDetection.type = "WEB_DETECTION"
        webDetection.setMaxResults(2)
        featureList.add(webDetection)

        val request = AnnotateImageRequest()
        request.image = inputImage
        request.features = featureList

        val batchRequest = BatchAnnotateImagesRequest()

        batchRequest.requests = Arrays.asList(request)

        val thread = Thread(Runnable {
            try {
                batchResponse = vision.images().annotate(batchRequest).execute()
                VerkoopApplication.instance.loader.hide(activity!!)

                if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).size > 0) {
                    if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(0).description != null) {
                        visionData = ((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(0).description
                    }
                    if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).size > 1) {
                        if (((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(1).description != null) {
                            visionData = visionData + "," + ((batchResponse.responses.get(0).get("webDetection") as WebDetection).webEntities).get(1).description
                        }
                    }
                }
                if (batchResponse.responses.get(0).labelAnnotations != null) {
                    if (batchResponse.responses.get(0).labelAnnotations.size > 0) {
                        if (batchResponse.responses.get(0).labelAnnotations.get(0).description != null) {
                            visionData = visionData + "," + batchResponse.responses.get(0).labelAnnotations.get(0).description
                        }
                        if (batchResponse.responses.get(0).labelAnnotations.size > 1) {
                            if (batchResponse.responses.get(0).labelAnnotations.get(1).description != null) {
                                visionData = visionData + "," + batchResponse.responses.get(0).labelAnnotations.get(1).description
                            }
                        }
                    }
                }

                if ((batchResponse.responses.get(0).get("webDetection") as GenericData).get("bestGuessLabels") as ArrayList<String> != null) {
                    val listData = (batchResponse.responses.get(0).get("webDetection") as GenericData).get("bestGuessLabels") as ArrayList<String>
                    if (listData.get(0) as com.google.api.client.util.ArrayMap<String, String> != null) {
                        val arrayMap = listData.get(0) as com.google.api.client.util.ArrayMap<String, String>
                        if (arrayMap.get("label") != null) {
                            visionData = visionData + "," + visionData + arrayMap.get("label")
                        }
                    }
                }

                Log.v("VisionDataToBacked", visionData)
//                val intent = Intent(activity!!, SearchActivity::class.java)
                val intent = Intent(activity!!, FavouritesActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 3)
                intent.putExtra("visionData", visionData)
                startActivity(intent)
            } catch (e: Exception) {
                VerkoopApplication.instance.loader.hide(activity!!)
//                Utils.showSimpleMessage(homeActivity, getString(R.string.try_again_later)).show()
                Toast.makeText(activity!!, getString(R.string.try_again_later), Toast.LENGTH_SHORT)
                e.printStackTrace()
            }
        })

        thread.start()
    }
}