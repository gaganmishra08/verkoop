package com.verkoopapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.CarModalAdapter
import com.verkoopapp.models.CarModelList
import com.verkoopapp.utils.AppConstants
import kotlinx.android.synthetic.main.car_brand_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*

class CarModalActivity: AppCompatActivity(){
    private var carModalLIst= ArrayList<CarModelList>()
    private var carBrand:String=""
    private var carBrandId:Int=0
    private lateinit var carModalAdapter: CarModalAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_brand_activity)
        carModalLIst= intent.getParcelableArrayListExtra(AppConstants.CAR_MODEL_LIST)!!
        carBrand= intent.getStringExtra(AppConstants.CAR_BRAND_NAME).toString()
        carBrandId=intent.getIntExtra(AppConstants.CAR_BRAND_ID,carBrandId)
        setAdapter()
        setData()

    }

    private fun setData() {
        tvHeaderLoc.text = carBrand
        ivLeftLocation.setOnClickListener { onBackPressed() }
    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvCarBrand.layoutManager = mManager
        carModalAdapter = CarModalAdapter(this,carBrand,carBrandId)
        rvCarBrand.adapter = carModalAdapter
        carModalAdapter.setData(carModalLIst)
        carModalAdapter.notifyDataSetChanged()
    }
}