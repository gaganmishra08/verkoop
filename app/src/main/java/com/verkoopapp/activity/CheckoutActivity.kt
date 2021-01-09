package com.verkoopapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import de.wirecard.paymentsdk.ui.activity.PaymentActivity
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class CheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        chekout_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }

        chekout_tool_lyt.tvHeaderLoc.text = getString(R.string.checkout)



        tv_change_address.setOnClickListener {
            val intent = Intent(this, AddNewAddressActivity::class.java)
            startActivity(intent)
        }

        tv_add_coupon.setOnClickListener {
            val intent = Intent(this, ApplyCouponActivity::class.java)
            startActivity(intent)
        }
        tv_place_order.setOnClickListener {
            val intent = Intent(this, PaymentDetailActivity::class.java)
            startActivity(intent)
        }
    }
}