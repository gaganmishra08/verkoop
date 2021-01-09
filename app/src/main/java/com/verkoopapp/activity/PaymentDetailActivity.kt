package com.verkoopapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_payment_detail.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class PaymentDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_detail)

        payment_tool_lyt.tvHeaderLoc.text = getString(R.string.payment)
        payment_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }
        wallet_lyt.setOnClickListener {
            val intent = Intent(this, WalletDetailActivity::class.java)
            startActivity(intent)
        }
        credit_card_lyt.setOnClickListener {
            val intent = Intent(this, CreditCardDetailActivity::class.java)
            startActivity(intent)
        }
        cash_lyt.setOnClickListener {
            val intent = Intent(this, CashActivity::class.java)
            startActivity(intent)
        }

    }
}