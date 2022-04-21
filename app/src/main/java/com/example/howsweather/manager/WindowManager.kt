package com.example.howsweather.manager

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.howsweather.R
import android.view.WindowManager
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.howsweather.databinding.AlertWindowManagerBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class WindowManager(var context: Context, var description: String) {

    private var windowManager: WindowManager? = null
    private var customNotificationDialogView: View? = null
    private var binding: AlertWindowManagerBinding? = null


    fun setMyWindowManger() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customNotificationDialogView =
            inflater.inflate(R.layout.alert_window_manager, null)
        binding = AlertWindowManagerBinding.bind(customNotificationDialogView!!)
        bindView()
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
            PixelFormat.TRANSLUCENT
        )
        windowManager!!.addView(customNotificationDialogView, params)
    }

    fun bindView() {
        binding?.imageView?.setImageResource(R.drawable.clouds)
        binding?.textView2?.setText(description)
        binding?.button2?.setOnClickListener(View.OnClickListener {
            close()
            stopMyService()
        })
    }

    private fun stopMyService() {
        context.stopService(Intent(context, MyService::class.java))
    }

    private fun close() {
        try {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(
                customNotificationDialogView
            )
            customNotificationDialogView!!.invalidate()
            (customNotificationDialogView!!.parent as ViewGroup).removeAllViews()
        } catch (e: Exception) {
        }
    }
}