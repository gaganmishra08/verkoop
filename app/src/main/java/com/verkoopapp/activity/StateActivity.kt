package com.verkoopapp.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.verkoopapp.R
import com.verkoopapp.adapter.CityListAdapter
import com.verkoopapp.models.City
import com.verkoopapp.utils.AppConstants
import kotlinx.android.synthetic.main.selarch_location_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.models.CityDataValue


class StateActivity: AppCompatActivity(), CityListAdapter.ClickEventCallBack {
    private var cityList=ArrayList<CityDataValue>()
    private lateinit var cityListAdapter: CityListAdapter

    override fun onSelectRegion(regionName: String, regionId: Int) {
        val returnIntent = Intent()
        returnIntent.putExtra(AppConstants.CITY_NAME, regionName)
        returnIntent.putExtra(AppConstants.CITY_ID, regionId)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selarch_location_activity)
        cityList= intent.getParcelableArrayListExtra(AppConstants.CITY_LIST)!!
        setAdapter()
        setData()
    }
    private fun setAdapter() {
        val mManager= LinearLayoutManager(this)
        rvLocation.layoutManager=mManager
        cityListAdapter= CityListAdapter(this)
        rvLocation.adapter=cityListAdapter
        cityListAdapter.setData(cityList)
    }

    private fun setData() {
        tvHeaderLoc.text = getString(R.string.city)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        etSearchPlace.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.e("search",charSequence.toString())
                cityListAdapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })

    }

    override fun onBackPressed() {
       // super.onBackPressed()
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }
}