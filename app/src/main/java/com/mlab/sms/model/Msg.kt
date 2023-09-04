package com.mlab.sms.model

//created a data class for only info of a sms that is needed
data class Msg(
    val phone: String,
    val sms: String,
    val time : Long,
    val type: Int
)
