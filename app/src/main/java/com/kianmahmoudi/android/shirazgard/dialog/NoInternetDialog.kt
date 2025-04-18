package com.kianmahmoudi.android.shirazgard.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kianmahmoudi.android.shirazgard.R

class NoInternetDialog : DialogFragment() {

    interface InternetDialogListener {
        fun onTryAgain()
        fun onExit()
    }

    private var listener: InternetDialogListener? = null

    fun setListener(listener: InternetDialogListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.no_internet))
            .setMessage(getString(R.string.check_internet))
            .setPositiveButton(getString(R.string.try_again)) { _, _ ->
                listener?.onTryAgain()
            }
            .setCancelable(false)
            .create()
        return dialog
    }

    companion object {
        fun newInstance(listener: InternetDialogListener): NoInternetDialog {
            return NoInternetDialog().apply {
                setListener(listener)
            }
        }
    }
}