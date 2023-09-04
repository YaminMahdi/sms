package com.mlab.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast

//using receiver like this with priority in manifest so that Toast work when app is closed
open class MsgReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try{
            //converting intent data to sms List
            val sms = Telephony.Sms.Intents.getMessagesFromIntent(intent).toList()
            if(sms.isNotEmpty()){
                //showing Toast not Notification as you asked for
                Toast.makeText(context, sms[0].messageBody , Toast.LENGTH_SHORT).show()
            }
        }
        // Exception can occur while getting data from intent
        catch (e : Exception){
            e.printStackTrace()
        }
    }
}