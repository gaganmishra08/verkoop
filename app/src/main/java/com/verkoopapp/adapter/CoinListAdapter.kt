package com.verkoopapp.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.fragment.GetCoinsFragment
import com.verkoopapp.models.CoinPlan
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.coin_row.*


class CoinListAdapter(private val context: Context, private val rvCoinList: Int,private val  getCoinsFragment: GetCoinsFragment):RecyclerView.Adapter<CoinListAdapter.ViewHolder>(){
    private val minflater:LayoutInflater=LayoutInflater.from(context)
    private var coinPlanList=ArrayList<CoinPlan>()
    private lateinit var purchaseCoinCallBack:PurchaseCoinCallBack
    private var width=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
       val view=minflater.inflate(R.layout.coin_row,parent,false)
        purchaseCoinCallBack=getCoinsFragment
        val params = view.layoutParams
        width = rvCoinList / 3
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return coinPlanList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val modal=coinPlanList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{
        fun bind(modal: CoinPlan) {
            llCoinParent.layoutParams.height =width
            tvCoins.text=modal.coin.toString()
            modal.amount = Utils.convertCurrencyWithoutSymbol(context,modal.currency,modal.amount)
            tvPriceCoins.text= StringBuilder().append(Utils.getPreferencesString(context, AppConstants.CURRENCY_SYMBOL)+" ").append(modal.amount)
//            tvPriceCoins.text= StringBuilder().append(Utils.getPreferencesString(context, AppConstants.USER_ID)).append(modal.amount)
            itemView.setOnClickListener {
            purchaseCoinCallBack.purchaseCoin(modal.id,adapterPosition,modal.amount,modal.coin)
            }
        }

    }

    fun setData(data: ArrayList<CoinPlan>) {
        coinPlanList=data
    }

    interface PurchaseCoinCallBack{
        fun purchaseCoin(coinPlanId:Int,position: Int,price:Double,totalCoin:Int)
    }

}