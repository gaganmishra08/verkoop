package com.verkoopapp.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.FullCategoriesActivity
import com.verkoopapp.models.SubCategory
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.sub_category.*


class SubCategoryAdapter(private  var context: Context,private val subCategoryList: ArrayList<SubCategory>,private var categoryId:Int):RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>(){
     lateinit var selectedSubcategory: SelectedSubcategory
    private val mInflater:LayoutInflater= LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
       val view= mInflater.inflate(R.layout.sub_category,parent,false)
        selectedSubcategory=context as FullCategoriesActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {

        var list=subCategoryList[position]
        holder.bind(list)
    }
    inner class ViewHolder(override val containerView: View):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind(subCategory: SubCategory) {
            tvSubCategory.text=subCategory.name
            tvSubCategory.setOnClickListener {
                selectedSubcategory.subCategoryName(subCategory.id,subCategory.name,categoryId)
            }
        }

    }
    interface SelectedSubcategory{
        fun subCategoryName(subCategoryId:Int,subCategoryName:String,categoryId:Int)
    }
}