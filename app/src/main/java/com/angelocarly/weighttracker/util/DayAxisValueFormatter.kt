package com.angelocarly.weighttracker.util

import android.util.Log
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DayAxisValueFormatter(private val chart: LineChart, private val startTime : Long = 0) : IAxisValueFormatter
{

    private val dayFormat = SimpleDateFormat("dd/MM")
    private val monthFormat = SimpleDateFormat("MM/yyyy")
    private val yearFormat = SimpleDateFormat("yyyy")

    override fun getFormattedValue(value: Float, axis: AxisBase): String
    {
        //If there are less than 5 months visible, format the values using day and month
        if(chart.visibleXRange / (60 * 60 * 24 * 30) < 5) return dayFormat.format(Date(startTime + value.toLong() * 1000))

        //If there are between 5 and 36 months visible, format the values using day and month
        if(chart.visibleXRange / (60 * 60 * 24 * 30) < 36) return monthFormat.format(Date(startTime + value.toLong() * 1000))

        //Else format with only the year
        return yearFormat.format(Date(startTime + value.toLong() * 1000))
    }
}