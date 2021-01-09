package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.adapter.SearchByUserAdapter
import com.verkoopapp.adapter.SearchListAdapter
import com.verkoopapp.adapter.SearchVisionDataListAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.KeyboardUtil
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.search_activity.*
import kotlinx.android.synthetic.main.toolbar_search_user.*
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private val TAG = SearchActivity::class.java.simpleName
    private lateinit var searchListAdapter: SearchListAdapter
    private lateinit var searchVisionDataListAdapter: SearchVisionDataListAdapter
    private lateinit var searchByUserAdapter: SearchByUserAdapter
    private val searchItemList = ArrayList<DataSearch>()
    private val searchByUserList = ArrayList<DataUser>()
    private val searchVisionDataList = ArrayList<SearchMultipleKeywordData>()
    private var comingFrom: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)
        etSearchHeader.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        if (intent.getStringExtra(AppConstants.CATEGORY_NAME) != null) {
            etSearchHeader.hint = intent.getStringExtra(AppConstants.CATEGORY_NAME)
        } else {
            etSearchHeader.hint = getString(R.string.search_verkoop)
        }
        if (intent.getIntExtra(AppConstants.COMING_FROM, 0) == 1) {
            ll_search.visibility = View.GONE
            etSearchHeader.hint = getString(R.string.search_userr)
            comingFrom = 1
            setUserSearchAdapter()
        } else {
            setAdapter()
        }
        if (intent.getStringExtra("visionData") != null) {
            etSearchHeader.isEnabled=false
            KeyboardUtil.hideKeyboard(this)
            ll_search.visibility = View.GONE
            val string: String = intent.getStringExtra("visionData")!!
            setVisionDataAdapter()
            callSearchKeywordMultipleData(string)
        }
        setData()
    }

    private fun callSearchKeywordMultipleData(string: String) {
        if (Utils.isOnline(this)) {
            searchKeywordMultipleData(string)
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            callSearchKeywordMultipleData(string)
        }
    }

    private fun searchKeywordMultipleData(string: String) {
        pbProgressSearch.visibility = View.VISIBLE
        ServiceHelper().searchKeywordMultipleDataService(SearchKeywordMultipleDataRequest(string, Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressSearch.visibility = View.GONE
                        val searchItemResponse = response.body() as SearchMultipleKeywordResponse?
                        if (searchItemResponse != null) {
                            searchVisionDataList.clear()
//                            searchVisionDataList.addAll(searchItemResponse.data)
                            searchVisionDataListAdapter.setData(searchVisionDataList)
                            searchVisionDataListAdapter.notifyDataSetChanged()
                        }
                        if (searchVisionDataList.size > 0) {
                            tvDemoText.visibility = View.GONE
                        } else {
                            tvDemoText.visibility = View.VISIBLE
                        }

                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(this@SearchActivity, msg!!).show()
                        pbProgressSearch.visibility = View.GONE
                    }
                })
    }

    private fun setUserSearchAdapter() {
        val mManager = LinearLayoutManager(this)
        rvSearchList.layoutManager = mManager
        searchByUserAdapter = SearchByUserAdapter(this)
        rvSearchList.adapter = searchByUserAdapter
    }

    private fun setVisionDataAdapter() {
        val mManager = LinearLayoutManager(this)
        rvSearchList.layoutManager = mManager
        searchVisionDataListAdapter = SearchVisionDataListAdapter(this)
        rvSearchList.adapter = searchVisionDataListAdapter
    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvSearchList.layoutManager = mManager
        searchListAdapter = SearchListAdapter(this)
        rvSearchList.adapter = searchListAdapter

    }

    private fun setData() {
        iv_left.setOnClickListener { onBackPressed() }
        ll_search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 1)
            startActivity(intent)
        }
        etSearchHeader.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                if (comingFrom == 1) {

                    if (!TextUtils.isEmpty(etSearchHeader.text.toString())) {
                        if (Utils.isOnline(this)) {
                            searchByUserNameApi(etSearchHeader.text.toString())
                            KeyboardUtil.hideKeyboard(this)
                        } else {
                            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                        }
                    }
                }
                else if (comingFrom==0){

                    if (!TextUtils.isEmpty(etSearchHeader.text.toString())) {
                        if (Utils.isOnline(this)) {
                            val intent = Intent(this, CategoryDetailsActivity::class.java)

                            intent.putExtra(AppConstants.CATEGORY_ID, "")
                            intent.putExtra(AppConstants.SUB_CATEGORY, "")
                            intent.putExtra(AppConstants.TYPE, 1)
                            intent.putExtra(AppConstants.Search, etSearchHeader.editableText.toString())
                            startActivityForResult(intent, 2)
//                            KeyboardUtil.hideKeyboard(this)
                        } else {
                            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                        }
                    }

                }

                true
            } else false
        }


        etSearchHeader.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (comingFrom != 1) {
                    if (Utils.isOnline(this@SearchActivity)) {
                        if (!TextUtils.isEmpty(charSequence.toString().trim())) {
                            callSearchApi(charSequence.toString())
                        }/*else{
                        searchItemList.clear()
                        searchListAdapter.setData(searchItemList)
                        searchListAdapter.notifyDataSetChanged()
                    }*/
                    } else {
                        Utils.showSimpleMessage(this@SearchActivity, getString(R.string.check_internet)).show()
                    }
                } else {
                    tvDemoText.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun callSearchApi(searchItem: String) {
        ServiceHelper().searchItemService(SearchItemRequest(searchItem, Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val searchItemResponse = response.body() as SearchItemResponse?
                        if (searchItemResponse != null) {
                            searchItemList.clear()
                            searchItemList.addAll(searchItemResponse.data)
                            searchListAdapter.setData(searchItemList)
                            searchListAdapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(this@SearchActivity, msg!!).show()
                    }
                })
    }

    private fun searchByUserNameApi(searchItem: String) {
        pbProgressSearch.visibility = View.VISIBLE
        ServiceHelper().searchByUserService(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), SearchUserRequest(searchItem),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressSearch.visibility = View.GONE
                        val searchItemResponse = response.body() as SearchByUserResponse?
                        if (searchItemResponse != null) {
                            searchByUserList.clear()
                            searchByUserList.addAll(searchItemResponse.data)
                            searchByUserAdapter.setData(searchByUserList)
                            searchByUserAdapter.notifyDataSetChanged()
                        }
                        if (searchByUserList.size > 0) {
                            tvDemoText.visibility = View.GONE
                        } else {
                            tvDemoText.visibility = View.VISIBLE
                        }

                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(this@SearchActivity, msg!!).show()
                        pbProgressSearch.visibility = View.GONE
                    }
                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION, 0)
                if (result == 1) {
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.TRANSACTION, result)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                val returnIntent = Intent()
                setResult(Activity.RESULT_CANCELED, returnIntent)
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }//onActivityResult


    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
        overridePendingTransition(0, 0)
    }
}