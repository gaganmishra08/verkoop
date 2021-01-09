package com.verkoopapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.DeliveryDetailActivity
import com.verkoopapp.activity.FoodFilterActivity
import com.verkoopapp.activity.FoodHomeActivity
import com.verkoopapp.activity.SearchFoodActivity
import com.verkoopapp.adapter.DiscoverRestroAdapter
import com.verkoopapp.adapter.NearFoodAdapter
import kotlinx.android.synthetic.main.fragment_food_home.*
import kotlinx.android.synthetic.main.toolbar_food_home.view.*


class FoodHomeFragment : BaseFragment() {

    val TAG = FoodHomeFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: FoodHomeActivity

    lateinit var linearLayoutManager: LinearLayoutManager
    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity = context as FoodHomeActivity
    }

    override fun getFragmentTag(): String? {
        return TAG
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        food_home_tool_lyt.img_search.setOnClickListener {
            val intent = Intent(context, SearchFoodActivity::class.java)
            startActivity(intent)
        }
        food_home_tool_lyt.img_filter.setOnClickListener {
            val intent = Intent(context, FoodFilterActivity::class.java)
            startActivity(intent)
        }
        food_home_tool_lyt.deliver_lyt.setOnClickListener {
            val intent = Intent(context, DeliveryDetailActivity::class.java)
            startActivity(intent)
        }

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_near_you.layoutManager = linearLayoutManager
        val nearFoodAdapter = NearFoodAdapter(context!!)
        rv_near_you.setHasFixedSize(true)
        rv_near_you.adapter = nearFoodAdapter
        rv_near_you!!.adapter!!.notifyDataSetChanged()

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_discover_restro.layoutManager = linearLayoutManager
        val adapter = DiscoverRestroAdapter(context!!)
        rv_discover_restro.setHasFixedSize(true)
        rv_discover_restro.adapter = adapter
        rv_discover_restro!!.adapter!!.notifyDataSetChanged()
    }


    companion object {
        fun newInstance(): FoodHomeFragment {
            val args = Bundle()
            val fragment = FoodHomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

}