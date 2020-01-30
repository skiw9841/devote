package com.ad4th.devote.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ad4th.devote.R
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.config.CODE
import com.ad4th.devote.model.EventVo
import com.ad4th.devote.model.IntroVo
import com.ad4th.devote.model.NoticeVo
import com.ad4th.devote.model.VersionVo
import com.ad4th.devote.network.RetroCallback
import com.ad4th.devote.utils.BlockerActivityUtil
import com.ad4th.devote.utils.DeviceUtil
import com.ad4th.devote.utils.MaterialDialogUtil
import com.ad4th.devote.utils.SharedProperty
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.concurrent.timerTask


/**
 * Intro Activity
 */
class IntroActivity : BaseActivity() {


    var layoutAd4th: ViewGroup? = null
    var layoutIcon: ViewGroup? = null

    var syncLock:Boolean = false

    // 최소 인트로 노출 시간
    val minSecond = 3.0
    // 인트로 로딩
    var doneIntroLoding = false
    // 버전 체크
    var doneVersionCheck = true
    // 푸쉬 받으면 결과 activity로
    var pushResult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        init()

    }

    /**
     * 초기화
     */
    fun init() {

        layoutAd4th = findViewById(R.id.layoutAd4th)
        layoutIcon = findViewById(R.id.layoutIcon)

        handler!!.postDelayed({
            changeIntro()
        }, (1000 * (minSecond / 2)).toLong())
    }

    override fun onResume() {
        super.onResume()
        // Meta 데이터 요청
        networkGetSelectIntro(DeviceUtil.getAppVersion(this))
    }

    /**
     * 인트로 화면 전환
     */
    fun changeIntro() {

        // front fade_out
        val animation_front :Animation =AnimationUtils.loadAnimation(this, R.anim.fade_out)
        animation_front.duration = 1000 * (minSecond / 2).toLong()
        animation_front.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation) {
                Timer().schedule(timerTask {
                    doneIntroLoding = true
                    this.cancel()
                }, 1000)
            }
        })

        layoutIcon!!.startAnimation(animation_front)
        layoutIcon!!.visibility = View.VISIBLE


        // front fade_in
        val animation :Animation =AnimationUtils.loadAnimation(this, R.anim.fade_in)
        animation.duration = 1000 * (minSecond / 2).toLong()
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation) {
                Timer().schedule(timerTask {
                    doneIntroLoding = true
                    this.cancel()
                }, 1000)
            }
        })

        layoutAd4th!!.startAnimation(animation)
        layoutAd4th!!.visibility = View.VISIBLE

    }

    /**
     * 행사 정보 요청
     */
    fun networkGetSelectIntro(version : String) {
        mRetroClient.getSelectIntro(version, object : RetroCallback<Any>(this) {
            override fun onError(t: Throwable) {
                super.onError(t)
            }

            override fun onSuccess(code: Int, receivedData: Any?) {
                super.onSuccess(code, receivedData)
                if (receivedData != null) {
/*      데이터가 존재할 경우   */
                    initProcess(receivedData as IntroVo)
                } else {
/*      데이터가 없을 경우   */

                }
            }
            override fun onFailure(code: Int) {
                super.onFailure(code)
            }
        })
    }

    /**
     * **********************************
     * init 프로세스
     * **********************************
     * 1. 앱버전 체크
     * 2. 공지사항 체크
     * 3. 행사정보 체크
     * 4. 메인 호출 or 앱 종료(진행 이벤트가 없을 경우)
     * **********************************
     */
    fun initProcess(@NotNull introVo: IntroVo) {
        // 메타 데이터 Application 저장
        DevoteApplication.metaData = introVo

        Thread(Runnable {
            /* 1. 앱버전 체크 */
            if (!validationVersion(introVo.version)) return@Runnable
            /* 2. 공지사항 체크 */
            if (introVo.notice != null) validationNotice(introVo.notice)
            /* 3. 행사정보 체크  > Main */
            validationEvent(introVo.events)



        }).start()
    }

    /**
     * 앱 버전 확인
     */
    fun validationVersion(@NotNull version: VersionVo) : Boolean {
        val appVersion: String = DeviceUtil.getAppVersion(this)
        if ( true /*!appVersion.equals(version.latest)*/ ) // 무조건 true, 앱이 1.2.5일때는 1.2.5 가 넘어오고 1.2.6 -> 1.2.6, 1.2.4미만일때는 무조건 1.2.5로 오면 업데이트 여부는 기존과 같게
        {            // 강제 업데이트 처리
            if (version.forceUpdate) {
                handler!!.post({
                    MaterialDialogUtil.alert(this@IntroActivity
                            , getString(R.string.alert_title_update)
                            , getString(R.string.alert_force_update)
                            , null
                            , getString(R.string.confirm)
                            , null
                            , MaterialDialogUtil.OnPositiveListener { dialog ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("market://details?id=" + applicationContext.packageName)
                        startActivity(intent)
                        dialog.dismiss()
                        finish()
                    }).show()
                })
                unblockker()
                return false
            } else if (version.needUpdate) {
                // 옵션 업데이트 처리
                doneVersionCheck = false

                handler!!.post({
                    MaterialDialogUtil.alert(this@IntroActivity
                            , getString(R.string.alert_title_update)
                            , getString(R.string.alert_need_update)
                            , getString(R.string.close)
                            , getString(R.string.confirm)
                            , MaterialDialogUtil.OnNegativeListener {
                        doneVersionCheck = true
                        unblockker()
                    }
                            , MaterialDialogUtil.OnPositiveListener { dialog ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("market://details?id=" + applicationContext.packageName)
                        startActivity(intent)
                        dialog.dismiss()
                        unblockker()
                        android.os.Process.killProcess(android.os.Process.myPid())
                        //finish()
                    }).show()

                })
            }
            if(version.forceUpdate || version.needUpdate ) blockker()
        }

        return true
    }

    /**
     * 공지사항 확인
     */
    fun validationNotice(@NotNull notice: NoticeVo) {

        // 다시보지 않기 체크
        val noticeNotId = mSharedProperty.getValue(SharedProperty.KEY_NOTICE_NOT_ID, -1)
        if (notice.id != noticeNotId) {

            if ((notice.deviceType.equals(CODE.DEVICE_TYPE_ALL, true) || notice.deviceType.equals(CODE.DEVICE_TYPE, true))
                    && notice.noticeStatus!!.equals(CODE.NOTICE_TYPE_SHOW, true)) {
                Timer().schedule(timerTask {
                    // 인트로 화면 애니메이션 종료 확인
                    if (doneIntroLoding) {
                        this.cancel()
                        Thread(Runnable {
                            //if (DevoteApplication.mForegroundManager!!.isBackground) return@Runnable
                            // 노출대상 디바이스 구분 /  노출여부
                            BlockerActivityUtil.startBlockerActivity(this@IntroActivity, NoticeActivity.newIntent(this@IntroActivity, notice.id, notice.url!!, notice.title!!))
                            unblockker()
                        }).start()
                    }

                }, 0, 400)
                blockker()
            }

        }
    }

    /**
     * 이벤트 확인
     */
    fun validationEvent(events: List<EventVo>) {

        var isValidData = false

        if(events != null) {
        /*      행사정보가 존재할 경우   && event.id == 1*/
            for (event in events) {
                if(event.eventStatus.equals(CODE.EVENT_ACTIVATE, true) ) {
                    DevoteApplication.event = event
                    isValidData = true


                    if (!doneIntroLoding || !doneVersionCheck) {
                        Timer().schedule(timerTask {
                            if (doneIntroLoding && doneVersionCheck) {
                                handler!!.post({

                                    pushResult = getIntent().getBooleanExtra("PUSH_RESULT", false)
                                    if( pushResult && !TextUtils.isEmpty(mUserManager.getData().code) && DevoteApplication!!.event!!.id == mUserManager.getData().eventId )
                                    {

                                        // 푸시 결과
                                        startActivity(VotingActivity.newIntent(this@IntroActivity))
                                        intent.putExtra("PUSH_RESULT", true)
                                        finish()
                                    }
                                    else
                                    {
                                        // 메인 페이지 이동
                                        startActivity(MainActivity.newIntent(this@IntroActivity))
                                        finish()
                                    }
                                    this.cancel()
                                })
                            }
                        }, 0, 500)


                    } else {
                        handler!!.post({
                            // 메인 페이지 이동
                            startActivity(MainActivity.newIntent(this))
                            finish()
                        })
                    }
                    break // 현재 위 버전에서는 단건의 행사에 대해서만 처리를 하도록 한다.
                }
            }
        }

        /*      행사정보가 없을 경우   */
        if(events == null || !isValidData) {
            handler!!.post({
                MaterialDialogUtil.alert(this@IntroActivity
                        , getString(R.string.alert_title_event)
                        , getString(R.string.alert_not_continue_event)
                        , null
                        , getString(R.string.confirm)
                        , null
                        , MaterialDialogUtil.OnPositiveListener { dialog ->
                    dialog.dismiss()
                    finish()
                }).show()
            })
        }
    }


    fun blockker() {
        synchronized(syncLock) {
            try {
                (syncLock as Object).wait()
            } catch (e: InterruptedException) {
            }

        }
    }

    fun unblockker() {
        try {
            synchronized(syncLock) {
                (syncLock as Object).notify()
            }
        } catch (e: InterruptedException) {
        }
    }
}
