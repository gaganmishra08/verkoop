package com.verkoopapp.fragment

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.FoodHomeActivity
import com.verkoopapp.adapter.CouponsAdapter
import kotlinx.android.synthetic.main.fragment_food_coupon_fragmnet.*


class FoodCouponFragmnet : BaseFragment(){

    val TAG = FoodCouponFragmnet::class.java.simpleName.toString()
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
        return inflater.inflate(R.layout.fragment_food_coupon_fragmnet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvCouponsList.layoutManager = linearLayoutManager
        val couponsAdapter = CouponsAdapter(context!!)
        rvCouponsList.setHasFixedSize(true)
        rvCouponsList.adapter = couponsAdapter
        rvCouponsList!!.adapter!!.notifyDataSetChanged()
    }


    companion object {
        fun newInstance(): FoodCouponFragmnet {
            val args = Bundle()
            val fragment = FoodCouponFragmnet()
            fragment.arguments = args
            return fragment
        }
    }

}