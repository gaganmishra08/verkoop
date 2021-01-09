package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.GalleryAdapter
import com.verkoopapp.customgallery.Define
import com.verkoopapp.customgallery.PickerController
import com.verkoopapp.customgallery.SingleMediaScanner
import com.verkoopapp.models.AddItemRequest
import com.verkoopapp.models.ImageModal
import com.verkoopapp.models.SelectedImage
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.CommonUtils
import com.verkoopapp.utils.PermissionCheck
import kotlinx.android.synthetic.main.gallery_activity.*
import kotlinx.android.synthetic.main.toolbar_filter.*
import java.io.File


class GalleryActivity : AppCompatActivity(), GalleryAdapter.ImageCountCallBack {
    private var define = Define()
    private var selectcount: Int = 0
    private var comingFrom: Int = 0
    private lateinit var itemAdapter: GalleryAdapter
    private lateinit var pickerController: PickerController
    private var selectedImageList = ArrayList<ImageModal>()
    private val imageUris = ArrayList<ImageModal>()
    private var addItemsRequest: AddItemRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)
        pickerController = PickerController(this)
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        setData()
        setAdapter()

    }

    private fun updateData() {
        var totalSize = 0
        selectedImageList = intent.getParcelableArrayListExtra(AppConstants.SELECTED_LIST)!!
        if (selectedImageList.size > 0) {
            for (i in selectedImageList.indices) {
                if (!TextUtils.isEmpty(selectedImageList[i].imageUrl)) {
                    if (selectedImageList[i].iseditable) {
                        selectcount += 1
                        imageUris[selectedImageList[i].imagePosition].isSelected = true
                        imageUris[selectedImageList[i].imagePosition].countSelect = selectcount
                    } else {
                        totalSize += 1
                    }
                }
            }
        }
        itemAdapter.updateImageCount(selectcount, totalSize)
        itemAdapter.notifyDataSetChanged()
        if (selectcount > 0) {
            tvSelectedCount.text = StringBuilder().append(" ").append(selectcount.toString()).append(" ").append("/ 10")
            tvChatGallery.visibility = View.VISIBLE
        } else {
            tvSelectedCount.text = StringBuilder().append(" ").append(getString(R.string.multiple))
            tvChatGallery.visibility = View.INVISIBLE
        }
    }

    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(this, 3)
        rvGallery.layoutManager = linearLayoutManager
        itemAdapter = GalleryAdapter(this, llParentGallery, pickerController, pickerController.getPathDir(0))
        rvGallery.adapter = itemAdapter
        Log.e("TAG", "setAdapter: "+checkPermission())
        if (checkPermission()) {
            pickerController.displayImage(0, true)
        }

    }

    fun setAdapterData(result: ArrayList<ImageModal>) {
        imageUris.clear()
        imageUris.addAll(result)
        itemAdapter.setData(imageUris)
        itemAdapter.notifyDataSetChanged()
        if (comingFrom == 1) {
            updateData()
        }
    }

    private fun setData() {
        iv_leftGallery.setOnClickListener {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }
        tvChatGallery.setOnClickListener {
            Log.e("<<NextClick>>", "clickedOnce")
            if (comingFrom == 1) {
                setResultData()

            } else {
                setIntentData()
            }
        }
        tvHeaderGallery.text = getString(R.string.photos)
        tvChatGallery.text = getString(R.string.next_s)
        tvChatGallery.visibility = View.INVISIBLE

    }

    private fun setResultData() {
        var selectedList = ArrayList<SelectedImage>()
        for (i in selectedImageList.indices) {
            if (!selectedImageList[i].iseditable && !TextUtils.isEmpty(selectedImageList[i].imageUrl)) {
                val selectedImage = SelectedImage(selectedImageList[i].imageUrl, i, false, selectedImageList[i].imageId)
                selectedList.add(selectedImage)
                selectcount += 1
            }
        }
        for (i in imageUris.indices) {
            if (selectedList.size < selectcount) {
                if (imageUris[i].isSelected) {
                    val selectedImage = SelectedImage(imageUris[i].imageUrl, i, true, 0)
                    selectedList.add(selectedImage)
                }
                if (selectcount == selectedList.size) {
                    val returnIntent = Intent()
                    returnIntent.putParcelableArrayListExtra(AppConstants.SELECTED_LIST, selectedList)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    break
                }
            } else {
                val returnIntent = Intent()
                returnIntent.putParcelableArrayListExtra(AppConstants.SELECTED_LIST, selectedList)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
                break
            }
        }

    }

    private fun setIntentData() {
        val selectedList = ArrayList<SelectedImage>()
        for (i in imageUris.indices) {
            if (selectedList.size < selectcount) {
                if (imageUris[i].isSelected) {
                    val selectedImage = SelectedImage(imageUris[i].imageUrl, i, true, imageUris[i].imageId)
                    selectedList.add(selectedImage)
                }
                if (selectcount == selectedList.size) {
                    val intent = Intent(this, AddDetailsActivity::class.java)
                    intent.putParcelableArrayListExtra(AppConstants.SELECTED_LIST, selectedList)
                    intent.putExtra(AppConstants.POST_DATA, addItemsRequest)
                    startActivityForResult(intent, 1)
                    break
                }
            } else {
                val intent = Intent(this, AddDetailsActivity::class.java)
                intent.putParcelableArrayListExtra(AppConstants.SELECTED_LIST, selectedList)
                intent.putExtra(AppConstants.POST_DATA, addItemsRequest)
                startActivityForResult(intent, 1)
                break
            }
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == define.TAKE_A_PICK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val savedFile = File(pickerController.getSavePath())
                    SingleMediaScanner(this, savedFile)
                    addImage(savedFile)
                    val contentURI = CommonUtils.getImageContentUri(this@GalleryActivity, savedFile)
                    Log.e("ImageContentURi", contentURI.toString())
                } else {
                    File(pickerController.getSavePath()).delete()
                }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION, 0)
                if (result == 1) {
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.TRANSACTION, result)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                } else if (result == 2) {
                    addItemsRequest = null
                    addItemsRequest = data.getParcelableExtra(AppConstants.POST_DATA)
                    val rejectList = data.getIntegerArrayListExtra(AppConstants.REJECT_LIST)
                    if (rejectList != null && rejectList.size > 0) {
                        itemAdapter.updateAdapter(rejectList)
                        itemAdapter.notifyDataSetChanged()
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    private fun addImage(path: File) {
        val imagemodal = ImageModal(CommonUtils.getImageContentUri(this@GalleryActivity, path).toString(), false, false, 0, 0, true, 0)
        imageUris.add(1, imagemodal)
        itemAdapter.notifyDataSetChanged()
        pickerController.setAddImagePath(Uri.fromFile(path))
    }

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            28 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults.size == 2) {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                            val pickerController = PickerController(this)
                            pickerController.displayImage(0, true)
                        } else {
                            checkPermission()
                            PermissionCheck(this).showPermissionDialog()
                            // finish()
                        }
                    } else {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            val pickerController = PickerController(this)
                            pickerController.displayImage(0, true)
                        } else {
                            checkPermission()
                            PermissionCheck(this).showPermissionDialog()
                            // finish()
                        }
                    }
                }
            }
        }
    }

    override fun imageCount(count: Int) {
        selectcount = count
        if (count > 0) {
            tvSelectedCount.text = StringBuilder().append(" ").append(count.toString()).append(" ").append("/ 10")
            tvChatGallery.visibility = View.VISIBLE
        } else {
            tvSelectedCount.text = StringBuilder().append(" ").append(getString(R.string.multiple))
            tvChatGallery.visibility = View.INVISIBLE
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }
}