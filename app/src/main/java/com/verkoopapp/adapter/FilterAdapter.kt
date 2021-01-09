package com.verkoopapp.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.CategoryDetailsActivity
import com.verkoopapp.models.FilterModal
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.filter_row.*

class FilterAdapter(private val context: Context) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    var mInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var selectFilterCallBack:SelectFilterCallBack
    private var itemFilterList= ArrayList<FilterModal>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.filter_row, parent, false)
        selectFilterCallBack=context as CategoryDetailsActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemFilterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal=itemFilterList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(modal: FilterModal) {
            if(modal.type==1){
                ivRemove.visibility=View.INVISIBLE
            }else{
                ivRemove.visibility=View.VISIBLE
            }
            tvFilterType.text=StringBuilder().append(modal.FilterType).append(" ").append(modal.FilterName)
            ivRemove.setOnClickListener {
                selectFilterCallBack.removeFilter(modal.FilterType,adapterPosition)
            }
            itemView.setOnClickListener {
                selectFilterCallBack.onSelectingFilter()
            }

        }
    }

    fun showData(filterList: ArrayList<FilterModal>) {
        itemFilterList=filterList
    }
    interface SelectFilterCallBack{
        fun removeFilter(remove:String,position: Int)
        fun onSelectingFilter()
    }
}