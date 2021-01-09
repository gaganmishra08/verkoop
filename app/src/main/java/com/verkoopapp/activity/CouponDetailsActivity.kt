package com.verkoopapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_coupon_details.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class CouponDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_details)

        coupon_tool_lyt.tvHeaderLoc.text = getString(R.string.coupon_details)
        coupon_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }
    }
}