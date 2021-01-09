package com.verkoopapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.DiscoverRestroAdapter
import kotlinx.android.synthetic.main.activity_search_food.*
import kotlinx.android.synthetic.main.fragment_food_home.*
import kotlinx.android.synthetic.main.fragment_food_home.rv_discover_restro
import kotlinx.android.synthetic.main.toolbar_search_food.*
import kotlinx.android.synthetic.main.toolbar_search_food.view.*
import kotlinx.android.synthetic.main.toolbar_search_food.view.ivChat

class SearchFoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_food)

        search_tool_lyt.iv_left.setOnClickListener {
            finish()
        }
        ivChat.setOnClickListener {
            val intent = Intent(this, FoodFilterActivity::class.java)
            startActivity(intent)
        }

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_discover_restro.layoutManager = linearLayoutManager
        val adapter = DiscoverRestroAdapter(this)
        rv_discover_restro.setHasFixedSize(true)
        rv_discover_restro.adapter = adapter
        rv_discover_restro!!.adapter!!.notifyDataSetChanged()


    }
}