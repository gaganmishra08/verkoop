package com.verkoopapp.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.RatingListAdapter
import com.verkoopapp.models.MyRatingResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class RatingActivity : AppCompatActivity() {
    lateinit var ratingListAdapter: RatingListAdapter
    private var userId:Int=0
    private var comingFrom:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        userId=intent.getIntExtra(AppConstants.USER_ID,0)
        comingFrom=intent.getIntExtra(AppConstants.COMING_FROM,0)
        setData()
        setAdapter()
        if (Utils.isOnline(this)) {
            getMyRatingApi(userId,comingFrom)
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvFavouriteList.layoutManager = layoutManager
        ratingListAdapter = RatingListAdapter(this)
        rvFavouriteList.adapter = ratingListAdapter
    }

    private fun setData() {
        tvHeaderLoc.text = getString(R.string.rating)
        ivLeftLocation.setOnClickListener { onBackPressed() }
    }
    private fun getMyRatingApi(userId:Int,comingFrom:Int) {
        pbProgressFav.visibility= View.VISIBLE
        ServiceHelper().getMyRatingService(comingFrom,userId, Utils.getPreferencesString(this@RatingActivity, AppConstants.USER_ID),object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                tvMssgData.visibility=View.GONE
                pbProgressFav.visibility= View.GONE
                val responseRating = response.body() as MyRatingResponse
                if (responseRating.data!!.isNotEmpty()) {
                    ratingListAdapter.setData(responseRating.data!!)
                    ratingListAdapter.notifyDataSetChanged()

                }else{
                    tvMssgData.visibility=View.VISIBLE
                   // Utils.showSimpleMessage(this@RatingActivity, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                pbProgressFav.visibility= View.GONE
                Utils.showSimpleMessage(this@RatingActivity, msg!!).show()
            }
        })

    }
}