package com.verkoopapp.adapter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.verkoopapp.R
import com.verkoopapp.models.PropertyTypeRequest
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_property.*


class PropertyTypeAdapter(private val context:Context,private val propertyList:ArrayList<PropertyTypeRequest>):RecyclerView.Adapter<PropertyTypeAdapter.ViewHolder>(){
    private var mInflater: LayoutInflater= LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
     val view=mInflater.inflate(R.layout.row_property,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
      return propertyList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
       val modal=propertyList[position]
        holder.bind(modal)
    }
    inner class ViewHolder(override val containerView: View):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind(modal: PropertyTypeRequest) {
            tvName.text=modal.name
            if(modal.isSelected){
                tvName.background= ContextCompat.getDrawable(context,R.drawable.red_rectangle_shape)
                tvName.setTextColor(ContextCompat.getColor(context,R.color.white))
            }else{
                tvName.background=ContextCompat.getDrawable(context,R.drawable.gray_rectangular_shape)
                tvName.setTextColor(ContextCompat.getColor(context,R.color.dark_black))
            }
            tvName.setOnClickListener {
                modal.isSelected=true
                setDeselection(adapterPosition)
                tvName.background=ContextCompat.getDrawable(context,R.drawable.gray_rectangular_shape)
                tvName.setTextColor(ContextCompat.getColor(context,R.color.dark_black))
            }
        }
    }

    private fun setDeselection(adapterPosition: Int) {
    for (i in propertyList.indices){
        if(i!=adapterPosition){
            propertyList[i].isSelected=false
        }
    }
        notifyDataSetChanged()
    }
}