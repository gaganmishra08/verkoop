package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.BannerAdapter
import com.verkoopapp.models.ViewAllBannerResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.view_all_banner_activity.*
import retrofit2.Response


class ViewAllBannerActivity: AppCompatActivity(){
    private lateinit var bannerListAdapter:BannerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_all_banner_activity)
        setData()
        setAdapter()
    }

    private fun setAdapter() {
     val mLayoutManager=LinearLayoutManager(this)
        rvBanner.layoutManager=mLayoutManager
        bannerListAdapter= BannerAdapter(this@ViewAllBannerActivity)
        rvBanner.adapter=bannerListAdapter
    }

    private fun setData() {
        tvHeaderLoc.text=getString(R.string.advert_list)
        ivLeftLocation.setOnClickListener { onBackPressed() }
        ivRight.setImageResource(R.drawable.ic_add_black_24dp)
        ivRight.visibility= View.VISIBLE
        ivRight.setOnClickListener {
            val intent = Intent(this, UploadBannerActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Utils.isOnline(this)) {
            getFavouriteApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }
    private fun getFavouriteApi() {
        pbViewAll.visibility= View.VISIBLE
        ServiceHelper().getAlBannerService(Utils.getPreferencesString(this, AppConstants.USER_ID),object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbViewAll.visibility= View.GONE
                val responseBanner = response.body() as ViewAllBannerResponse
                if (responseBanner.data.isNotEmpty()) {
                    bannerListAdapter.setData(responseBanner.data)
                    bannerListAdapter.notifyDataSetChanged()

                }else{
//                    Utils.showSimpleMessage(this@ViewAllBannerActivity, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                pbViewAll.visibility= View.GONE
                Utils.showSimpleMessage(this@ViewAllBannerActivity, msg!!).show()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 5){
            if(resultCode == Activity.RESULT_OK){
//                if (Utils.isOnline(this)) {
//                    getFavouriteApi()
//                } else {
//                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
//                }
            }
        }
    }
}