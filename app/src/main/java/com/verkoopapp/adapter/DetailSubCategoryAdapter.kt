package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.CategoryDetailsActivity
import com.verkoopapp.models.SubCategoryPost
import com.verkoopapp.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.details_sub_category.*
import kotlinx.android.synthetic.main.toolbar_search_user.*


class DetailSubCategoryAdapter(private val context: Context, private val recyclerView: RecyclerView, private val subCategoryList: ArrayList<SubCategoryPost>) : RecyclerView.Adapter<DetailSubCategoryAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.details_sub_category, parent, false)
        val params = view.layoutParams
        params.width = recyclerView.width / 3
        params.height = params.width
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = subCategoryList[position]
        holder.bind(data)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(data: SubCategoryPost) {
            tvSubCategoryPost.text = data.name
            itemView.setOnClickListener {
                itemView.isEnabled = false
                Handler().postDelayed(Runnable {
                    itemView.isEnabled = true
                }, 1000)
                val intent = Intent(context, CategoryDetailsActivity::class.java)
                intent.putExtra(AppConstants.CATEGORY_ID, data.id)
                intent.putExtra(AppConstants.SUB_CATEGORY, data.name)
                intent.putExtra(AppConstants.TYPE, 1)
                intent.putExtra(AppConstants.Search, "")

                (context as CategoryDetailsActivity).startActivityForResult(intent, 2)
            }
        }
    }
}