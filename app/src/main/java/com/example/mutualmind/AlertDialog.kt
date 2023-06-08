package com.example.mutualmind

import android.app.Dialog
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertDialog {

    fun showLoadingDialog(context: Context): Dialog {
        val dialog = Dialog(context)

        dialog.setContentView(R.layout.loading_alert_dialog)
        dialog.setCancelable(false)
        dialog.show()

        return dialog
    }

    fun dismissLoadingDialog(dialog: Dialog) {
        dialog.dismiss()
    }
}