package com.verkoopapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.OngoingTripAdapter
import com.verkoopapp.adapter.SelectDriverAdapter
import kotlinx.android.synthetic.main.activity_select_driver.*
import kotlinx.android.synthetic.main.fragment_ongoing_trips.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class SelectDriverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_driver)

        driver_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()

        }

        driver_tool_lyt.tvHeaderLoc.text = getString(R.string.select_driver)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvDriverList.layoutManager = linearLayoutManager
        val ongoingTripAdapter = SelectDriverAdapter(this)
        rvDriverList.setHasFixedSize(true)
        rvDriverList.adapter = ongoingTripAdapter
        rvDriverList!!.adapter!!.notifyDataSetChanged()

    }
}