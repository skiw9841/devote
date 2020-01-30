package com.ad4th.devote.utils

import android.app.Activity
import android.content.Context

class SharedProperty(c: Context) {


    private val PREF_NAME = "com.ad4th.devote"

    val all: Map<String, *>
        get() {
            val pref = mContext?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)!!
            return pref.all
        }

    init {
        mContext = c
    }

    fun put(key: String, value: String) {
        val pref = mContext?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)!!
        val editor = pref.edit()

        editor.putString(key, value)
        editor.commit()
    }

    fun put(key: String, value: Boolean) {
        val pref = mContext?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)!!
        val editor = pref.edit()

        editor.putBoolean(key, value)
        editor.commit()
    }

    fun put(key: String, value: Int) {
        val pref = mContext?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)!!
        val editor = pref.edit()

        editor.putInt(key, value)
        editor.commit()
    }

    fun getValue(key: String, dftValue: String): String? {
        val pref = mContext?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)!!

        try {
            return pref.getString(key, dftValue)
        } catch (e: Exception) {
            return dftValue
        }

    }

    fun getValue(key: String, dftValue: Int): Int {
        val pref = mContext?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)!!

        try {
            return pref.getInt(key, dftValue)
        } catch (e: Exception) {
            return dftValue
        }

    }

    fun getValue(key: String, dftValue: Boolean): Boolean {
        val pref = mContext?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)!!

        try {
            return pref.getBoolean(key, dftValue)
        } catch (e: Exception) {
            return dftValue
        }

    }

    fun removeAll() {
        val pref = mContext?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)!!
        val editor = pref.edit()
        editor.clear()
        editor.commit()

    }

    fun remove(value: String) {
        val pref = mContext?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)!!
        val editor = pref.edit()
        editor.remove(value)
        editor.commit()

    }

    companion object {
        val KEY_USER_DATA               = "key.devote.userData"                 // User 정보
        val KEY_FIRST_RUN               = "key.devote.firstRun"                 // 앱 최초실행여부
        val KEY_PUSH_TOKEN              = "key.devote.pushToken"                // 푸시토큰
        val KEY_NOTICE_NOT_ID           = "key.devote.noticeNotId"              // 공지 다시보지않기

        val KEY_TEMP_ICX                = "key.devote.temp_icx"

        internal var mContext: Context? =null
    }

}