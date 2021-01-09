package com.verkoopapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.adapter.OngoingOrderAdapter
import com.verkoopapp.adapter.OngoingTripAdapter
import kotlinx.android.synthetic.main.fragment_ongoing_order.*
import kotlinx.android.synthetic.main.fragment_ongoing_trips.*


class OngoingTripsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ongoing_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_ongoing_trips.layoutManager = linearLayoutManager
        val ongoingTripAdapter = OngoingTripAdapter(context!!)
        rv_ongoing_trips.setHasFixedSize(true)
        rv_ongoing_trips.adapter = ongoingTripAdapter
        rv_ongoing_trips!!.adapter!!.notifyDataSetChanged()

    }

}