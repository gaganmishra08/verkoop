package com.verkoopapp.adapter

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.verkoopapp.R
import com.verkoopapp.activity.GalleryActivity
import com.verkoopapp.models.ImageModal
import com.verkoopapp.customgallery.PickerController
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.gallery_item.*



class GalleryAdapter(private var context: Activity, private var llParent: LinearLayout, private var pickerController: PickerController, private var saveDir:String) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var imageCountCallBack:ImageCountCallBack
    private var imageList = ArrayList<ImageModal>()
   // private var selectedList = ArrayList<String>()
    private var imageCount = 0
    private var totalSize = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  view = mInflater.inflate(R.layout.gallery_item, parent, false)
        val params = view.layoutParams
        params.width = llParent.width / 3
       // params.height = llParent.height / 5
        params.height = params.width
        view.layoutParams = params
        imageCountCallBack=context as GalleryActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = imageList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {

        fun bind(modal: ImageModal) {
            if (modal.isSelected) {
                llFrame.background = ContextCompat.getDrawable(context, R.drawable.rectangle_border_shape)
                llSelection.visibility = View.VISIBLE
                tvSelectCount.text=modal.countSelect.toString()
            } else {
                llFrame.background = ContextCompat.getDrawable(context, R.drawable.transparent_rect_shape)
                llSelection.visibility = View.GONE
            }
            if(modal.type){
                flCamera.visibility=View.VISIBLE
                flItem.visibility=View.GONE
            }else{
                flCamera.visibility=View.GONE
                flItem.visibility=View.VISIBLE
            }
           if(!TextUtils.isEmpty(modal.imageUrl)) {
               Glide.with(context).load(Uri.parse(modal.imageUrl))
                       .centerCrop()
                       .placeholder(R.mipmap.gallery_place)
                       .error(R.mipmap.gallery_place)
                       .override(750,750)
                       .into(ivImage)

           }
            flItem.setOnClickListener {
                if ((imageCount+totalSize) < 10) {
                    llFrame.background = ContextCompat.getDrawable(context, R.drawable.rectangle_border_shape)
                    llSelection.visibility = View.VISIBLE
                    if (modal.isSelected) {
                        val tempCount =  imageList[adapterPosition].countSelect
                        imageList[adapterPosition].countSelect=0
                  //      selectedList.remove(modal.imageUrl)
                        refreshData(tempCount)
                        imageCount -= 1
                    } else {
                        imageCount += 1
                 //       selectedList.add(modal.imageUrl)
                    }
                    modal.countSelect=imageCount
                    modal.isSelected=!modal.isSelected
                    notifyDataSetChanged()
                } else {
                    if (modal.isSelected) {
                        var tempCount =  imageList[adapterPosition].countSelect
                        modal.isSelected=!modal.isSelected
                        imageCount -= 1
                       // selectedList.remove(modal.imageUrl)
                        modal.countSelect=imageCount
                        refreshData(tempCount)
                    }else {
                        Utils.showToast(context, context.getString(R.string.select_10_images))
                    }
                }
                imageCountCallBack.imageCount(imageCount)
            }
            flCamera.setOnClickListener {
                Log.e("TAG", "bindcamera: "+saveDir)
                pickerController.takePicture(context, saveDir)
            }
        }


    }

    fun refreshData(tempCount: Int) {
        for (j in imageList.indices){
            if(imageList[j].isSelected&&imageList[j].countSelect>=tempCount){
                imageList[j].countSelect-=1
            }
        }

        /* for (i in selectedList.indices){
           for (j in imageList.indices)
               if(!TextUtils.isEmpty(imageList[j].imageUrl)) {
                   if (selectedList[i] == imageList[j].imageUrl) {
                       val imagemodal = ImageModal(imageList[j].imageUrl, imageList[j].isSelected, imageList[j].type, i + 1)
                       imageList[j] = imagemodal
                       //break
                   }
               }

         }*/

        notifyDataSetChanged()
        imageCountCallBack.imageCount(imageCount)
    }
   /* override fun getItemViewType(position: Int): Int {
        return position
    }*/

    fun setData(result: ArrayList<ImageModal>) {
        imageList = result
    }
    interface ImageCountCallBack{
       fun imageCount(count:Int)
    }

    fun updateAdapter(unselectedList: java.util.ArrayList<Int>) {
      for (i in unselectedList.indices){
          imageCount -= 1
          val tempCount =  imageList[unselectedList[i]].countSelect
          imageList[unselectedList[i]].countSelect=0
          refreshData(tempCount)
          imageList[unselectedList[i]].countSelect=imageCount
          imageList[unselectedList[i]].isSelected=!imageList[unselectedList[i]].isSelected
        }
        notifyDataSetChanged()
    }

    fun updateImageCount(selectCount: Int, listLise: Int) {
        imageCount=selectCount
        totalSize=listLise
    }
}