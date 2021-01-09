package com.verkoopapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.utils.AppConstants

import kotlinx.android.synthetic.main.walk_through_activity.*


class WalkThroughActivity: AppCompatActivity(){
    private val mCaptions= ArrayList<String>()
    private val mImageResources = intArrayOf(R.drawable.onboarding1_illustration, R.drawable.onboarding2_illustration, R.drawable.onboarding3_illustration)
    private var vpPosition:Int=0
    private var id = 0
    private var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.walk_through_activity)
         type = intent.getIntExtra(AppConstants.TYPE, 0)
         id = intent.getIntExtra(AppConstants.ID, 0)
        mCaptions.add("Discover");
        mCaptions.add("Chat Instantly");
        mCaptions.add("Sell and declutter");
        setAdapter()
    }




    private fun setAdapter() {
       val mAdapter = PicturePreViewAdapter(this, mImageResources,mCaptions)
        vpWalkThrough.adapter = mAdapter
        vpWalkThrough.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                highlightIndicator(position)
                vpPosition=position
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        tvCreateAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        tvLoginS.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

//        tvNextW.setOnClickListener {
//            if(tvNextW.text.toString().equals("NEXT",ignoreCase = true)){
//                vpWalkThrough.currentItem = vpPosition+1
//            //    vpWalkThrough.currentItem = vpPosition
//            }else{
//                val intent = Intent(this, LoginActivity::class.java)
//                intent.putExtra(AppConstants.ID, id)
//                intent.putExtra(AppConstants.TYPE, type)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }
//        }
//        tvSkipW.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.putExtra(AppConstants.ID, id)
//            intent.putExtra(AppConstants.TYPE, type)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            finish()
//        }
    }

    private fun highlightIndicator(count: Int) {
        when (count) {
            0 -> {
                setGrayBackground()
                ivIndicatorFirstW.setImageResource(R.mipmap.ic_red_oval)
//                tvNextW.text=getString(R.string.next)
//                tvSkipW.visibility=View.VISIBLE
            }
            1 -> {
                setGrayBackground()
                ivIndicatorSecondW.setImageResource(R.mipmap.ic_red_oval)
//                tvNextW.text=getString(R.string.next)
//                tvSkipW.visibility=View.VISIBLE
            }
            2 -> {
                setGrayBackground()
                ivIndicatorThirdW.setImageResource(R.mipmap.ic_red_oval)
//                tvNextW.text=getString(R.string.done)
//                tvSkipW.visibility=View.INVISIBLE
            }
        }
    }

    private fun setGrayBackground() {
        ivIndicatorFirstW.setImageResource(R.mipmap.ic_gray_dot)
        ivIndicatorSecondW.setImageResource(R.mipmap.ic_gray_dot)
        ivIndicatorThirdW.setImageResource(R.mipmap.ic_gray_dot)
    }

    class PicturePreViewAdapter(mContext: Context, private var mImageResources: IntArray, private var mCaptions: ArrayList<String>) : PagerAdapter() {
       private var mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false)
            val imageView = itemView.findViewById(R.id.ivPagerItem) as ImageView
            val textView = itemView.findViewById(R.id.tvPagerItem) as TextView
            imageView.setImageResource(mImageResources[position])
            textView.setText(mCaptions[position])
            container.addView(itemView)
            return itemView
        }

        override fun getCount(): Int {
            return 3
        }


        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as FrameLayout)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}