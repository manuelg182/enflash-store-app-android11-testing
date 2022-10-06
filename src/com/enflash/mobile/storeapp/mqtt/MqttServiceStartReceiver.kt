package com.enflash.mobile.storeapp.mqtt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.enflash.mobile.storeapp.utils.FileLog

class MqttServiceStartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            context.startService(Intent(context, ServiceActivity::class.java))
        }catch (ex: Exception){
            if(ex.message != null){
                FileLog.writeToConsole(ex.message!!)
            }
        }
    }
}