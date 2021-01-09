package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.verkoopapp.R
import com.verkoopapp.activity.OngoingOrderDetailActivity
import kotlinx.android.extensions.LayoutContainer

class FavroutiesFoodAdapter (private val context: Context): RecyclerView.Adapter<FavroutiesFoodAdapter.ViewHolder>() {
    val TAG = SaveAdressAdapter::class.java.simpleName.toString()
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_fav_food, parent, false)
        val params = view.layoutParams
        //params.width = (rvCategoryHome-70) / 3
        // params.height = params.width
        //  view.layoutParams = params
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val data = categoryList[position]
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 7
    }


    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        fun bind() {

        }
    }

}