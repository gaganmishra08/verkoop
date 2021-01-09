package com.verkoopapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_add_new_address.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class FindResturantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_resturant)


        new_tool_lyt.tvHeaderLoc.text = getString(R.string.new_address)

        new_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }

    }
}