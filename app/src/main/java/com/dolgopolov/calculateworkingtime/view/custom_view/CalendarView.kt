package com.dolgopolov.calculateworkingtime.view.custom_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dolgopolov.calculateworkingtime.R
import com.dolgopolov.calculateworkingtime.managers.DateParser
import com.dolgopolov.calculateworkingtime.models.DayInformation
import com.dolgopolov.calculateworkingtime.view.adapters.DayInfoHolder
import java.util.*
import javax.inject.Inject

class CalendarView @Inject constructor(private val context: Context) {
    private var view: View? = null
    private var containerDays: ViewGroup? = null
    private var tvMonthAndYearDate: TextView? = null

    var onPreviousMonthClick: (() -> Unit)? = null
    var onNextMonthClick: (() -> Unit)? = null
    var onDaySelected: ((DayInformation) -> Unit)? = null

    fun init(container: ViewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.calendar_view, container, false)
        containerDays = view?.findViewById(R.id.container_days)
        tvMonthAndYearDate = view?.findViewById(R.id.tv_month_and_year)
        val bPrevMonth = view?.findViewById<View>(R.id.b_prev)
        val bNextMonth = view?.findViewById<View>(R.id.b_next)

        bPrevMonth?.setOnClickListener {
            onPreviousMonthClick?.invoke()
        }

        bNextMonth?.setOnClickListener {
            onNextMonthClick?.invoke()
        }

        container.addView(view)
    }

    fun setMonthAndYearDate(monthName: String) {
        tvMonthAndYearDate?.text = monthName
    }

    fun setDays(days: List<DayInformation>, workingHoursInDay: Int) {
        containerDays?.removeAllViews()

        val dayViewHolder = DayInfoHolder()


        days.forEach { dayInfo ->
            val dayView =
                LayoutInflater.from(context)
                    .inflate(R.layout.item_calendar, containerDays, false)

            dayViewHolder.bind(
                dayView,
                dayInfo,
                workingHoursInDay,
                onClick = {
                    onDaySelected?.invoke(dayInfo)
                }
            )

            containerDays?.addView(dayView)
        }
    }

    fun onDestroyView() {
        view = null
        containerDays = null
        tvMonthAndYearDate = null
    }
}