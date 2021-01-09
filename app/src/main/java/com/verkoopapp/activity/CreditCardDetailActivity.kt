package com.verkoopapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_credit_card_detail.*
import kotlinx.android.synthetic.main.toolbar_chat.view.*

class CreditCardDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card_detail)
        card_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }
        card_tool_lyt.tvHeaderLoc.text = getString(R.string.card_details)
    }
}