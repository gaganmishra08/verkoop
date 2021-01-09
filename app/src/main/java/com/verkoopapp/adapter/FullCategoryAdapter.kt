package com.verkoopapp.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.FullCategoriesActivity
import com.verkoopapp.models.DataCategory
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.full_categories_row.*
import java.util.*


class FullCategoryAdapter(private val context: Context, private var categoryList: ArrayList<DataCategory>) : RecyclerView.Adapter<FullCategoryAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
     private lateinit var selectedCategory:SelectedCategory

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.full_categories_row, parent, false)
        selectedCategory=context as FullCategoriesActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = categoryList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(modal: DataCategory) {
            val mManager = LinearLayoutManager(context)
            rvSubCategoriesName.layoutManager = mManager
            val subCategoryAdapter = SubCategoryAdapter(context,modal.sub_category,modal.id)
            rvSubCategoriesName.isNestedScrollingEnabled = false
            rvSubCategoriesName.adapter = subCategoryAdapter
            tvCategoryName.text = modal.name
            tvCategoryName.setOnClickListener {selectedCategory.categoryName(modal.id,modal.name)  }
        }
    }
    interface SelectedCategory{
        fun categoryName(categoryId:Int,categoryName:String)
    }
}