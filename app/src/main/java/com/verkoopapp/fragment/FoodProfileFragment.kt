package com.verkoopapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.FoodHomeActivity


class FoodProfileFragment : BaseFragment(){

    val TAG = FoodProfileFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: FoodHomeActivity


    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity = context as FoodHomeActivity
    }

    override fun getFragmentTag(): String? {
        return TAG
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_profile, container, false)
    }

    companion object{
        fun newInstance(): FoodProfileFragment{
            val args = Bundle()
            val fragment=  FoodProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }



}