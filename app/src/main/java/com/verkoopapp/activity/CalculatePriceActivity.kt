package com.verkoopapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_calculate_price.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class CalculatePriceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_price)

        calculate_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }

        calculate_tool_lyt.tvHeaderLoc.text = getString(R.string.calculate_price)
        tv_find_driver.setOnClickListener {
            val intent = Intent(this, SelectDriverActivity::class.java)
            startActivity(intent)
        }
    }
}