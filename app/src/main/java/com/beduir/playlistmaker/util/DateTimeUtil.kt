package com.beduir.playlistmaker.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object DateTimeUtil {
    fun getYear(dateFormat: String, dateString: String): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        return try {
            val date = formatter.parse(dateString)
            date?.let { getYearFromDate(it) } ?: ""
        } catch (e: ParseException) {
            ""
        }
    }

    private fun getYearFromDate(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR).toString()
    }
}