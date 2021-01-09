package com.verkoopapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.FavroutiesFoodAdapter
import kotlinx.android.synthetic.main.activity_favourites_food.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class FavouritesFoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites_food)
        tool_lyt.tvHeaderLoc.text = getString(R.string.favourites)
        tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvFavouriteFoodList.layoutManager = linearLayoutManager
        val favFoodAdapter = FavroutiesFoodAdapter(this)
        rvFavouriteFoodList.setHasFixedSize(true)
        rvFavouriteFoodList.adapter = favFoodAdapter
        rvFavouriteFoodList!!.adapter!!.notifyDataSetChanged()
    }

}