package com.verkoopapp.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.CoinsActivity
import com.verkoopapp.adapter.CoinsHistoryAdapter
import com.verkoopapp.models.WalletHistoryResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.history_fragment.rvCoinsHistoryList
import kotlinx.android.synthetic.main.history_fragment.pbProgressHistory
import retrofit2.Response

class HistoryFragment : BaseFragment() {
    private val TAG = HistoryFragment::class.java.simpleName
    private lateinit var coinsActivity: CoinsActivity
    private lateinit var paymentHistoryAdapter: CoinsHistoryAdapter
    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coinsActivity = context as CoinsActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        if (Utils.isOnline(coinsActivity)) {
            getWalletHistoryApi()
        } else {
            Utils.showSimpleMessage(coinsActivity, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(coinsActivity)
        rvCoinsHistoryList.layoutManager = mManager
        paymentHistoryAdapter = CoinsHistoryAdapter(coinsActivity)
        rvCoinsHistoryList.adapter = paymentHistoryAdapter
    }

    companion object {
        fun newInstance(): HistoryFragment {
            val arg = Bundle()
            val fragment = HistoryFragment()
            fragment.arguments = arg
            return fragment
        }
    }

    fun refreshApi() {
        if (Utils.isOnline(coinsActivity)) {
            getWalletHistoryApi()
        } else {
            Utils.showSimpleMessage(coinsActivity, getString(R.string.check_internet)).show()
        }
    }

    private fun getWalletHistoryApi() {
        pbProgressHistory.visibility = View.VISIBLE
        ServiceHelper().getCoinHistoryService(Utils.getPreferencesString(coinsActivity, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                if (pbProgressHistory != null) {
                    pbProgressHistory.visibility = View.GONE
                }
                val responseWallet = response.body() as WalletHistoryResponse
                // tvTotalAmount.text=responseWallet.amount.toString()
                if (responseWallet.data!!.isNotEmpty()) {
                    paymentHistoryAdapter.setData(responseWallet.data!!)
                    paymentHistoryAdapter.notifyDataSetChanged()

                } else {
                    //  Utils.showSimpleMessage(coinsActivity, "No data found.").show()
                }
            }

            override fun onFailure(msg: String?) {
                pbProgressHistory.visibility = View.GONE
                // Utils.showSimpleMessage(coinsActivity, msg!!).show()
            }
        })
    }
}