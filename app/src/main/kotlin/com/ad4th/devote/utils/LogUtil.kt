package com.voting.kotlin.utils


import android.content.Context
import android.util.Log
import android.widget.Toast
import com.ad4th.devote.BuildConfig

object LogUtil {
    private val DEBUG_MODE = BuildConfig.DEBUG
    private val TAG_REGREX = "%s="

    fun d(log: String) {
        if (DEBUG_MODE) {
            val tag = String.format(TAG_REGREX, log)
            Log.d(tag, log)
        }
    }

    fun d(cls: Class<*>, log: String) {
        if (DEBUG_MODE) {
            Log.d(cls.canonicalName, log)
        }
    }

    fun d(tag: String, log: String?) {
        if (DEBUG_MODE) {
            Log.d(tag, log)
        }
    }

    fun i(log: String) {
        if (DEBUG_MODE) {
            val tag = String.format(TAG_REGREX, log)
            Log.i(tag, log)
        }
    }

    fun i(cls: Class<*>, log: String) {
        if (DEBUG_MODE) {
            Log.i(cls.canonicalName, log)
        }
    }

    fun i(tag: String, log: String) {
        if (DEBUG_MODE) {
            Log.i(tag, log)
        }
    }

    fun toast(ctx: Context, log: String) {
        if (DEBUG_MODE) {
            val tag = String.format(TAG_REGREX, log)
            Log.d(tag, log)
            Toast.makeText(ctx, log, Toast.LENGTH_LONG).show()
        }
    }

    fun e(tag: String, error: Exception) {
        if (DEBUG_MODE) {
            Log.e(tag, "error", error)
        }
    }

    fun e(tag: String, log: String) {
        if (DEBUG_MODE) {
            Log.e(tag, log)
        }
    }

    fun map(tag: String, data: Map<String, String>) {
        val iterator = data.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = data[key]
            d(tag, "key=$key,value=$value")
        }
    }

    fun list(tag: String, list: List<Map<String, String>>) {
        for (i in list.indices) {
            val data = list[i]
            map(tag, data)
        }
    }
}