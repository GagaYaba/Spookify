package com.ynov.spookify.ui.util

import androidx.annotation.StringRes
import com.ynov.spookify.R
import java.time.LocalTime
import java.util.Locale

fun Int.toDurationText(): String = "%d:%02d".format(Locale.US, this / 60, this % 60)

fun Long.toDurationText(): String = (this / 1000).toInt().toDurationText()

fun Int.toCompactCount(): String = when {
    this >= 1_000_000 -> "%.1fM".format(Locale.US, this / 1_000_000f)
    this >= 1_000 -> "%.1fk".format(Locale.US, this / 1_000f)
    else -> toString()
}

enum class Greeting(@StringRes val titleRes: Int) {
    Day(R.string.greeting_day),
    Evening(R.string.greeting_evening),
}

fun greetingForHour(hour: Int): Greeting = if (hour in 5..17) Greeting.Day else Greeting.Evening

fun currentGreeting(): Greeting = greetingForHour(LocalTime.now().hour)
