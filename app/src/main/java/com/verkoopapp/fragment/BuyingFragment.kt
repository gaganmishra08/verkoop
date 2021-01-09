package com.verkoopapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R


class BuyingFragment: BaseFragment(){

    override fun getTitle(): Int {
    return 0
    }

    override fun getFragmentTag(): String? {
        return null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.chat_inbox_fragment, container, false)
        return view

    }
        companion object {
        fun newInstance(): BuyingFragment? {
            val bundle=Bundle()
            val fragment=BuyingFragment()
            fragment.arguments=bundle
            return fragment
        }

    }

}