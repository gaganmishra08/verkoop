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
import com.verkoopapp.models.Brand
import com.verkoopapp.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_home_row.*


class BrandListAdapter(private val context: Context, private val rvBrandList: Int, private val brandsList: ArrayList<Brand>) : RecyclerView.Adapter<BrandListAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.category_home_row, parent, false)
        val params = view.layoutParams
        params.width = (rvBrandList - 60) / 3
        params.height = params.width - 40
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return brandsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = brandsList[position]
        holder.bind(data, position)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: Brand, position: Int) {
            tvLevelHome.text = data.name
            if (!TextUtils.isEmpty(data.image)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL + data.image)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.setting)
                        .into(ivItemsHome)
            } else{
                ivItemsHome.setImageDrawable(context.resources.getDrawable(R.mipmap.setting))
            }
            itemView.setOnClickListener {
                val intent = Intent(context, CarsFilterActivity::class.java)
                intent.putExtra(AppConstants.FILTER_ID, data.id)
                intent.putExtra(AppConstants.FILTER_TYPE, context.getString(R.string.brand_))
                intent.putExtra(AppConstants.TYPE, 1)
                context.startActivity(intent)
            }
        }

    }
}