package com.verkoopapp.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.FollowFollowingAdapter
import com.verkoopapp.models.HomeRequest
import com.verkoopapp.models.HomeRequestID
import com.verkoopapp.models.SearchByUserResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.KeyboardUtil
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.search_activity.*
import kotlinx.android.synthetic.main.toolbar_search_user.*
import retrofit2.Response

class FollowFollowingActivity : AppCompatActivity() {
    private lateinit var searchByUserAdapter: FollowFollowingAdapter
    private var comingFrom = 0
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)
        ll_search.visibility = View.GONE
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        userId = intent.getIntExtra(AppConstants.USER_ID, 0)
        setData()
        setUserSearchAdapter()
        if (Utils.isOnline(this)) {
            if (comingFrom == 0) {
                etSearchHeader.hint = "Search Followers"
                followFollowingApi(0, userId)
            } else {
                etSearchHeader.hint = "Search Following User"
                followFollowingApi(1, userId)
            }
            KeyboardUtil.hideKeyboard(this)
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setUserSearchAdapter() {
        val mManager = LinearLayoutManager(this)
        rvSearchList.layoutManager = mManager
        searchByUserAdapter = FollowFollowingAdapter(this,userId,comingFrom)
        rvSearchList.adapter = searchByUserAdapter
    }

    private fun setData() {
        iv_left.setOnClickListener { onBackPressed() }
        etSearchHeader.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchByUserAdapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }


    private fun followFollowingApi(type: Int, userId: Int) {
        pbProgressSearch.visibility = View.VISIBLE
        ServiceHelper().followFollowingService(userId, HomeRequestID(type),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressSearch.visibility = View.GONE
                        val searchItemResponse = response.body() as SearchByUserResponse?
                        if (searchItemResponse != null) {
                            searchByUserAdapter.setData(searchItemResponse.data)
                            searchByUserAdapter.notifyDataSetChanged()
                        }
                        if (searchItemResponse!!.data.size > 0) {
                            tvDemoText.visibility = View.GONE
                        } else {
                            tvDemoText.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(this@FollowFollowingActivity, msg!!).show()
                        pbProgressSearch.visibility = View.GONE
                    }
                })
    }
}