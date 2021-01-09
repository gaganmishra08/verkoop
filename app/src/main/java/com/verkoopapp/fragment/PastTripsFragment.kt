package com.verkoopapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.adapter.OngoingTripAdapter
import com.verkoopapp.adapter.PastTripAdapter
import kotlinx.android.synthetic.main.fragment_ongoing_trips.*
import kotlinx.android.synthetic.main.fragment_past_trips.*


class PastTripsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_past_trips.layoutManager = linearLayoutManager
        val pastTripAdapter = PastTripAdapter(context!!)
        rv_past_trips.setHasFixedSize(true)
        rv_past_trips.adapter = pastTripAdapter
        rv_past_trips!!.adapter!!.notifyDataSetChanged()

    }

}