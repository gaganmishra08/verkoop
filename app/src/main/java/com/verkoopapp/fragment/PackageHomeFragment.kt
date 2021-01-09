package com.verkoopapp.fragment

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.PackageActivity
import com.verkoopapp.adapter.PackageAdapter
import com.verkoopapp.adapter.PackageTypeAdapter
import kotlinx.android.synthetic.main.fragment_package_home.*


class PackageHomeFragment : BaseFragment() {

    val TAG = PackageHomeFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: PackageActivity

    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
        return TAG
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity = context as PackageActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_package_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvPackageTypeList.layoutManager = linearLayoutManager
        val packageTypeAdapter = PackageTypeAdapter(context!!)
        rvPackageTypeList.setHasFixedSize(true)
        rvPackageTypeList.adapter = packageTypeAdapter
        rvPackageTypeList!!.adapter!!.notifyDataSetChanged()

       val  linearLayoutManager1 = GridLayoutManager(homeActivity, 2)
        rv_packageList.layoutManager = linearLayoutManager1
        val packageAdapter = PackageAdapter(context!!)
        rv_packageList.setHasFixedSize(true)
        rv_packageList.adapter = packageAdapter
        rv_packageList!!.adapter!!.notifyDataSetChanged()

    }

    companion object {
        fun newInstance(): PackageHomeFragment {
            val args = Bundle()
            val fragment = PackageHomeFragment()
            fragment.arguments = args
            return fragment
        }
    }


}