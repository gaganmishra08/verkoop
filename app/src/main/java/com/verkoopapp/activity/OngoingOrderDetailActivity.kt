package com.verkoopapp.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.utils.RateUserListener
import com.verkoopapp.utils.RatingBarDialog
import com.verkoopapp.utils.RatingFoodOrder
import kotlinx.android.synthetic.main.activity_ongoing_order_detail.*

class OngoingOrderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing_order_detail)
        img_back.setOnClickListener {
            finish()
        }

        tv_favourites.setOnClickListener {
            openRatingDialog()
        }


    }


    private fun openRatingDialog() {
        val ratingDialog = RatingFoodOrder(this)
        ratingDialog.show()
    }
}