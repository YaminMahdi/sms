package com.mlab.sms.util

import java.text.SimpleDateFormat
import java.util.Locale

//converted TimeMillis to user readable format
fun Long.toDateTime(): String{
    //time with date
    var date = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(this)
    val day = SimpleDateFormat("dd MMM", Locale.US).format(System.currentTimeMillis())

    //time without date if it's today
    if (day == date.split(", ")[0])
        date = date.split(", ")[1]
    return date.toString()
}