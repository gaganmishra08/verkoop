package com.verkoopapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.PackageActivity
import kotlinx.android.synthetic.main.fragment_package_profile.*
import kotlinx.android.synthetic.main.toolbar_location.view.*


class PackageProfileFragment : BaseFragment() {

    val TAG = PackageTripFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: PackageActivity

    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity = context as PackageActivity
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
        return inflater.inflate(R.layout.fragment_package_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pack_profile_tool_lyt.tvHeaderLoc.text = getString(R.string.how_help)
        pack_profile_tool_lyt.ivLeftLocation.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    companion object {
        fun newInstance(): PackageProfileFragment {
            val arg = Bundle()
            val fragment = PackageProfileFragment()
            fragment.arguments = arg
            return fragment
        }
    }
}