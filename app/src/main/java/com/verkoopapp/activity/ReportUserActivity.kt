package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import com.github.florent37.viewanimator.ViewAnimator
import com.verkoopapp.R
import com.verkoopapp.adapter.ReportListAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.report_user_activity.*
import retrofit2.Response


class ReportUserActivity : AppCompatActivity(), ReportListAdapter.OnSelectedCallBack {
    private var regionId: Int = 0
    private var selectedtype: Int = 0
    private var itemId: Int = 0
    private var comingFrom: Int = 0
    override fun onSelection(reportId: Int, description: String, type: Int) {
        tvReason.text = description
        regionId = reportId
        selectedtype = type
    }

    private var reportList = ArrayList<ReportResponse>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_user_activity)
        reportList = intent.getParcelableArrayListExtra(AppConstants.REPORT_LIST)!!
        itemId = intent.getIntExtra(AppConstants.ITEM_ID, 0)
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        setAdapter()
        setData()
        setAnimation()
    }

    private fun setAnimation() {
        ViewAnimator
                .animate(flParentReport)
                .translationY(1000f, 0f)
                .duration(400)
                .start()
    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvReasonList.layoutManager = mManager
        val reportListAdapter = ReportListAdapter(this, reportList)
        rvReasonList.adapter = reportListAdapter
    }

    private fun setData() {
        pbProgressReport.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        tvCancel.setOnClickListener {
            onBackPressed()
        }

        tvSubmit.setOnClickListener {
            if (regionId != 0) {
                if (Utils.isOnline(this)) {
                    tvSubmit.isEnabled = false
                    Handler().postDelayed(Runnable {
                        tvSubmit.isEnabled = true
                    }, 700)
                    callReportSubmitApi()
                } else {
                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()

                }
            } else {
                Utils.showSimpleMessage(this, "Please select a valid region for report.").show()
            }
        }
    }

    private fun callReportSubmitApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressReport.visibility = View.VISIBLE
        var reportUserResponse = ReportUserRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), itemId, regionId, selectedtype)
        ServiceHelper().reportAddedService(reportUserResponse,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressReport.visibility = View.GONE
                        val loginResponse = response.body() as DisLikeResponse
                        Utils.showToast(this@ReportUserActivity, getString(R.string.report_submitted))
                        onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressReport.visibility = View.GONE
                        Utils.showSimpleMessage(this@ReportUserActivity, msg!!).show()
                    }
                })
    }

    override fun onBackPressed() {
        ViewAnimator
                .animate(flParentReport)
                .translationY(0f, 2000f)
                .duration(700)
                .andAnimate(llParentReport)
                .alpha(1f, 0f)
                .duration(600)
                .onStop {
                    llParentReport.visibility = View.GONE
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_CANCELED, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()

    }
}