package com.verkoopapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_cash.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class CashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash)
        cash_tool_lyt.tvHeaderLoc.text = getString(R.string.cash)
        cash_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }
    }
}