package com.verkoopapp.utils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.widget.ImageView
import androidx.annotation.StringRes
import com.ksmtrivia.common.BaseActivity

/**
 * Created by admin on 4/3/2018.
 */

abstract class BaseFragment : Fragment() {
    @StringRes
    abstract fun getTitle(): Int
    abstract fun getFragmentTag(): String?

    val isActivityAlive: Boolean
        get() {
            val activity = activity as FragmentActivity
            return !activity.isFinishing
        }
    //public abstract void setTitle();

    fun replaceFragment(fragment: Fragment, holder: Int) {
        (activity as BaseActivity).replaceFragment(fragment, holder)
    }

    fun replaceFragmentWithSharedElement(frg: Fragment, iv: ImageView, transitionName: String, holder: Int) {
        (activity as BaseActivity).replaceFragmentWithSharedElement(frg, iv, transitionName, holder)
    }

    fun getFragmentByTag(tag: String): Fragment {
        return (activity as BaseActivity).getFragmentByTag(tag)
    }

    fun addFragment(fragment: Fragment, holder: Int) {
        (activity as BaseActivity).addFragment(fragment, holder)
    }

    fun setTitle(title: String) {
        if (activity != null) {
            activity?.title = title
        }
    }

    fun setHeaderTitle(title: String) {

    }

    fun setHeaderTitle(stringResId: Int) {
        setHeaderTitle(getString(stringResId))
    }

    fun removeTopfragment() {
        (activity as BaseActivity).removeTopfragment()
    }

    fun removeFragmentByTag(tag: String) {
        (activity as BaseActivity).removeFragmentByTag(tag)
    }

    fun showHomeFragment() {
        (activity as BaseActivity).showHomeFragment()
    }
}
