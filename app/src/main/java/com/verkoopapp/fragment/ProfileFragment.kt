package com.verkoopapp.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.*
import com.verkoopapp.adapter.ProfileAdapter

import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.GridSpacingProfileDecorate
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.toolbar_profile.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Response


class ProfileFragment : BaseFragment() {
    private val TAG = ProfileFragment::class.java.simpleName.toString()
    lateinit var profileAdapter: ProfileAdapter
    private lateinit var homeActivity: HomeActivity
    private var itemsList = ArrayList<ItemHome>()

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
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window: Window = activity!!.window
        window.statusBarColor = ContextCompat.getColor(activity!!,R.color.gray);
        ivLeftLocation.visibility=View.GONE
        val display = homeActivity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        setData()
        setAdapter(width)
        ivChat.setOnClickListener {
            val intent = Intent(context, ChatInboxActivity::class.java)
            startActivity(intent)
        }
        if (Utils.isOnline(homeActivity)) {
            homeActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (pbProgressProfile != null) {
                pbProgressProfile.visibility = View.VISIBLE
            }
            myProfileInfoApi(0)
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter(width: Int) {
        val linearLayoutManager = GridLayoutManager(context, 2)
        linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (profileAdapter.getItemViewType(position)) {
                    profileAdapter.PROFILE_DETAILS -> 2
                    else ->
                        1
                }
            }
        }
        rvPostsList.addItemDecoration(GridSpacingProfileDecorate(2, Utils.dpToPx(homeActivity, 2F).toInt(), false))
        rvPostsList.layoutManager = linearLayoutManager
        profileAdapter = ProfileAdapter(homeActivity, width, this)
        rvPostsList.adapter = profileAdapter
    }

    private fun setData() {
        scProfile.setOnRefreshListener {
            if (Utils.isOnline(homeActivity)) {
                if (Utils.isOnline(homeActivity)) {
                    myProfileInfoApi(1)
                } else {
                    scProfile.isRefreshing = false
                    Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
                }
            } else {
                scProfile.isRefreshing = false
                Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
            }
        }
        scProfile.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary)

        tvCategoryProfile.setOnClickListener {
            val intent = Intent(homeActivity, FullCategoriesActivity::class.java)
            startActivity(intent)
        }
        llSearchProfile.setOnClickListener {
            val intent = Intent(homeActivity, SearchActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        tvSellProfile.setOnClickListener {
            tvSellProfile.isEnabled = false
            val intent = Intent(context, GalleryActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
            Handler().postDelayed(Runnable {
                tvSellProfile.isEnabled = true
            }, 700)
        }
    }

    companion object {
        fun newInstance(): ProfileFragment {
            val arg = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = arg
            return fragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                val adapterPosition = data!!.getIntExtra(AppConstants.ADAPTER_POSITION, 0)
                if (data.getStringExtra(AppConstants.TYPE).equals("soldItem", ignoreCase = true)) {
                    itemsList[adapterPosition - 1].is_sold = 1
                    profileAdapter.notifyDataSetChanged()
                } else if (data.getStringExtra(AppConstants.TYPE).equals("deleteItem", ignoreCase = true)) {
                    itemsList.removeAt(adapterPosition - 1)
                    profileAdapter.notifyDataSetChanged()
                } else if (data.getStringExtra(AppConstants.TYPE).equals("UpdateItem", ignoreCase = true)) {
                    if (Utils.isOnline(homeActivity)) {
                        homeActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        if (pbProgressProfile != null) {
                            pbProgressProfile.visibility = View.VISIBLE
                        }
                        myProfileInfoApi(0)
                    } else {
                        Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private fun myProfileInfoApi(loadMore: Int) {
        ServiceHelper().myProfileService(Utils.getPreferencesString(homeActivity, AppConstants.USER_ID),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        if (scProfile != null) {
                            scProfile.isRefreshing = false
                        }
                        if (pbProgressProfile != null) {
                            pbProgressProfile.visibility = View.GONE
                        }
                        homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val myProfileResponse = response.body() as MyProfileResponse

                        if (myProfileResponse.data != null) {
                            try {
                                rvPostsList.visibility = View.VISIBLE
                                itemsList.clear()
                                itemsList = myProfileResponse.data.items
                                profileAdapter.profileDetail(myProfileResponse.data)
                                profileAdapter.setData(itemsList)
                                profileAdapter.notifyDataSetChanged()
                            } catch (e: Exception) {
                            }
                        } else {
//                            Utils.showSimpleMessage(homeActivity, myProfileResponse.message).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Utils.showSimpleMessage(homeActivity, msg!!).show()
                        if (scProfile != null) {
                            scProfile.isRefreshing = false
                        }
                        if (pbProgressProfile != null) {
                            pbProgressProfile.visibility = View.GONE
                        }

                    }
                })
    }

    fun refreshUI(from: Int) {
        if (from == 0) {
            //   rvPostsList.isFocusable=true
            //   rvPostsList.scrollToPosition(0)
        } else if (from == 1) {
            if (Utils.isOnline(homeActivity)) {
                homeActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (pbProgressProfile != null) {
                    pbProgressProfile.visibility = View.VISIBLE
                }
                myProfileInfoApi(0)
            } else {
                Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (Utils.isOnline(homeActivity)) {
            homeActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (pbProgressProfile != null) {
                pbProgressProfile.visibility = View.VISIBLE
            }
            myProfileInfoApi(0)
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }
}