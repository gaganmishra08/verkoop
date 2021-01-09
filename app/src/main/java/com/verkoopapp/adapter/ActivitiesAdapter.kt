package com.verkoopapp.adapter

import android.content.Context
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.fragment.ActivitiesFragment
import com.verkoopapp.models.ActivityData
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.NotificationType
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer

import kotlinx.android.synthetic.main.notification_row.tvNotificationTitle
import kotlinx.android.synthetic.main.notification_row.tvNotificationDescription
import kotlinx.android.synthetic.main.notification_row.ivProfileNotification
import kotlinx.android.synthetic.main.notification_row.tvNotificationTime
import kotlinx.android.synthetic.main.notification_row.llNotification

class ActivitiesAdapter(val context: Context, private val activitiesFragment: ActivitiesFragment) : RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var notificationsList = ArrayList<ActivityData>()
    var homeActivity = context as HomeActivity?
    var notificationType: NotificationType = activitiesFragment

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: ActivityData?) {
            if (data != null) {
                tvNotificationTitle.text = data.message
                tvNotificationDescription.text = data.description
                tvNotificationTime.text = StringBuilder().append(Utils.getDateDifference(data.created_at!!.date)).append(" ").append("ago")

                if (!TextUtils.isEmpty(data.image)) {
                    Picasso.with(context).load(AppConstants.IMAGE_URL + data.image)
                            .resize(720, 720)
                            .centerInside()
                            .error(R.mipmap.pic_placeholder)
                            .placeholder(R.mipmap.pic_placeholder)
                            .into(ivProfileNotification)
                }
            }
            llNotification.setOnClickListener {
                llNotification.isEnabled = false
                notificationType.typeNotification(position!!)
                Handler().postDelayed(Runnable {
                    llNotification.isEnabled = true
                }, 1000)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.notification_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = notificationsList[position]
        (holder as ViewHolder).bind(modal)
    }


    fun setData(dataList: ArrayList<ActivityData>) {
        notificationsList = dataList
    }

//    StringBuilder().append(Utils.getDateDifference(data.created_at!!.date)).append(" ").append("ago")
}
