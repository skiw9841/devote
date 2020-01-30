package com.ad4th.devote.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.ad4th.devote.utils.AnimUtil
import com.voting.kotlin.utils.LogUtil

/**
 * BaseFragment
 */
open class BaseFragment : Fragment() {
    var startAnimType: Int = 0
    var endAnimType: Int = 0
    private val attachingActivityLock = Any()
    private val syncVariable = false
    private var mActivity: BaseActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun initialize() {}

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        synchronized(attachingActivityLock) {
            mActivity = activity as BaseActivity?
            (attachingActivityLock as Object).notifyAll()
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    fun getmActivity(): BaseActivity? {
        return mActivity
    }

    /**
     * Activity is null bug fix
     */
    fun ctx(activity: Activity?): Activity{
        //activity 가 null 이라면
        activity.let {
            synchronized(attachingActivityLock) {
                while (!syncVariable) {
                    try {
                        (attachingActivityLock as Object).wait()
                    } catch (e: InterruptedException) {
                        LogUtil.d(TAG, e.toString())
                    }

                }
            }
        }
        return activity!!
    }


    override fun startActivity(intent: Intent) {
        if (isAdded) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            super.startActivity(intent)
            startAnimType()
        }
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        if (isAdded) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            super.startActivityForResult(intent, requestCode)
            startAnimType()
        }
    }


    /**
     * 시작 애니메이션 타입
     */
    fun startAnimType() {
        when (startAnimType) {
            ANIM_TYPE_NONE -> return
            ANIM_TYPE_HOLD -> {
                AnimUtil.hold(ctx(mActivity))
                return
            }
            ANIM_TYPE_RIGHT -> {
                AnimUtil.right(ctx(mActivity))
                return
            }
            ANIM_TYPE_LEFT -> {
                AnimUtil.left(ctx(mActivity))
                return
            }
            else -> {
                AnimUtil.left(ctx(mActivity))
                return
            }
        }
    }

    /**
     * Super Toast show
     */
    fun showToast(msg: String) {
        //        SuperToast.show(ctx(mActivity), msg);
    }

    /**
     * 종료 애니메이션 타입
     */
    fun endAnimType() {
        when (endAnimType) {
            ANIM_TYPE_NONE -> return
            ANIM_TYPE_HOLD -> {
                AnimUtil.hold(ctx(mActivity))
                return
            }
            ANIM_TYPE_LEFT -> {
                AnimUtil.left(ctx(mActivity))
                return
            }
            ANIM_TYPE_RIGHT -> {
                AnimUtil.right(ctx(mActivity))
                return
            }
            else -> {
                AnimUtil.right(ctx(mActivity))
                return
            }
        }
    }

    companion object {

        val ANIM_TYPE_LEFT = 0x234
        val ANIM_TYPE_RIGHT = 0X542
        val ANIM_TYPE_HOLD = 0X632
        val ANIM_TYPE_NONE = 0X77

        private val TAG = BaseFragment::class.java.name
    }
}
