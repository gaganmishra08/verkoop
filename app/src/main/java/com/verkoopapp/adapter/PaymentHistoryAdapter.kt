package com.verkoopapp.adapter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.verkoopapp.R
import com.verkoopapp.models.DataHistory
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.my_wallet_activity.*
import kotlinx.android.synthetic.main.row_payment_history.*

class PaymentHistoryAdapter(private val context: Context, private val type: Int) : RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var dataList = ArrayList<DataHistory>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_payment_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = dataList[position]
        holder.build(modal)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun build(modal: DataHistory) {
            if (modal.type == 0) {
                tvTypeHeading.text = context.getString(R.string.added_to_verkoop)
                tvPriceWallet.setTextColor(ContextCompat.getColor(context, R.color.accept_offer))

                tvPriceWallet.text = StringBuilder().append("$" + " ").append(modal.amount)
            } else if (modal.type == 1) {
                tvTypeHeading.text = context.getString(R.string.send_to)
                tvPriceWallet.setTextColor(ContextCompat.getColor(context, R.color.black_))
                tvPriceWallet.text = StringBuilder().append("- ").append("$"+" ").append(modal.amount)
            }
            if (!TextUtils.isEmpty(modal.created_at)) {
                tvData.text = StringBuilder().append(Utils.getDateDifferenceDetails(modal.created_at)).append(" ").append("ago")
            }
        }
    }

    fun setData(responseWalletList: ArrayList<DataHistory>) {
        dataList = responseWalletList
    }
}