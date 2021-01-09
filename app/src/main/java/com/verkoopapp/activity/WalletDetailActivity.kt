package com.verkoopapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_wallet_detail.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class WalletDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_detail)

        wallet_tool_lyt.tvHeaderLoc.text = getString(R.string.wallet)
        wallet_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }


    }
}