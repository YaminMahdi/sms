package com.mlab.sms.presentation

import android.content.ContentResolver
import android.provider.Telephony
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlab.sms.model.Msg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel : ViewModel(){
    private val _smsList by lazy {
        MutableLiveData<MutableList<Pair<String,MutableList<Msg>>>>()
    }
    //mutable to immutable
    val smsList : LiveData<out List<Pair<String,List<Msg>>>> get() = _smsList
                                     //Phone   //conversation of that phone number
    fun saveMsg(sms: Msg){
        //working on Input Output thread for better performance
        viewModelScope.launch(Dispatchers.IO){
            val tmpConv = _smsList.value ?: mutableListOf()
            //adding sms to old conversation if exist
            if(tmpConv.map { it.first }.contains(sms.phone)){
                val ind = tmpConv.map { it.first }.indexOf(sms.phone)
                tmpConv[ind].second.add(sms)
            }
            //creating new conversation
            else{
                tmpConv.add(Pair(sms.phone, mutableListOf(sms)))
            }
            withContext(Dispatchers.Main){
//                savedStateHandle["smsList"]= tmpConv
                _smsList.value = tmpConv

            }
        }
    }

    fun readAllSMS(cr: ContentResolver)
    {
        viewModelScope.launch(Dispatchers.IO){
            val totalMsgList = mutableListOf<Msg>()
            val conversations = mutableListOf<Pair<String,MutableList<Msg>>>()

            //collecting needed data of a sms
            val numberCol = Telephony.TextBasedSmsColumns.ADDRESS
            val textCol = Telephony.TextBasedSmsColumns.BODY
            val typeCol = Telephony.TextBasedSmsColumns.TYPE // 1 - Inbox, 2 - Sent
            val typeTime = Telephony.TextBasedSmsColumns.DATE

            val projection = arrayOf(numberCol, textCol, typeCol, typeTime)

            val cursor = cr.query(
                Telephony.Sms.CONTENT_URI,
                projection, null, null, Telephony.Sms.DATE
            )

            val numberColIdx = cursor!!.getColumnIndex(numberCol)
            val textColIdx = cursor.getColumnIndex(textCol)
            val typeColIdx = cursor.getColumnIndex(typeCol)
            val timeColIdx = cursor.getColumnIndex(typeTime)

            //collecting all sms in a single list
            while (cursor.moveToNext()) {
                val number = cursor.getString(numberColIdx)
                val text = cursor.getString(textColIdx)
                val type = cursor.getString(typeColIdx).toInt()
                val time = cursor.getString(timeColIdx).toLong()

                val msg = Msg(number,text,time,type)
                totalMsgList.add(msg)

                Log.d("TAG", "$msg")
            }
            cursor.close()
            //building conversation
            //separate list for every phone number
            totalMsgList.forEach {msg->
                if(conversations.map { it.first }.contains(msg.phone)){
                    val ind = conversations.map { it.first }.indexOf(msg.phone)
                    conversations[ind].second.add(msg)
                }
                else{
                    conversations.add(Pair(msg.phone, mutableListOf(msg)))
                }
            }
            //sorting is needed so that new sms comes in top
            conversations.sortByDescending { it.second.last().time }

            withContext(Dispatchers.Main){
//                savedStateHandle["smsList"]= conversations
                _smsList.value = conversations

            }
        }

    }
}