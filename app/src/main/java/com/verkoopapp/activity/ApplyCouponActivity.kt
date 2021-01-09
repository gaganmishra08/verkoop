package com.verkoopapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_apply_coupon.view.*
import kotlinx.android.synthetic.main.activity_coupon_details.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class ApplyCouponActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_coupon)

        coupon_tool_lyt.tvHeaderLoc.text = getString(R.string.apply_coupon)
        coupon_tool_lyt.tvLogin.setOnClickListener {
            finish()
        }
        coupon_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }
    }
}