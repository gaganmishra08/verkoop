package com.verkoopapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.utils.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.ChatInboxActivity


class SellingFragment: BaseFragment(){
    private var TAG=ChatInboxFragment::class.java.simpleName.toString()
    private lateinit var chatInboxActivity: ChatInboxActivity


    override fun getFragmentTag(): String? {
        return TAG
    }


    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        chatInboxActivity=context as ChatInboxActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.chat_inbox_fragment, container, false)
        return view

    }

    companion object {
        fun newInstance(): SellingFragment? {
            val bundle=Bundle()
            val fragment=SellingFragment()
            fragment.arguments=bundle
            return fragment
        }

    }

}