package com.verkoopapp.customgallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


  class CameraUtil {
    companion object {
         var savePathReal: String? = null
    }


     fun takePictureCamera(activity: Activity, saveDir: String) {
         Log.e("TAG", "takePicture: "+saveDir)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
         Log.e("TAG", "takePictureCamera: "+takePictureIntent.resolveActivity(activity.packageManager))
//        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            Log.e("TAG", "takePictureCameraif: "+saveDir)
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                Log.e("TAG", "takePictureCamera: "+createImageFile(saveDir))
                photoFile = createImageFile(saveDir) //make a file

                Log.e("TAG", "takePictureCamera: "+photoFile)
                savePathReal = photoFile.absolutePath
                Log.e("TAG", "takePictureCamera: "+ savePathReal)
            }
//            catch (ex: IOException) {
//                ex.printStackTrace()
//
//                Log.e("TAG", "takePictureCameraex: "+ex.message)
//                // Error occurred while creating the File
//            }
            catch (exe: Exception) {
                exe.printStackTrace()

                Log.e("TAG", "takePictureCameraexe: "+exe.message)
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                val uri: Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(activity,
                            activity.applicationContext.packageName + ".provider", photoFile)
                } else {
                    uri = Uri.fromFile(photoFile)
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

                activity.startActivityForResult(takePictureIntent, Define().TAKE_A_PICK_REQUEST_CODE)
            }
//        }

    }

    @Throws(IOException::class)
    private fun createImageFile(saveDir: String): File {
        // Create an image file name
//        Log.e("TAG", "createImageFile: "+saveDir)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = File(saveDir)
        Log.e("TAG", "createImageFile: "+timeStamp)
        Log.e("TAG", "createImageFile: "+imageFileName)
        Log.e("TAG", "createImageFile: "+storageDir)

        return File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
    }
      internal fun getSavePath(): String? {
          return savePathReal
      }

      internal fun setSavePath(savePath: String) {
          savePathReal=savePath
      }
}
