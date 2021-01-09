package com.verkoopapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.FoodHomeActivity
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.activity.PackageActivity
import kotlinx.android.synthetic.main.fragment_food_vervo.*


class FoodVervoFragment : BaseFragment(){

    val TAG = FoodVervoFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: FoodHomeActivity


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
        return inflater.inflate(R.layout.fragment_food_vervo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_verkoop.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
        }

        tv_package.setOnClickListener {
            val intent = Intent(context, PackageActivity::class.java)
            startActivity(intent)
        }


    }


    companion object {
        fun newInstance(): FoodVervoFragment {
            val args = Bundle()
            val fragment = FoodVervoFragment()
            fragment.arguments = args
            return fragment
        }
    }

}