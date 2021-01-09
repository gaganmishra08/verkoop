package com.verkoopapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.OngoingOrderAdapter
import com.verkoopapp.adapter.PastOrderAdapter
import kotlinx.android.synthetic.main.fragment_ongoing_order.*
import kotlinx.android.synthetic.main.fragment_past_order.*


class OngoingOrderFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ongoing_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_ongoing_order.layoutManager = linearLayoutManager
        val ongoingOrderAdapter = OngoingOrderAdapter(context!!)
        rv_ongoing_order.setHasFixedSize(true)
        rv_ongoing_order.adapter = ongoingOrderAdapter
        rv_ongoing_order!!.adapter!!.notifyDataSetChanged()
    }
}