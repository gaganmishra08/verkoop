package com.verkoopapp.fragment

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.CoinsActivity
import com.verkoopapp.adapter.CoinListAdapter
import com.verkoopapp.models.CoinPlanResponse
import com.verkoopapp.models.PurchaseCoinRequest
import com.verkoopapp.models.UpdateWalletResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.PurchaseCoinDialog
import com.verkoopapp.utils.SelectionListener
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.get_coin_fragment.*
import retrofit2.Response

class GetCoinsFragment : BaseFragment(), CoinListAdapter.PurchaseCoinCallBack {
    private lateinit var coinUpdateCallBack: CoinUpdateCallBack
    private lateinit var coinsActivity: CoinsActivity
    private val TAG = GetCoinsFragment::class.java.simpleName
    private lateinit var getCoinAdapter: CoinListAdapter

    override fun purchaseCoin(coinPlanId: Int, position: Int, price: Double, totalCoin: Int) {
        purchaseDialog(coinPlanId, position, price, totalCoin)
    }


    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        coinsActivity = context as CoinsActivity
        coinUpdateCallBack = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.get_coin_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val display = coinsActivity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        setAdapter(width)
        if (Utils.isOnline(coinsActivity)) {
            getWalletHistoryApi()
        } else {
            Utils.showSimpleMessage(coinsActivity, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter(width: Int) {
        val mLayoutManager = GridLayoutManager(coinsActivity, 3)
        rvCoinsList.layoutManager = mLayoutManager
        getCoinAdapter = CoinListAdapter(coinsActivity, width, this)
        rvCoinsList.adapter = getCoinAdapter
    }

    companion object {
        fun newInstance(): GetCoinsFragment {
            val arg = Bundle()
            val fragment = GetCoinsFragment()
            fragment.arguments = arg
            return fragment
        }
    }

    private fun getWalletHistoryApi() {
        pbProgressCoin.visibility = View.VISIBLE
        ServiceHelper().getCoinPlanService(Utils.getPreferencesString(coinsActivity, AppConstants.USER_ID).toInt(), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                if (pbProgressCoin != null) {
                    pbProgressCoin!!.visibility = View.GONE
                }
                val responseWallet = response.body() as CoinPlanResponse
                if (responseWallet.data.isNotEmpty()) {
                    getCoinAdapter.setData(responseWallet.data)
                    getCoinAdapter.notifyDataSetChanged()
                    coinUpdateCallBack.updateHistoryList(responseWallet.coins, 0)
                    Utils.saveIntPreferences(coinsActivity, AppConstants.COIN, responseWallet.coins)
                } else {
                    Utils.showSimpleMessage(coinsActivity, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                pbProgressCoin.visibility = View.GONE
                Utils.showSimpleMessage(coinsActivity, msg!!).show()
            }
        })

    }

    private fun purchaseCoinService(coin_id: Int, position: Int, totalCoin: Int) {
        coinsActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressCoin.visibility = View.VISIBLE
        ServiceHelper().purchaseCoinService(PurchaseCoinRequest(Utils.getPreferencesString(coinsActivity, AppConstants.USER_ID).toInt(), coin_id),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        coinsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressCoin.visibility = View.GONE
                        val loginResponse = response.body() as UpdateWalletResponse
                        Utils.showToast(coinsActivity, loginResponse.message)
                        coinUpdateCallBack.updateHistoryList(totalCoin, 2)
                        Utils.saveIntPreferences(coinsActivity, AppConstants.COIN, totalCoin)
                    }

                    override fun onFailure(msg: String?) {
                        coinsActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressCoin.visibility = View.GONE
                        Utils.showSimpleMessage(coinsActivity, msg!!).show()
                    }
                })

    }

    private fun purchaseDialog(coin_id: Int, position: Int, price: Double, totalCoin: Int) {
        val message = StringBuffer().append("Want to purchase ").append(totalCoin).append(" coins?")
        val shareDialog = PurchaseCoinDialog(coinsActivity, message, object : SelectionListener {
            override fun leaveClick() {
                purchaseCoinService(coin_id, position, totalCoin)
            }
        })
        shareDialog.show()
    }

    interface CoinUpdateCallBack {
        fun updateHistoryList(totalCoin: Int, type: Int)
    }
}