package com.verkoopapp.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.CategoriesActivity
import com.verkoopapp.models.DataCategory
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_row.*


class CategoryAdapter(private var context: Context, private val categoryList: ArrayList<DataCategory>, private val llParentCate: LinearLayout) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    lateinit var selectedCategory: SelectedCategory
    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(
                R.layout.category_row, parent, false)
        selectedCategory = context as CategoriesActivity
//        val params = llParentCate.layoutParams
//        params.width = llParentCate.width / 3
//        params.height = params.width
//        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val data = categoryList[position]
        holder.bind(data, position)

    }


    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        fun bind(data: DataCategory, position: Int) {
            tvLevel.text = data.name
            if (!TextUtils.isEmpty(data.image)) {

//                GlideToVectorYou
//                        .init()
//                        .with(context)
//                        .setPlaceHolder(R.drawable.ic_settings, R.drawable.ic_settings)
//                        .load(Uri.parse(AppConstants.IMAGE_URL+data.image), ivItemsCategory)
                Picasso.with(context).load(data.image)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.drawable.ic_settings)
                        .into(ivItemsCategory)
            }
            llParent.setOnClickListener {
                if (data.isSelected) {
                    data.isSelected = false
                    tvLevel.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                    // ivItems.setImageResource(data.unselectedImage)
                    ivSelected.visibility = View.GONE
                    selectedCategory.selectedCount(checkSelectionCount())
                } else {
//                    if (checkSelectionCount() < 3) {
                    data.isSelected = true
                    tvLevel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    //   ivItems.setImageResource(data.selectedImage)
                    ivSelected.visibility = View.VISIBLE
                    selectedCategory.selectedCount(checkSelectionCount())
//                    } else {
//                        Utils.showSimpleMessage(context, context.getString(R.string.three_category)).show()
//                    }
                }
            }
        }
    }

    private fun checkSelectionCount(): Int {
        var selectionCount = 0
        for (i in categoryList.indices) {
            if (categoryList[i].isSelected) {
                selectionCount++
            }
        }
        return selectionCount
    }

    interface SelectedCategory {
        fun selectedCount(addItem: Int)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}