package com.verkoopapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.verkoopapp.R
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.activity.PackageActivity
import kotlinx.android.synthetic.main.fragment_package_vervo.tv_verkoop
import kotlinx.android.synthetic.main.fragment_package_vervo.tv_food


class PackageVervoFragment : Fragment() {
//    lateinit var tv_food: TextView
//    lateinit var tv_verkoop: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//        tv_food = view?.findViewById(R.id.tv_food) as TextView
//        tv_verkoop = view?.findViewById(R.id.tv_verkoop) as TextView
        return inflater.inflate(R.layout.fragment_package_vervo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_verkoop.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
        }

        tv_food.setOnClickListener {
            val intent = Intent(context, PackageActivity::class.java)
            startActivity(intent)
        }

    }



    companion object {
        fun newInstance(): PackageVervoFragment {
            val args = Bundle()
            val fragment = PackageVervoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}