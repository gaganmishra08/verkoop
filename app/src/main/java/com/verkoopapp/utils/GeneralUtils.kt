package com.verkoopapp.utils

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import com.github.twocoffeesoneteam.glidetovectoryou.GlideApp
import com.verkoopapp.R
import com.verkoopapp.databinding.DialogAppImageBinding


object GeneralFunctions {


}

private var mAppDialogImage: android.app.AlertDialog? = null


fun showFullImageDialog(
        activity: Activity?,
        imageUrl: String?
) {
    if (activity != null) {
        if (mAppDialogImage != null && mAppDialogImage!!.isShowing) {
            mAppDialogImage!!.dismiss()
        }
        val binding: DialogAppImageBinding = DataBindingUtil.inflate(
                activity.layoutInflater,
                R.layout.dialog_app_image,
                null,
                false
        )
        val builder: android.app.AlertDialog.Builder?
        builder = android.app.AlertDialog.Builder(activity)
        //set view
        builder.setView(binding.root)

        GlideApp.with(binding.ivFullImage.context).load(imageUrl)
                .into(binding.ivFullImage)

        binding.ibClose.setOnClickListener {
            mAppDialogImage!!.dismiss()
        }


        builder.setCancelable(false)

        mAppDialogImage = builder.create()
        if (mAppDialogImage!!.window != null) {
            mAppDialogImage!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mAppDialogImage!!.show()
        }
    }
}


interface AppDialogClickListeners {
    fun onPositiveClick()
    fun onNegativeClick()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}



