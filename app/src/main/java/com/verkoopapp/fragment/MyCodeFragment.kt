package com.verkoopapp.fragment

import android.os.Bundle
import com.verkoopapp.utils.BaseFragment


class MyCodeFragment: BaseFragment(){
    private  var TAG=MyCodeFragment::class.java.simpleName.toString()
    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
       return TAG
    }

    companion object {
        fun newInstance():MyCodeFragment{
            val agr=Bundle()
            val fragment=MyCodeFragment()
            fragment.arguments=agr
            return fragment
        }
    }
}