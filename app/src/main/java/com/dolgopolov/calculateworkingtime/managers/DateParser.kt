package com.dolgopolov.calculateworkingtime.managers

import android.annotation.SuppressLint
import com.dolgopolov.calculateworkingtime.models.DayInformation
import com.dolgopolov.calculateworkingtime.models.WorkingTimeInformation
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.text.StringBuilder

object DateParser {
    private const val DATE_DIVIDER = "."
    private const val HOURS_IN_DAY = 24L
    private const val SECONDS_IN_MINUTE = 60L
    private const val SECONDS_IN_HOUR = 60 * 60L
    private const val SECONDS_IN_DAY = 24 * 60 * 60L


    fun getFormattedDate(calendar: Calendar = Calendar.getInstance()): String {
        val format = SimpleDateFormat("dd${DATE_DIVIDER}MM${DATE_DIVIDER}yyyy", Locale.getDefault())
        return format.format(calendar.time)
    }

    fun getDateNumberFromFormattedDate(date: String) = date.substringBefore(DATE_DIVIDER)

    fun getWorkingTimeFormatted(timeSeconds: Long): String {
        val hours = timeSeconds / SECONDS_IN_HOUR
        val minutes = (timeSeconds - hours * SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
        return StringBuilder()
            .append(hours)
            .append("h")
            .append("\n")
            .append(minutes)
            .append("m")
            .toString()
    }

    fun getWorkingTimeFormatted(list: List<WorkingTimeInformation>) =
        getWorkingTimeFormatted(list.sumOf { it.seconds })

    fun getMonthAndYearDate(calendar: Calendar): String {
        val monthName = SimpleDateFormat("LLLL", Locale.getDefault()).format(calendar.time)
        return StringBuilder()
            .append(monthName)
            .append("\n")
            .append(calendar.get(Calendar.YEAR))
            .toString()
    }

    fun getTodayFormattedDate() = getFormattedDate()

    fun getFormattedTimePassed(secondsPassed: Long): String {
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("GMT")
        return format.format(Date(secondsPassed * 1000))
    }

    fun splitWorkingTime(info: WorkingTimeInformation): List<DayInformation> {
        var countDays = info.seconds / SECONDS_IN_DAY
        if (info.seconds % SECONDS_IN_DAY != 0L) countDays++

        //IF USER WORK ONLY IN TODAY DAY
        if (countDays == 1L) return listOf(
            DayInformation(
                getTodayFormattedDate(),
                listOf(info)
            )
        )

        //IF USER WORK SEVERAL DAYS IN ROW
        val listDays = ArrayList<DayInformation>()

        //HOW MANY WORK TODAY
        val now = Calendar.getInstance()
        val secondsPassToday = (now.get(Calendar.SECOND) +
                now.get(Calendar.MINUTE) * SECONDS_IN_MINUTE +
                now.get(Calendar.HOUR) * SECONDS_IN_HOUR)
        val workingTimeInfoToday =
            WorkingTimeInformation(Random().nextInt(), info.project, secondsPassToday)
        listDays.add(DayInformation(getTodayFormattedDate(), listOf(workingTimeInfoToday)))

        //HOW MANY FULL DAYS PASSED
        val countFullDays = countDays - 2

        if (countFullDays > 0) {
            for (i in 0 until countFullDays) {
                val prevDayWorkingTime =
                    WorkingTimeInformation(Random().nextInt(), workingTimeInfoToday.project, SECONDS_IN_DAY)
                now.add(Calendar.DAY_OF_MONTH, -1)

                val prevDayInfo = DayInformation(getFormattedDate(now), listOf(prevDayWorkingTime))
                listDays.add(prevDayInfo)
            }
        }

        //DAY, WHEN USER STARTED WORK
        val lastPrevDayWorkingInfo = WorkingTimeInformation(
            Random().nextInt(),
            workingTimeInfoToday.project,
            workingTimeInfoToday.seconds - secondsPassToday - countFullDays * SECONDS_IN_DAY
        )
        now.add(Calendar.DAY_OF_MONTH, -1)
        val lastPrevDayInfo = DayInformation(
            getFormattedDate(now),
            listOf(lastPrevDayWorkingInfo)
        )
        listDays.add(lastPrevDayInfo)

        return listDays
    }

    fun calculateProgress(list: List<WorkingTimeInformation>, workingHoursInDay: Int): Float {
        return (list.sumOf { it.seconds }.toFloat() / (workingHoursInDay * SECONDS_IN_HOUR)) * 100
    }
}