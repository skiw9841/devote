package com.ad4th.devote.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    @Throws(ParseException::class)
    fun isInDuration(evalDate: Date, beginDate: Date, endDate: Date): Boolean {
        var result = false

        if (beginDate.compareTo(evalDate) == 0 || endDate.compareTo(evalDate) == 0 ||
                beginDate.before(evalDate) && endDate.after(evalDate)) {
            result = true
        }
        return result
    }

    /**
     * Date 포맷
     */
    fun dateFormet(date: Date, format: String) : String {
        return SimpleDateFormat(format, Locale.US).format(date)
    }


    /**
     * 기간 포맷
     * 양식) 2018년 05월 31일
     */

    fun dateDayTermFormet(beginDate: Date, endDate: Date) : String {

        val day_formet="yyyy년 MM월 dd일 "
        val strBeginDay = SimpleDateFormat(day_formet).format(beginDate)
        val strEndDay = SimpleDateFormat(day_formet).format(endDate)

        var strResult = ""

        // 동일날짜일 경우  - 시간까지 표기
        if(strBeginDay == strEndDay) {
            strResult = strBeginDay
        } else {
            strResult += strBeginDay
            strResult += SimpleDateFormat("~ MM월 dd일 ").format(endDate)
        }
        return  strResult
    }


    /**
     * 기간 포맷
     * 양식) 2018년 05월 31일 PM 2:00~3:00
     */

    fun dateTermFormet(beginDate: Date, endDate: Date) : String {

        val day_formet="yyyy년 MM월 dd일 "
        val time_formet="a HH:mm"
        val strBeginDay = SimpleDateFormat(day_formet).format(beginDate)
        val strEndDay = SimpleDateFormat(day_formet).format(endDate)

        val strBeginTime = SimpleDateFormat(time_formet ,Locale.US).format(beginDate)
        val strEndTime = SimpleDateFormat(time_formet, Locale.US).format(endDate)

        var strResult = ""

        // 동일날짜일 경우  - 시간까지 표기
        if(strBeginDay == strEndDay) {
            strResult = strBeginDay
            if(strBeginTime == strEndTime) {
                strResult += strBeginTime
            } else {
                strResult += strBeginDay
                strResult += SimpleDateFormat("~HH:mm").format(endDate)
            }
        } else {
            strResult += strBeginDay
            strResult += SimpleDateFormat("~mm월 dd일 ").format(endDate)
            if(strBeginTime == strEndTime) {
                strResult += strBeginTime
            }
        }

        return  strResult
    }
}
