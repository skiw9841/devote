package com.ad4th.devote.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.ad4th.devote.application.DevoteApplication
import com.voting.kotlin.utils.LogUtil
import java.util.*

object BlockerActivityUtil {

    private val TAG = "BlockerActivity"
    private val SAVED_PARAM_TABLE = "blocker_param_table"
    val mParamKey = "activity_data_param_key"
    var mBlockerParamTable: Hashtable<String, BlockerActivityResult>? = null


    fun startBlockerActivity(context: Context, intent: Intent): BlockerActivityResult {
        if (null == mBlockerParamTable) {
            mBlockerParamTable = Hashtable()
        }
        val aActivityResult = BlockerActivityResult()
        val aRandom = Random()
        var aParamKey: String? = null

        aRandom.setSeed(System.currentTimeMillis())
        aParamKey = aRandom.nextInt().toString()
        mBlockerParamTable!![aParamKey] = aActivityResult

        intent.putExtra("activity_data_param_key", aParamKey)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        context.startActivity(intent)

        LogUtil.d(TAG, "------------- BLOCKED -------------")
        synchronized(aActivityResult) {
            try {
                (aActivityResult as Object).wait()
            } catch (e: InterruptedException) {
            }

        }
        LogUtil.d(TAG, "------------- UNBLOCKED -------------")

        mBlockerParamTable!!.remove(aParamKey)
        return aActivityResult
    }

    fun finishBlockerActivity(param: Any?) {
        if (null != param) {
            synchronized(param) {
                (param as Object).notify()
            }
        }
    }

    fun getParam(context: Context, receivedIntent: Intent?): BlockerActivityResult? {
        if (null == mBlockerParamTable) {
            val aIdentifier = context.resources.getIdentifier("process_killed_by_android_os", "string", context.packageName)
            var aMsg: String? = null
            if (aIdentifier != 0) {
                aMsg = context.getString(aIdentifier)
            } else {
                aMsg = "Restart application..."
            }
            Toast.makeText(context, aMsg, Toast.LENGTH_SHORT).show()
            val aIntent = Intent(context, DevoteApplication.mMainActivityClass)
            aIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            context.startActivity(aIntent)
            return null
        }
        val aExtra = receivedIntent?.getStringExtra("activity_data_param_key")

        mBlockerParamTable!![aExtra].let {
            return mBlockerParamTable!![aExtra] as BlockerActivityResult
        }
        return null
    }

    fun saveBlockerParam(outState: Bundle) {

        val aHashMap = HashMap<String, BlockerActivityResult>()
        mBlockerParamTable?.let { aHashMap.putAll(it) }
        outState.putSerializable(SAVED_PARAM_TABLE, aHashMap)
    }

    fun restoreBlockerParam(savedInstanceState: Bundle) {
        mBlockerParamTable = Hashtable()
        val aHashMap = savedInstanceState.getSerializable(SAVED_PARAM_TABLE) as HashMap<String, BlockerActivityResult>
        mBlockerParamTable!!.putAll(aHashMap)
    }

    data class BlockerActivityResult(var resultCode: Int =1, var data: Intent? = null) {
        fun setBlockerResult(data: Intent?) {
                this.data = data
        }
    }
}

