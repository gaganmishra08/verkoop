package com.verkoopapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_package_detail.*

class PackageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_detail)
        tv_payment_type.setOnClickListener{
            val intent = Intent(this, PaymentDetailActivity::class.java)
            startActivity(intent)
        }
        img_back.setOnClickListener {
            finish()
        }
    }
}