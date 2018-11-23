package com.angelocarly.weighttracker.fragments

import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.angelocarly.weighttracker.R
import com.angelocarly.weighttracker.model.WeightEntry
import com.angelocarly.weighttracker.model.weightEntryConverter
import com.angelocarly.weighttracker.util.DayAxisValueFormatter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*


/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment()
{

    companion object
    {
        fun newInstance(): MainActivityFragment
        {
            return MainActivityFragment()
        }
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)

    }


    fun fetchDataOnline()
    {
        val prefs = this.activity!!.getSharedPreferences("", Context.MODE_PRIVATE)
        var username = prefs.getString("client-username", "")
        var token = prefs.getString("client-token", "")

        FuelManager.instance.basePath = "http://weighttrackerapp.herokuapp.com"
        "/API/weightEntries/$username".httpGet()
                .header(Pair("Authorization", "Bearer $token"))
                .responseString { request, response, result ->
                    //make a GET
                    val (data, error) = result
                    if (error == null && data != null) //success
                    {
                        //map result to json
                        val res = Klaxon().converter(weightEntryConverter).parseArray<WeightEntry>(data)
                        if(res != null)
                        {
                            fillChart(res)
                        }
                    } else //error
                    {
                        Log.d("Fuel", error.toString())
                    }
                }
    }

    private fun createChart()
    {

    }

    private fun fillChart(weightEntries: List<WeightEntry>)
    {
        var entries = ArrayList<Entry>()
        var maxWeight = 0f

        //Sort the received list
        var sortedWeightEntries = weightEntries.sortedBy { w -> w.date }
        //Find the smallest time
        var startTime = Date.from(weightEntries.first().date.toInstant()).time
        for(data in sortedWeightEntries)
        {
            //Store the delta times in the list
            var deltaTime = Date.from(data.date.toInstant()).time - startTime
            entries.add(Entry(deltaTime.toFloat() / 1000, data.weight))

            //Calculate max weight
            if(data.weight > maxWeight) maxWeight = data.weight
        }

        //Create dataset
        var dataSet = LineDataSet(entries, "Weight")
        dataSet.setDrawValues(false)
        dataSet.lineWidth = 5f
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.setDrawHorizontalHighlightIndicator(false)
        dataSet.setDrawFilled(true)

        //Create chart
        var lineData = LineData(dataSet)
        chart.data = lineData
        chart.setDrawMarkers(false)

        //Axis settings
        chart.axisRight.isEnabled = false
        chart.axisLeft.axisMaximum = maxWeight * 1.2f
        chart.axisLeft.axisMinimum = 0f
        chart.xAxis.valueFormatter = DayAxisValueFormatter(chart, startTime)
        chart.legend.isEnabled = false

        chart.viewPortHandler.setMaximumScaleY(1f)

        //Create chart
        chart.invalidate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        fetchDataOnline()

        return inflater.inflate(R.layout.fragment_main, container, false)
    }
}
