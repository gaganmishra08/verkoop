package com.verkoopapp.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.verkoopapp.R
import com.verkoopapp.activity.AddDetailsActivity
import com.verkoopapp.activity.GalleryActivity
import com.verkoopapp.models.ImageModal
import com.verkoopapp.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.image_list_row.*


class SelectedImageAdapter(private val context: Activity, private var selectedImageList: ArrayList<ImageModal>, private var flList: FrameLayout, private var listSize: Int, private var comingFrom: Int) : RecyclerView.Adapter<SelectedImageAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var selectedImageCount: SelectedImageCount

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.image_list_row, parent, false)
        val params = view.layoutParams
        params.width = flList.width / 3
        params.height = params.width
        view.layoutParams = params
        selectedImageCount = context as AddDetailsActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return selectedImageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modalList = selectedImageList[position]
        holder.bind(modalList)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(modalList: ImageModal) {
            if (modalList.type) {
                flGallery.visibility = View.VISIBLE
                flImage.visibility = View.GONE
                flClose.visibility = View.GONE
            } else {
                flGallery.visibility = View.GONE
                flImage.visibility = View.VISIBLE
                flClose.visibility = View.VISIBLE
            }
            if (modalList.iseditable) {
                if (!TextUtils.isEmpty(modalList.imageUrl)) {
                    Glide.with(context).load(Uri.parse(modalList.imageUrl))
                            .placeholder(R.mipmap.gallery_place)
                            .error(R.mipmap.gallery_place)
                            .into(ivImageDetail)

                }
            } else {
                if (!TextUtils.isEmpty(modalList.imageUrl)) {
                    Glide.with(context).load(Uri.parse(AppConstants.IMAGE_URL + modalList.imageUrl))
                            .placeholder(R.mipmap.gallery_place)
                            .error(R.mipmap.gallery_place)
                            .into(ivImageDetail)

                }
            }
            /*flGallery.setOnClickListener {
                Utils.showToast(context, "cameraClick")
            }*/
            flClose.setOnClickListener {
                listSize--
                setData(modalList.imagePosition, modalList.imageId)
                selectedImageList.remove(modalList)

            }
            flGallery.setOnClickListener {
                flGallery.isEnabled = false
                Handler().postDelayed(Runnable {
                    flGallery.isEnabled = true
                }, 700)
                if (comingFrom == 1) {
                    val intent = Intent(context, GalleryActivity::class.java)
                    intent.putParcelableArrayListExtra(AppConstants.SELECTED_LIST, selectedImageList)
                    intent.putExtra(AppConstants.COMING_FROM, comingFrom)
                    context.startActivityForResult(intent, 2)
                } else {
                    context.onBackPressed()
                }
            }
        }

        private fun setData(selectPosition: Int, imageId: Int) {
            val imageModal = ImageModal("", false, true, 0, 0, false, 0)
            if (!selectedImageList.contains(imageModal)) {
                selectedImageList.add(imageModal)
            }
            notifyDataSetChanged()
            selectedImageCount.selectDetailCount(listSize, selectPosition, imageId)
        }
    }

    interface SelectedImageCount {
        fun selectDetailCount(count: Int, position: Int, imageId: Int)
    }
}