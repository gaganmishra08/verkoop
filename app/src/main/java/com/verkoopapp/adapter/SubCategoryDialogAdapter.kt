package com.verkoopapp.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.SelectCategoryDialogActivity
import com.verkoopapp.models.SubCategory
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.sub_category_dialog_row.*

class SubCategoryDialogAdapter(private val context: Context, private val subCategoryList: List<SubCategory>,private val categoryId:Int) : RecyclerView.Adapter<SubCategoryDialogAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var selectedCategory: SelectedCategory
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.sub_category_dialog_row, parent, false)
        selectedCategory=context as SelectCategoryDialogActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal=subCategoryList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(modal: SubCategory) {
            if(adapterPosition==subCategoryList.size-1){
                viewSub.visibility=View.GONE
            }else{
                viewSub.visibility=View.VISIBLE
            }
            tvSubCategoryName.text=modal.name
            itemView.setOnClickListener {
                selectedCategory.subCategoryName(modal.name,modal.id,categoryId)
            }
        }

    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    interface SelectedCategory{
        fun subCategoryName(name:String,subCategoryId: Int,categoryId:Int)
    }
}