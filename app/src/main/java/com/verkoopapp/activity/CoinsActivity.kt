package com.verkoopapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.verkoopapp.R
import com.verkoopapp.adapter.GetCoinAdapter
import com.verkoopapp.fragment.*
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.coins_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*


class CoinsActivity:AppCompatActivity(), GetCoinsFragment.CoinUpdateCallBack {
    private var fragmentList = ArrayList<Fragment>()
    private var getCoinFragment: GetCoinsFragment? = null
    private var getHistoryFragment: HistoryFragment? = null
    lateinit var tvHeaderLoc: TextView
    lateinit var ivLeftLocation: ImageView
    override fun updateHistoryList(totalCoin: Int, type: Int) {
        Log.e("<<Total coin>>", Utils.getPreferencesInt(this,AppConstants.COIN).toString())
        if(type==2){
            tvTotalCoin.text=((tvTotalCoin.text.toString()).toInt()+totalCoin).toString()
            getHistoryFragment!!.refreshApi()
        }else if(type!=0) {
            getHistoryFragment!!.refreshApi()
        }else{
            tvTotalCoin.text=totalCoin.toString()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coins_activity)
        tvHeaderLoc=findViewById(R.id.tvHeaderLoc)
        ivLeftLocation=findViewById(R.id.ivLeftLocation)
        tvTotalCoin.text=Utils.getPreferencesInt(this, AppConstants.COIN).toString()
        getCoinFragment = GetCoinsFragment.newInstance()
        getHistoryFragment = HistoryFragment.newInstance()
        fragmentList.add(getCoinFragment!!)
        fragmentList.add(getHistoryFragment!!)
        setData()
        setAdapter()
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text=getString(R.string.coins)
        llHistory.setOnClickListener {vpGetCoin.currentItem=1  }
        llCoin.setOnClickListener {vpGetCoin.currentItem=0  }
    }
    private fun setAdapter() {
        val mAdapter = GetCoinAdapter(supportFragmentManager,fragmentList)
        vpGetCoin.adapter = mAdapter
        vpGetCoin.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
               highlightIndicator(position)
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun highlightIndicator(position: Int) {
        if(position==0){
            ivTipCoin.visibility=View.VISIBLE
            ivTipHistory.visibility=View.INVISIBLE
            view_all.visibility=View.VISIBLE
            view_buy.visibility=View.INVISIBLE
            tvGetCoin.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            tvHistory.setTextColor(ContextCompat.getColor(this,R.color.black))
        }else{
            ivTipCoin.visibility=View.INVISIBLE
            ivTipHistory.visibility=View.VISIBLE
            view_all.visibility=View.INVISIBLE
            view_buy.visibility=View.VISIBLE
            tvHistory.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            tvGetCoin.setTextColor(ContextCompat.getColor(this,R.color.black))
        }
    }
}