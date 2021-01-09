package com.verkoopapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_resturants_detail.*
import kotlinx.android.synthetic.main.layout_resturant_detail.view.*

class ResturantsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resturants_detail)

        restro_tool_lyt.img_loc.setOnClickListener {
            finish()
        }

        food_menu_1.setOnClickListener {
            val intent = Intent(this, FoodItemDetailActivity::class.java)
            startActivity(intent)
        }

        food_menu_3.setOnClickListener {
            val intent = Intent(this, FoodItemDetailActivity::class.java)
            startActivity(intent)
        }

        food_menu_2.setOnClickListener {
            val intent = Intent(this, FoodItemDetailActivity::class.java)
            startActivity(intent)
        }

        food_menu_4.setOnClickListener {
            val intent = Intent(this, FoodItemDetailActivity::class.java)
            startActivity(intent)
        }
    }


}