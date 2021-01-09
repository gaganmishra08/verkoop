package com.verkoopapp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import android.view.View
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.verkoopapp.R
import com.verkoopapp.utils.*
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.upload_banner_activity.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class UploadBannerActivity: AppCompatActivity() {
    private var uriTemp: Uri? = null
    private var mCurrentPhotoPath: String? = null
    private var categoryName:String?=null
    private var categoryId:Int=0

    companion object {
        internal const val SELECT_CATEGORY = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_banner_activity)
        setData()
    }

    private fun setData() {
        tvSelectCategory.setOnClickListener {
            val intent = Intent(this, SelectCategoryDialogActivity::class.java)
            startActivityForResult(intent, SELECT_CATEGORY)
            overridePendingTransition(0, 0)
        }
        ivRight.setImageResource(R.mipmap.get_coins)
        ivRight.visibility= View.INVISIBLE
        tvHeaderLoc.text = getString(R.string.featured_product)
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvUploadImage.setOnClickListener {
            addProfileImage()
        }
        tvSaveBanner.setOnClickListener {
            if(!TextUtils.isEmpty(mCurrentPhotoPath)) {
                if(categoryId!=0) {
                    val intent = Intent(this, AdvertPackagesActivity::class.java)
                    intent.putExtra(AppConstants.IMAGE_URL, mCurrentPhotoPath)
                    intent.putExtra(AppConstants.CATEGORY_ID, categoryId)
                    startActivity(intent)
                }else{
                    Utils.showSimpleMessage(this@UploadBannerActivity,getString(R.string.select_category_)).show()
                }
            }else{
                Utils.showSimpleMessage(this@UploadBannerActivity,getString(R.string.upload_banne)).show()
            }
           /* if(tvSaveBanner.text.toString().equals("NEXT",ignoreCase = true)) {
                val intent = Intent(this, AdvertPackagesActivity::class.java)
                startActivityForResult(intent,4)
            }else{
                if(!TextUtils.isEmpty(mCurrentPhotoPath)) {
                    updateProfileData()
                }else{
                    Utils.showSimpleMessage(this@UploadBannerActivity,getString(R.string.upload_banne)).show()
                }
            }*/
        }
        ivRight.setOnClickListener {
            val intent=Intent(this,AdvertPackagesActivity::class.java)
           startActivity(intent)
        }
    }

    private fun addProfileImage() {
        if (checkPermission()) {
            openPopUp()
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

    private fun openPopUp() {
        val shareDialog = SelectOptionDialog(this, object : SelectionOptionListener {
            override fun leaveClick(option: String) {
                if (option.equals(getString(R.string.camera), ignoreCase = true)) {
                    takePicture()
                } else if (option.equals(getString(R.string.gallery), ignoreCase = true)) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Pick From"), EditProfileActivity.REQUEST_GET_PHOTO)
                }
            }
        })
        shareDialog.show()
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
                startActivityForResult(takePictureIntent, EditProfileActivity.REQUEST_TAKE_PHOTO)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                EditProfileActivity.REQUEST_TAKE_PHOTO -> {

                    val f = File(mCurrentPhotoPath!!)
                    uriTemp = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", f)
                    CropImage.activity(uriTemp)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)
                }

                EditProfileActivity.REQUEST_GET_PHOTO -> {
                    uriTemp = data?.data
                    CropImage.activity(uriTemp)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    tvUploadImage.text="Change Banner"
                    val result = CropImage.getActivityResult(data)
                    mCurrentPhotoPath = Utils.getRealPathFromURI(this@UploadBannerActivity, result.uri)
                    val file = File(mCurrentPhotoPath!!)
                    Picasso.with(this).load(file)
                            .resize(1024, 1024)
                            .centerCrop()
                            .error(R.mipmap.gallery_place)
                            .placeholder(R.mipmap.gallery_place)
                            .into(ivBanner)
                }

                SELECT_CATEGORY -> {
                    if (resultCode === Activity.RESULT_OK) {
                        categoryName = data!!.getStringExtra(AppConstants.CATEGORY_NAME)
                        categoryId = data.getIntExtra(AppConstants.SUB_CATEGORY_ID, 0)
                        tvSelectCategory.text=categoryName
                        //parentId = data.getIntExtra(AppConstants.CATEGORY_ID, 0)
                       // val typeSelection = data.getIntExtra(AppConstants.SCREEN_TYPE, 0)

                    }
                }

            }
        }
    }



    private fun setDialogBox() {
            val shareDialog = WarningDialog(this,  object : SelectionListener {
                override fun leaveClick() {
                    onBackPressed()
                }
            })
            shareDialog.show()

    }
}