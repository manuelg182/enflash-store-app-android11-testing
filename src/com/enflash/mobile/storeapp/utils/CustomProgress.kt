package com.enflash.mobile.storeapp.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.enflash.mobile.storeapp.R

class CustomProgress {

    var dialog: Dialog? = null
        private set

    private lateinit var view: View

    fun show(context: Context, cancelable: Boolean): Dialog? {
        return show(context, "Validando...", cancelable)
    }

    fun show(context: Context, title: CharSequence? = null, cancelable: Boolean = false,
             cancelListener: DialogInterface.OnCancelListener? = null): Dialog? {

        val nullParent: ViewGroup? = null
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflator.inflate(R.layout.progress_bar, nullParent, false)

        try {
            if (title != null && title != "") {
                val tv = view.findViewById<View>(R.id.id_title) as TextView
                tv.text = title
            }
            dialog = Dialog(context)
            dialog!!.setContentView(view)
            dialog!!.setCancelable(cancelable)
            dialog!!.setOnCancelListener(cancelListener)

            val window = dialog!!.window
            if (window != null) {
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                window.setDimAmount(0.0f)
                window.attributes = window.attributes
            }

            dialog!!.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dialog
    }

}