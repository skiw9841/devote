package com.ad4th.devote.base


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.ad4th.devote.activity.IntroActivity
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.control.GlideManager
import com.ad4th.devote.control.UserManager
import com.ad4th.devote.network.RetroClient
import com.ad4th.devote.utils.AnimUtil
import com.ad4th.devote.utils.SharedProperty
import com.ad4th.devote.utils.permission.PermissionManager
import com.ad4th.devote.widget.MaterialProgressDialog
import com.voting.kotlin.utils.LogUtil

/**
 * BaseActivity
 */
open class BaseActivity : AppCompatActivity(), View.OnClickListener {
    private var startAnimType: Int = 0
    private var endAnimType: Int = 0
    private var mIsSingleTask: Boolean = false
    private var mActivityHandler: Handler? = Handler()
    var mProgressDialog: MaterialProgressDialog? = null

    lateinit var mVotingApplcation: DevoteApplication

    lateinit var mSharedProperty: SharedProperty
    /** Http */
    lateinit var mRetroClient: RetroClient
    /** Image */
    lateinit var mGlideManager: GlideManager
    /** 퍼미션 */
    lateinit var mPermissionManager: PermissionManager
    /** 유저 메타 데이터*/
    lateinit var mUserManager:UserManager

    /**
     * 공용 핸들러
     */
    val handler: Handler?
        get() {
            //null일 경우
            let {
                runOnUiThread { mActivityHandler = Handler() }
            }

            return mActivityHandler
        }

    /**
     * Super Toast show
     */
    fun showToast(msg: String) {
        runOnUiThread {
            Toast.makeText(applicationContext,msg, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * MaterialProgressDialog Show 활성화
     */
    fun showProgress() {
        try {
            mProgressDialog = MaterialProgressDialog()
            mProgressDialog?.show(this)
        } catch (e: Exception) {
            dismissProgress()
        }
    }

    /**
     * MaterialProgressDialog Dismiss 비활성화
     */
    fun dismissProgress() {
        if (mProgressDialog != null) mProgressDialog?.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // 백그라운드에 앱 장시간 있다가 복귀 시 NullPoint Error 처리
//        if(DevoteApplication.event == null && localClassName.lastIndexOf(IntroActivity::class.java.simpleName.toString()) < 0) {
//            startActivity(Intent(this, IntroActivity::class.java))
//            finish()
//            return
//        }

        LogUtil.d(TAG, "onCreate")
        initialize()
    }

    private fun initialize() {
        mVotingApplcation = applicationContext as DevoteApplication
        mPermissionManager = PermissionManager.instance
        mRetroClient = RetroClient.getInstance(mVotingApplcation).createBaseApi()
        mSharedProperty = SharedProperty(mVotingApplcation)
        mGlideManager = GlideManager.instance!!
        mUserManager = UserManager(mVotingApplcation)

    }


    public override fun onStart() {
        super.onStart()
        LogUtil.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        LogUtil.d(TAG, "onStop")
    }

    /**
     * foreground
     */
    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d(TAG, "onDestroy")
    }

    /**
     * 일정시간 지연해서 실행 해야될 쓰레드에 사용
     *
     * @param runnable  Runnable 쓰레드
     * @param delayedMs 딜레이 초
     */
    fun postDelayed(runnable: Runnable, delayedMs: Int) {
        handler!!.postDelayed(runnable, delayedMs.toLong())
    }


    /**
     * background
     */
    override fun onPause() {
        super.onPause()
        LogUtil.d(TAG, "onPause")
    }


    override fun finish() {
        super.finish()
        endAnimType()
    }

    override fun startActivity(intent: Intent) {
        if (mIsSingleTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        super.startActivity(intent)
        LogUtil.d(TAG, "startActivity")
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        if (mIsSingleTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        super.startActivityForResult(intent, requestCode)
        LogUtil.d(TAG, "startActivityForResult")
    }

    /**
     * 시작 화면 이동 모션 셋팅 default start : left ex)     setAnimType(ANIM_TYPE_RIGHT); startActivity(new
     * Intent(getApplicationContext(),CardActivity_.class);
     *
     * @param startAnimType 시작 애니메이션을 바꿀 경우
     */

    fun setStartAnimType(startAnimType: Int) {
        this.startAnimType = startAnimType
    }

    /**
     * 종료 화면 이동 모션 셋팅 default  end : right ex)     setAnimType(ANIM_TYPE_LEFT); finish();
     *
     * @param endAnimType 종료 애니메이션을 바꿀 경우
     */
    fun setEndAnimType(endAnimType: Int) {
        this.endAnimType = endAnimType
    }

    /**
     * 시작 애니메이션 타입
     */
    fun startAnimType() {
        when (startAnimType) {
            ANIM_TYPE_NONE -> return
            ANIM_TYPE_HOLD -> {
                AnimUtil.hold(this)
                return
            }
            ANIM_TYPE_RIGHT -> {
                AnimUtil.right(this)
                return
            }
            ANIM_TYPE_LEFT -> {
                AnimUtil.left(this)
                return
            }
            else -> {
                AnimUtil.left(this)
                return
            }
        }
    }

    /**
     * 종료 애니메이션 타입
     */
    fun endAnimType() {
        when (endAnimType) {
            ANIM_TYPE_NONE -> return
            ANIM_TYPE_HOLD -> {
                AnimUtil.hold(this)
                return
            }
            ANIM_TYPE_LEFT -> {
                AnimUtil.left(this)
                return
            }
            ANIM_TYPE_RIGHT -> {
                AnimUtil.right(this)
                return
            }
            else -> {
                AnimUtil.right(this)
                return
            }
        }
    }

    override fun onClick(view: View) {

    }

    companion object {

        private val TAG = DevoteApplication::class.java.name

        val ANIM_TYPE_LEFT = 0x234
        val ANIM_TYPE_RIGHT = 0X542
        val ANIM_TYPE_HOLD = 0X632
        val ANIM_TYPE_NONE = 0X77
    }
}