package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.CarsFilterActivity
import com.verkoopapp.models.CarType
import com.verkoopapp.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.details_sub_category.*


class CarBodyTypeAdapter(private val context: Context, private val recyclerView: Int,private val carTypeLIst: ArrayList<CarType>,private val comingFrom:Int):RecyclerView.Adapter<CarBodyTypeAdapter.ViewHolder>(){
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view=mLayoutInflater.inflate(R.layout.details_sub_category,parent,false)
        val params = view.layoutParams
        params.width = (recyclerView-60 )/ 3
        params.height = params.width
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
      return carTypeLIst.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val modal=carTypeLIst[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer {
        fun bind(data: CarType) {
            tvSubCategoryPost.text=data.name
            if (!TextUtils.isEmpty(data.image)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL+data.image)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.setting)
                        .into(ivSubCategory)
            }
            itemView.setOnClickListener {
                if(comingFrom!=1) {
                    val intent = Intent(context, CarsFilterActivity::class.java)
                    intent.putExtra(AppConstants.FILTER_ID, data.id)
                    intent.putExtra(AppConstants.FILTER_TYPE, context.getString(R.string.brand))
                    intent.putExtra(AppConstants.TYPE, 1)
                    context.startActivity(intent)
                }else{
                    val intent = Intent(context, CarsFilterActivity::class.java)
                    intent.putExtra(AppConstants.FILTER_ID, data.id)
                    intent.putExtra(AppConstants.FILTER_TYPE, context.getString(R.string.zone))
                    intent.putExtra(AppConstants.TYPE, 2)
                    context.startActivity(intent)
                }
            }
        }

    }

}