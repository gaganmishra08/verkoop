package com.verkoopapp.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.adapter.CategoryAdapter
import com.verkoopapp.R
import com.verkoopapp.activity.CategoriesActivity
import com.verkoopapp.models.DataCategory
import com.verkoopapp.utils.AppConstants.CATEGORY_LIST
import com.verkoopapp.utils.AppConstants.PAGER_POSITION
import kotlinx.android.synthetic.main.first_category.llParentCate
import kotlinx.android.synthetic.main.first_category.rvCategoryList

class FirstCategoryFragment : Fragment() {
    private lateinit var categoriesActivity: CategoriesActivity
    private var categoryList=ArrayList<DataCategory>()
    private var position=0


    override fun onAttach(context: Context) {
        super.onAttach(context)
        categoriesActivity = activity as CategoriesActivity
        categoryList= arguments!!.getParcelableArrayList(CATEGORY_LIST)!!
        position= arguments!!.getInt(PAGER_POSITION)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.first_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setAdapter(categoryList)
    }


//    private fun setAdapter(categoryList: ArrayList<DataCategory>) {
//        val linearLayoutManager = GridLayoutManager(context, 3)
//        rvCategoryList.layoutManager = linearLayoutManager
//        val categoriesAdapter= CategoryAdapter(categoriesActivity, categoryList, position,llParentCate)
//        rvCategoryList.adapter=categoriesAdapter
//    }

    companion object {
        fun newInstance(id: Int, categoryList: ArrayList<DataCategory>): FirstCategoryFragment {
            val args = Bundle()
            args.putInt(PAGER_POSITION, id)
            args.putParcelableArrayList(CATEGORY_LIST, categoryList)
            val fragment = FirstCategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}