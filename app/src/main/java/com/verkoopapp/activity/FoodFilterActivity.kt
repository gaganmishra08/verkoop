package com.verkoopapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_food_filter.*
import kotlinx.android.synthetic.main.toolbar_chat.view.*
import kotlinx.android.synthetic.main.toolbar_chat.view.ivLeftLocation
import kotlinx.android.synthetic.main.toolbar_chat.view.tvHeaderLoc
import kotlinx.android.synthetic.main.toolbar_location.view.*

class FoodFilterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_filter)

        filter_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }
        filter_tool_lyt.tvHeaderRight.visibility=View.VISIBLE
        filter_tool_lyt.tvHeaderLoc.text = getString(R.string.filter)
        filter_tool_lyt.tvHeaderRight.text = getString(R.string.reset)

        tv_apply.setOnClickListener {
            val intent = Intent(this, FoodHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}