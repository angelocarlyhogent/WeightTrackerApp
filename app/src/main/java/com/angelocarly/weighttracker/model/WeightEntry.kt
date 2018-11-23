package com.angelocarly.weighttracker.model

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import java.io.Serializable
import java.time.ZonedDateTime

class WeightEntry(val date: ZonedDateTime, val name: String, val weight: Float, val id: String = "") : Serializable
{
    override fun toString(): String
    {
        return """{"date" : "$date", "name" : "$name", "weight" : "$weight", "id" : "$id"}"""
    }
}

val weightEntryConverter = object: Converter
{
    override fun canConvert(cls: Class<*>)
            = cls == WeightEntry::class.java

    override fun toJson(value: Any): String
            = """{"date" : "${(value as WeightEntry).date}", "name" : "${value.name}", "weight" : "${value.weight}", "_id" : "${value.id}"}"""

    override fun fromJson(jv: JsonValue)
            = WeightEntry(
            ZonedDateTime.parse(jv.objString("date")),
            jv.objString("name"),
            (jv.obj!!.get("weight")).toString().toFloat(),
            jv.objString("_id")
    )
}