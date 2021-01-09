package com.verkoopapp.adapter

import android.content.Intent
import android.net.Uri
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.*
import com.verkoopapp.models.Category
import com.verkoopapp.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_home_row.*
import kotlinx.android.synthetic.main.category_row.*
import kotlinx.android.synthetic.main.my_profile_details_row.*


class CategoryListAdapter(private val context: HomeActivity, private var categoryList: ArrayList<Category>, private val rvCategoryHome: Int): RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    val TAG = CategoryListAdapter::class.java.simpleName.toString()
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.category_home_row, parent, false)
        val params = view.layoutParams
        params.width = (rvCategoryHome-70) / 3
        params.height = params.height
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = categoryList[position]
        holder.bind(data,position)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }


    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        fun bind(data: Category, position: Int) {
            tvLevelHome.text=data.name
            if (!TextUtils.isEmpty(data.image)) {

//                GlideToVectorYou
//                        .init()
//                        .with(context)
//                        .setPlaceHolder(R.drawable.ic_settings, R.drawable.ic_settings)
//                        .load(Uri.parse(AppConstants.IMAGE_URL+data.image), ivItemsHome)

                Log.e(TAG, "bind: "+data.image)
                Picasso.with(context).load(AppConstants.IMAGE_URL+data.image)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.drawable.ic_settings)
                        .into(ivItemsHome)

//                Picasso.with(context).load(AppConstants.IMAGE_URL+data.image)
//                        .resize(720, 720)
//                        .centerInside()
//                        .error(R.mipmap.setting)
//                        .into(ivItemsHome)
            }
            itemView.setOnClickListener {
                itemView.isEnabled=false
                Handler().postDelayed(Runnable {
                    itemView.isEnabled = true
                }, 700)
                when {
                    data.id==85 -> {
                        val intent = Intent(context, BuyCarsActivity::class.java)
                        context.startActivityForResult(intent, 2)

                    }
                    data.id==24 -> {
                        val intent = Intent(context, BuyPropertiesActivity::class.java)
                        context.startActivityForResult(intent, 2)
                    }
                    else -> {
                        val intent = Intent(context, CategoryDetailsActivity::class.java)
                        intent.putExtra(AppConstants.CATEGORY_ID, data.id)
                        intent.putExtra(AppConstants.TYPE, 0)
                        intent.putExtra(AppConstants.SUB_CATEGORY, data.name)
                        context.startActivityForResult(intent, 2)
                        intent.putExtra(AppConstants.Search, "")
                    }
                }
            }
        }
    }

}