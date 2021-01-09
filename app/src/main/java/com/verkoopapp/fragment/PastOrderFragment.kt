package com.verkoopapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.activity.FoodHomeActivity
import com.verkoopapp.adapter.PastOrderAdapter
import kotlinx.android.synthetic.main.adds_category_row.*
import kotlinx.android.synthetic.main.fragment_past_order.*


class PastOrderFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_past_order.layoutManager = linearLayoutManager
        val pastOrderAdapter = PastOrderAdapter(context!!)
        rv_past_order.setHasFixedSize(true)
        rv_past_order.adapter = pastOrderAdapter
        rv_past_order!!.adapter!!.notifyDataSetChanged()
    }

}