package com.verkoopapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.FavroutiesFoodAdapter
import com.verkoopapp.adapter.SaveAdressAdapter
import kotlinx.android.synthetic.main.activity_delivery_detail.*
import kotlinx.android.synthetic.main.activity_favourites_food.*
import kotlinx.android.synthetic.main.activity_saved_address.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class SavedAddressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_address)

        save_address_tool_lyt.tvHeaderLoc.text = getString(R.string.save_address)

        save_address_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_save_address_list.layoutManager = linearLayoutManager
        val saveAddressAdapter = SaveAdressAdapter(this)
        rv_save_address_list.setHasFixedSize(true)
        rv_save_address_list.adapter = saveAddressAdapter
        rv_save_address_list!!.adapter!!.notifyDataSetChanged()

    }
}