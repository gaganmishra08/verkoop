package com.verkoopapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.activity.MyWalletstripeActivity
import com.verkoopapp.activity.ProductDetailsActivity
import com.verkoopapp.activity.UserProfileActivity
import com.verkoopapp.adapter.ActivitiesAdapter
import com.verkoopapp.models.ActivityData
import com.verkoopapp.models.ActivityListResponseModel
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.NotificationType
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.activities_fragment.slActivities
import kotlinx.android.synthetic.main.activities_fragment.rvNotificationList
import retrofit2.Response

class ActivitiesFragment : BaseFragment(), NotificationType {

    val TAG = ActivitiesFragment::class.java.simpleName.toString()
    private lateinit var activitesAdapter: ActivitiesAdapter
    private lateinit var homeActivity: HomeActivity
    private var notificationsList = ArrayList<ActivityData>()

    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeActivity = context as HomeActivity
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activities_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        slActivities.setOnRefreshListener {
            if (Utils.isOnline(activity!!)) {
                getActivityListApi()
            } else {
                Utils.showSimpleMessage(activity!!, "Please check your internet connection.")
            }

            slActivities.isRefreshing = false
        }
        if (Utils.isOnline(activity!!)) {
            getActivityListApi()
        } else {
            Utils.showSimpleMessage(activity!!, "Please check your internet connection.")
        }
    }

    private fun getActivityListApi() {
        ServiceHelper().getActivityList(Utils.getPreferencesString(activity!!, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val activityResponse = response.body() as ActivityListResponseModel

                if (activityResponse.data != null) {
                    try {
                        rvNotificationList.visibility = View.VISIBLE
                        notificationsList.clear()
                        notificationsList = activityResponse.data.activities
                        activitesAdapter.setData(notificationsList)
                        activitesAdapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                    }
                } else {
//                            Utils.showSimpleMessage(homeActivity, myProfileResponse.message).show()
                }
            }

            override fun onFailure(msg: String?) {
                Utils.showSimpleMessage(homeActivity, msg!!).show()
            }
        })

    }

    private fun setAdapter() {
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvNotificationList.layoutManager = linearLayoutManager
        activitesAdapter = ActivitiesAdapter(homeActivity, this)
        rvNotificationList.adapter = activitesAdapter
    }

    companion object {
        fun newInstance(): ActivitiesFragment {
            val args = Bundle()
            val fragment = ActivitiesFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun typeNotification(position: Int?) {
        when (notificationsList[position!!].type) {
            1 -> {
                if (notificationsList[position!!].item_id != null) {
                    val intent = Intent(activity!!, ProductDetailsActivity::class.java)
                    intent.putExtra(AppConstants.ITEM_ID, notificationsList[position!!].item_id)
                    startActivity(intent)
                }
            }
            2 -> {
                if (notificationsList[position!!].user_id != null) {
                    val reportIntent = Intent(activity!!, UserProfileActivity::class.java)
                    reportIntent.putExtra(AppConstants.USER_ID, notificationsList[position!!].user_id)
                    startActivity(reportIntent)
                }
            }
            3 -> {
                if (notificationsList[position!!].item_id != null) {
                    val intent = Intent(activity!!, ProductDetailsActivity::class.java)
                    intent.putExtra(AppConstants.ITEM_ID, notificationsList[position!!].item_id)
                    startActivity(intent)
                }
            }
            4 -> {
                if (notificationsList[position!!].user_id != null) {
                    val reportIntent = Intent(activity!!, UserProfileActivity::class.java)
                    reportIntent.putExtra(AppConstants.USER_ID, notificationsList[position!!].user_id)
                    startActivity(reportIntent)
                }
            }
            5 -> {
                if (notificationsList[position!!].user_id != null) {
                    val reportIntent = Intent(activity!!, MyWalletstripeActivity::class.java)
//                    reportIntent.putExtra(AppConstants.USER_ID, notificationsList[position!!].user_id)
                    startActivity(reportIntent)
                }
            }
            6 -> {
                if (notificationsList[position!!].item_id != null) {
                    val intent = Intent(activity!!, ProductDetailsActivity::class.java)
                    intent.putExtra(AppConstants.ITEM_ID, notificationsList[position!!].item_id)
                    startActivity(intent)
                }
            }
        }
    }
}