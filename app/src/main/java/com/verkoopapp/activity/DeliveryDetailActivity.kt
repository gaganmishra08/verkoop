package com.verkoopapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_delivery_detail.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class DeliveryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_detail)

        delivery_tool_lyt.tvHeaderLoc.text = getString(R.string.delivery_details)

        delivery_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }
        tv_address.setOnClickListener {
            val intent = Intent(this, AddNewAddressActivity::class.java)
            startActivity(intent)
        }
        tv_save_address.setOnClickListener {
            val intent = Intent(this, SavedAddressActivity::class.java)
            startActivity(intent)

        }

        tv_curent_loc.setOnClickListener {
            val intent = Intent(this, FindResturantActivity::class.java)
            startActivity(intent)
        }
        txt_adress.setOnClickListener {
            val intent = Intent(this, FindResturantActivity::class.java)
            startActivity(intent)
        }
        txt_deliver_food.setOnClickListener {
            val intent = Intent(this, FindResturantActivity::class.java)
            startActivity(intent)
        }

    }
}