package com.verkoopapp.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.AdvertPackagesActivity
import com.verkoopapp.models.DataAdvert
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.coin_row.*


class AdvertPackageAdapter(private val context: Context, private val rvCoinList: Int) : RecyclerView.Adapter<AdvertPackageAdapter.ViewHolder>() {
    private var width = 0
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var submitBannerCallBack: SubmitBannerCallBack
    private var advertList = ArrayList<DataAdvert>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.coin_row, parent, false)
        val params = view.layoutParams
        width = rvCoinList / 3
        view.layoutParams = params
        submitBannerCallBack = context as AdvertPackagesActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return advertList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = advertList[position]
        holder.bind(modal)

    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(modal: DataAdvert) {
            llCoinParent.layoutParams.height = width
            tvCoins.text = modal.name
            tvPriceCoins.text = StringBuilder().append(modal.coin).append(" ").append(context.getString(R.string.coins))
            itemView.setOnClickListener {

                if (Utils.getPreferencesInt(context as AdvertPackagesActivity, AppConstants.COIN) >= modal.coin) {
                    submitBannerCallBack.planSelectionClick(modal.id,modal.coin)
                } else {
                    Utils.showSimpleMessage(context, context.getString(R.string.insufficient_balance)).show()
                }
                /*val returnIntent = Intent()
                returnIntent.putExtra(AppConstants.INTENT_RESULT, modal.id)
                (context as AdvertPackagesActivity).setResult(Activity.RESULT_OK, returnIntent)
                context .finish()*/
            }
        }
    }

    fun setData(data: ArrayList<DataAdvert>) {
        advertList = data
    }

    interface SubmitBannerCallBack {
        fun planSelectionClick(planId: Int,totalCoin:Int)
    }
}