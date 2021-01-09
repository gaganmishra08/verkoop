package com.ksmtrivia.common

import android.os.Build
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verkoopapp.utils.BaseFragment
import io.socket.emitter.Emitter

abstract class BaseActivity : AppCompatActivity() {
    private var lastFragmentName = ""

    private val backStackCount: Int
        get() {
            val fm = supportFragmentManager
            return fm.backStackEntryCount
        }

    protected abstract fun backStackChangeListner(backStackCount: Int)

    fun addFragment(fragment: Fragment?, holder: Int) {
        if (fragment != null) {


            val fragmentManager = supportFragmentManager
            fragmentManager.addOnBackStackChangedListener(getListener(holder))

            val transaction = fragmentManager.beginTransaction()


            transaction.addToBackStack(fragment.javaClass.simpleName)
            supportFragmentManager.backStackEntryCount

            transaction.add(holder, fragment, fragment.javaClass.simpleName)
            transaction.commitAllowingStateLoss()
        }
    }


        fun replaceFragment(fragment: Fragment?, holder: Int) {
        if (fragment != null) {
            //isResettingStack = false;
            val fragmentManager = supportFragmentManager
            fragmentManager.addOnBackStackChangedListener(getListener(holder))

            val transaction = fragmentManager.beginTransaction()

            transaction.addToBackStack(fragment.javaClass.simpleName)
            supportFragmentManager.backStackEntryCount
            transaction.replace(holder, fragment, fragment.javaClass.simpleName)
            transaction.commitAllowingStateLoss()

        }
    }


    fun getFragmentByTag(tag: String): Fragment {

        val fragmentManager = supportFragmentManager

        return fragmentManager.findFragmentByTag(tag)!!

    }

    fun replaceFragmentWithSharedElement(fragment: androidx.fragment.app.Fragment?, view: View,
                                         transitionName: String, holder: Int) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            fragmentManager.addOnBackStackChangedListener(getListener(holder))

            val transaction = fragmentManager.beginTransaction()

            transaction.addToBackStack(fragment.javaClass.simpleName)
            supportFragmentManager.backStackEntryCount
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                transaction.addSharedElement(view, transitionName)
            }

            transaction.replace(holder, fragment, fragment.javaClass.simpleName)
            transaction.commit()
        }
    }

    fun removeFragment(fragment: Fragment?) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager
                    .beginTransaction()

            transaction.remove(fragment)
            transaction.commit()
            fragmentManager.popBackStack()
        }
    }


    fun removeTopfragment() {
        val fm = supportFragmentManager
        fm.popBackStack()
    }

    fun removeFragmentByTag(tag: String) {
        val fragmentManager = supportFragmentManager
        val trans = fragmentManager.beginTransaction()
        val frg = fragmentManager.findFragmentByTag(tag)
        trans.remove(frg!!)
        trans.commit()
        fragmentManager.popBackStack()
    }

    //protected boolean isResettingStack = false;
    fun resetBackStack() {
        val fm = supportFragmentManager
        //isResettingStack = true;
        for (i in 1 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
        //isResettingStack = false;
    }


    fun showHomeFragment() {
        val fm = supportFragmentManager
        //fm.removeOnBackStackChangedListener(getListener());
        for (i in 1 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }


    private fun getListener(holder: Int): () -> Unit {
        return {
            val manager = supportFragmentManager

            if (manager != null) {

                //if( !isResettingStack )
                backStackChangeListner(backStackCount)

                val currFrag: BaseFragment? = manager
                        .findFragmentById(holder) as BaseFragment?

                if (lastFragmentName != currFrag?.javaClass?.name) {
                    lastFragmentName = currFrag?.javaClass?.simpleName.toString()
                    currFrag?.onResume()
                }
            }
        }
    }

    abstract val newMessage: Emitter.Listener?
}
