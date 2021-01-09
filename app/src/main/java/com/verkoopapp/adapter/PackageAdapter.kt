package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.CalculatePriceActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_packages.*

class PackageAdapter (private val context: Context) : RecyclerView.Adapter<PackageAdapter.ViewHolder>() {
    val TAG = PackageAdapter::class.java.simpleName.toString()
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_packages, parent, false)
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
        return 4
    }


    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        fun bind() {
            llParentItem.setOnClickListener {
                val intent = Intent(context, CalculatePriceActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

}