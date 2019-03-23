package com.kecsot.leitnerbox.common.util

import android.content.Context
import com.kecsot.basekecsot.util.TimeSplitterUtil
import com.kecsot.leitnerbox.R

public object LeitnerBoxTimeFormatUtil {

    // TODO refactor
    public fun formatRuleTime(context: Context, spacedRep: Long, isSkipEmpty: Boolean): String {
        val daysMinsHours = TimeSplitterUtil.getDaysHoursMinsTriple(spacedRep)

        val days = daysMinsHours.days
        val hours = daysMinsHours.hours
        val mins = daysMinsHours.mins

        val resources = context.resources
        val daysString = resources.getQuantityString(R.plurals.plurals_day, days, days)
        val hoursString = resources.getQuantityString(R.plurals.plurals_hour, hours, hours)
        val minString = resources.getQuantityString(R.plurals.plurals_min, mins, mins)

        var list = arrayListOf(
            Pair(days, daysString),
            Pair(hours, hoursString),
            Pair(mins, minString)
        )

        if(isSkipEmpty){
            val filteredList = list.filter { it.first != 0 }
            list = ArrayList(filteredList)
        }

        val stringBuilder = StringBuilder()
        list.forEachIndexed { index, item ->
            val isLastItem = index == list.size - 1

            stringBuilder.append(item.second)
            if (!isLastItem) {
                stringBuilder.append(", ")
            }
        }

        return "( $stringBuilder )"
    }

}