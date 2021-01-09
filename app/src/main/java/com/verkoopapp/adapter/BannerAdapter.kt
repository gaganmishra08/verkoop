package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.AdvertPackagesActivity
import com.verkoopapp.activity.AdvertisementActivity
import com.verkoopapp.activity.UploadBannerActivity
import com.verkoopapp.activity.ViewAllBannerActivity
import com.verkoopapp.models.DataBanner
import com.verkoopapp.models.DisLikeResponse
import com.verkoopapp.models.RenewBannerRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.DeleteCommentDialog
import com.verkoopapp.utils.SelectionListener
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.all_banner_row.*
import retrofit2.Response


class BannerAdapter(private val context: Context) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var bannerList = ArrayList<DataBanner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.all_banner_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = bannerList[position]
        holder.bind(data)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: DataBanner) {
            tvPurchaseData.text = StringBuffer().append(": ").append(Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.updated_at, "MMMM dd, yyyy"))
            tvExpiredDate.text = StringBuffer().append(": ").append(Utils.dateDifference("yyyy-MM-dd hh:mm:ss", Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.updated_at, "MMMM dd, yyyy"), data.day))
            Picasso.with(context).load(AppConstants.IMAGE_URL + data.image)
                    .resize(1024, 1024)
                    .centerCrop()
                    .error(R.mipmap.post_placeholder)
                    .placeholder(R.mipmap.post_placeholder)
                    .into(ivProductImageHome)

            ivProductImageHome.setOnClickListener {
                val intent=Intent(context, AdvertisementActivity::class.java)
                intent.putExtra(AppConstants.CATEGORY_ID,data.id)
                context.startActivity(intent)
            }
            tvDeleteBanner.setOnClickListener {
                tvDeleteBanner.isEnabled = false
                deleteBannerPopUp(data.id, adapterPosition)
                Handler().postDelayed(Runnable {
                    tvDeleteBanner.isEnabled = true
                }, 1000)
            }

            if (data.status.equals("0")) {
                tvRenewBanner.text = "Waiting for review"
            } else if (data.status.equals("1")) {
                tvRenewBanner.alpha = 0.3f
            } else if(data.status.equals("2")){
                tvRenewBanner.alpha = 1f
            } else if(data.status.equals("3")){
                tvRenewBanner.text = "Rejected"
            }

            tvRenewBanner.setOnClickListener {
                if (data.status.equals("2")) {
                    tvRenewBanner.isEnabled = false
                    val intent = Intent(context, AdvertPackagesActivity::class.java)
                    intent.putExtra(AppConstants.COMING_FROM, "forRenewPackage")
                    intent.putExtra(AppConstants.BANNERID, data.id)
//                    context.startActivity(intent)
                    (context as ViewAllBannerActivity).startActivityForResult(intent,5)
//                renewBannerPopUp(data.id,adapterPosition)
//                    renewBannerApi(data.id, adapterPosition)
                    Handler().postDelayed(Runnable {
                        tvRenewBanner.isEnabled = true
                    }, 1000)
                }
            }
        }
    }

    private fun renewBannerPopUp(bannerId: Int, adapterPosition: Int) {
        val shareDialog = DeleteCommentDialog(context, context.getString(R.string.deactivate_heading), context.getString(R.string.delete_banner), object : SelectionListener {
            override fun leaveClick() {
                if (Utils.isOnline(context)) {
                    renewBannerApi(bannerId, adapterPosition)
                } else {
                    Utils.showSimpleMessage(context, context.getString(R.string.check_internet)).show()
                }
            }
        })
        shareDialog.show()
    }

    private fun renewBannerApi(bannerId: Int, adapterPosition: Int) {
        ServiceHelper().renewBannerService(RenewBannerRequest(bannerId, 3),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val likeResponse = response.body() as DisLikeResponse
                        if (likeResponse.message.equals("Advertisement deleted successfully")) {
                            bannerList.removeAt(adapterPosition)
                            notifyDataSetChanged()
                            Utils.showSimpleMessage(context, context.getString(R.string.banner_deleted_successfully)).show()
                        } else {
                            Utils.showSimpleMessage(context, likeResponse.message!!).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(context, msg!!).show()
                    }
                })
    }

    private fun deleteBannerPopUp(bannerId: Int, adapterPosition: Int) {
        val shareDialog = DeleteCommentDialog(context, context.getString(R.string.deactivate_heading), context.getString(R.string.delete_banner), object : SelectionListener {
            override fun leaveClick() {
                if (Utils.isOnline(context)) {
                    deleteBannerApi(bannerId, adapterPosition)
                } else {
                    Utils.showSimpleMessage(context, context.getString(R.string.check_internet)).show()
                }
            }
        })
        shareDialog.show()
    }

    private fun deleteBannerApi(bannerId: Int, adapterPosition: Int) {
        ServiceHelper().deleteBannerService(bannerId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val likeResponse = response.body() as DisLikeResponse
                        if (likeResponse.message.equals("Advertisement deleted successfully")) {
                            bannerList.removeAt(adapterPosition)
                            notifyDataSetChanged()
                            Utils.showSimpleMessage(context, context.getString(R.string.banner_deleted_successfully)).show()
                        } else {
                            Utils.showSimpleMessage(context, likeResponse.message!!).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(context, msg!!).show()
                    }
                })
    }

    fun setData(data: ArrayList<DataBanner>) {
        bannerList = data
    }
}