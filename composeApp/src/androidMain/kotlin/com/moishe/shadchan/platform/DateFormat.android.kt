package com.moishe.shadchan.platform

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatDayMonthYear(millis: Long): String =
    SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date(millis))
