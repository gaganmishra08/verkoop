package com.stripe.example.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.verkoopapp.R

class ErrorDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments!!.getString("title")
        val message = arguments!!.getString("message")

        return AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()
    }

    companion object {
        fun newInstance(title: String, message: String): ErrorDialogFragment {
            val fragment = ErrorDialogFragment()

            val args = Bundle()
            args.putString("title", title)
            args.putString("message", message)

            fragment.arguments = args

            return fragment
        }
    }
}
