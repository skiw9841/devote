package com.ad4th.devote.application

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.ad4th.devote.BuildConfig
import com.ad4th.devote.activity.IntroActivity
import com.ad4th.devote.config.Config
import com.ad4th.devote.config.ConfigDev
import com.ad4th.devote.config.ConfigReal
import com.ad4th.devote.control.ForegroundManager
import com.ad4th.devote.model.EventVo
import com.ad4th.devote.model.IntroVo
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import java.security.Security


class DevoteApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        init()
    }

    fun init() {
        mInstance = this
        mForegroundManager = ForegroundManager.init(this)
        initFabric()
        security()
    }


    private fun initFabric(){

        val fabric = Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(true)
                .build()
        Fabric.with(fabric)
    }

    companion object {

        @JvmStatic
        fun security()= Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)

        val FALAVOR_DEV = "dev"
        val FALAVOR_REAL = "real"
        private var mInstance: DevoteApplication? = null
        var mForegroundManager: ForegroundManager? = null

        /**
         * 어플리케이션이 재시작 되는 경우, 진행중인 작업을 종료하고 돌아갈 최초 Activity
         */
        val mMainActivityClass: Class<*> = IntroActivity::class.java

        /**
         * 개발 및 운영서버 환경 설정
         */
        val serverConfig = server  // dev or real

        /**
         * 메타 데이터
         */
        var metaData: IntroVo?= null

        /**
         *  행사
         */
        var event: EventVo?= null


        /**
         * 현재 타겟 서버 를 리턴
         */
        val server: Config
            get() {
                when (BuildConfig.FLAVOR) {
                    FALAVOR_DEV -> return ConfigDev()
                    FALAVOR_REAL -> return ConfigReal()
                }
                return ConfigReal()
            }

        val context: Context?
            get() = mInstance
    }
}

