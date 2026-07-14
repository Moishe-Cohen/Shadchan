package com.moishe.shadchan.platform

/** Formats an epoch-millis timestamp as "d/M/yyyy", matching the original app's date display. */
expect fun formatDayMonthYear(millis: Long): String
