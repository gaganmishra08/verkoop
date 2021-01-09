package com.verkoopapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.CarBrandsAdapter
import com.verkoopapp.models.Brand
import com.verkoopapp.utils.AppConstants
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*


class CarBrandsActivity: AppCompatActivity(){
    private  lateinit var linearLayoutManager:GridLayoutManager
    private lateinit var carBrandsAdapter: CarBrandsAdapter
    private var brandList= ArrayList<Brand>()
    private var comingFrom=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        brandList= intent.getParcelableArrayListExtra(AppConstants.CAR_BRAND_LIST)!!
        setData()
        setAdapter()
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text=getString(R.string.car_brands)
    }

    private fun setAdapter() {
        linearLayoutManager = GridLayoutManager(this, 3)
        rvFavouriteList.layoutManager = linearLayoutManager
        carBrandsAdapter = CarBrandsAdapter(this, rvFavouriteList,brandList)
        rvFavouriteList.adapter = carBrandsAdapter
    }
}