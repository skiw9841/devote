package com.ad4th.devote.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.ad4th.devote.R
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.config.CODE
import com.ad4th.devote.config.URI
import com.ad4th.devote.eventbus.MainEvent
import com.ad4th.devote.model.AvailVo
import com.ad4th.devote.network.RetroCallback
import com.ad4th.devote.utils.*
import com.google.firebase.messaging.FirebaseMessaging
import de.greenrobot.event.EventBus


/**
 * 메인화면
 */
class MainActivity : BaseActivity() {

    var buttonVoting: Button? = null
    var buttonJoin: Button? = null
    var buttonResult: Button? = null   // add sw 180722
    var textViewMainTitle: TextView? = null
    var textViewTitleComment: TextView? = null
    var menu_bt: Button?=null

    var votingOver: Boolean?= true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onResume() {
        super.onResume()

        /* Net 서버 체크 */
        checkNetType()


        /* 지갑등록여부 체크 */
        handler!!.post({


            if(votingOver != true) {
                /* 투표가능 상태 */
                if (TextUtils.isEmpty(mUserManager.getData().walletAddress)) {
                    // 지갑 미등록
                    buttonJoin!!.visibility = View.GONE       // 투표참여하기 버튼 Hide
                    buttonVoting!!.visibility = View.VISIBLE
                    //textViewTitleComment!!.visibility = View.VISIBLE
                    buttonResult!!.visibility = View.GONE
                } else {
                    // 지갑 등록
                    buttonJoin!!.visibility = View.VISIBLE    // 투표참여하기 버튼 Show
                    buttonVoting!!.visibility = View.GONE
                    //textViewTitleComment!!.visibility = View.GONE
                    buttonResult!!.visibility = View.GONE
                }
            }
            else {
                // 투표 결과확인
                buttonJoin!!.visibility = View.GONE
                buttonVoting!!.visibility = View.GONE
                //textViewTitleComment!!.visibility = View.VISIBLE
                buttonResult!!.visibility = View.VISIBLE
            }
        })
    }

    /**
     * 초기화
     */
    fun init() {
        //Crashlytics.getInstance().crash(); // Force a crash

        EventBus.getDefault().register(this);

        buttonVoting = findViewById(R.id.buttonVoting)
        buttonJoin = findViewById(R.id.buttonJoin)
        buttonResult = findViewById(R.id.buttonResult)
        textViewMainTitle = findViewById(R.id.textViewMainTitle)
        textViewMainTitle!!.text= DevoteApplication.event!!.mainTitle
        textViewTitleComment = findViewById(R.id.textViewTitleComment)
        menu_bt = findViewById(R.id.menu_bt)

        /* 테스트넷 여부에 따른 안내 */
        if(DevoteApplication.event!!.networkType.equals(CODE.NET_TYPE_TEST, true)){
            findViewById<TextView>(R.id.textViewTestNet).visibility= View.VISIBLE
        } else {
            findViewById<TextView>(R.id.textViewTestNet).visibility= View.INVISIBLE
        }

        /* FCM */
         FirebaseMessaging.getInstance().subscribeToTopic(DevoteApplication.event!!.id.toString())

//        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");

        /* 최초실행 여부 체크 */
        if (mSharedProperty.getValue(SharedProperty.KEY_FIRST_RUN, true)) permissionInfoWithValidationEvent() // 퍼미션 이용안내 팝업 > 행사정보체크
        else permissionCheck()
        /* 필수 퍼미션 체크 및 요청 */

        // add sw
        // 투표가능여부
        votingOver = DevoteApplication.event!!.votingOver
        //dismissProgress()

        /* 투표접수하기 */
        buttonVoting!!.setOnClickListener {
            startActivity(WalletActivity.newIntent(this@MainActivity))
        }

        /* 투표참여하기 */
        buttonJoin!!.setOnClickListener {

            var userEventId = mUserManager.getData().eventId;

            if((DevoteApplication.event!!.networkType == CODE.NET_TYPE_TEST
                            && userEventId!! > 0 && userEventId != DevoteApplication.event!!.id)) {
                // 테스트 기존유저에서 새로운 이벤트가 들어왔을 경우
                MaterialDialogUtil.errorAlert(this@MainActivity, getString(R.string.alert_new_event), {
                    mUserManager.reset()
                    startActivity(WalletActivity.newIntent(this@MainActivity))
                }).show()

                // 9월행사대응
                // ICX 초기화
                //IconUtil.TEMP_ICX = 0
                //mSharedProperty.put(SharedProperty.KEY_TEMP_ICX, IconUtil.TEMP_ICX)

                return@setOnClickListener
            }

            if (TextUtils.isEmpty(mUserManager.getData().code)) {
                // QR코드 미등록
                startActivity(WalletRegeditFinishActivity.newIntent(this))
            } else {
                // QR코드 등록
                startActivity(VotingActivity.newIntent(this@MainActivity))
            }
            // 테스트코드
            //startActivity(VotingActivity.newIntent(this@MainActivity))
        }

        /* 마이페이지 */
        /*
        buttonMy!!.setOnClickListener {
            startActivity(MyActivity.newIntent(this@MainActivity))
        }
        */

        // 투표결과확인
        buttonResult!!.setOnClickListener {
            startActivity(VotingActivity.newIntent(this@MainActivity))
        }

        // menu
        menu_bt!!.setOnClickListener {
            startActivity(MenuActivity.newIntent( this@MainActivity))
            overridePendingTransition(R.anim.left_in,0);
        }


        // 9월행사대응
        //IconUtil.TEMP_ICX = mSharedProperty.getValue(SharedProperty.KEY_TEMP_ICX, 0)

    }

    /**
     * 퍼미션 이용안내 Activity 노출 > 이벤트 확인
     */
    fun permissionInfoWithValidationEvent(){
        Thread(Runnable {
            BlockerActivityUtil.startBlockerActivity(this, PermissionInfoActivity.newIntent(this))
            // 최초 실행여부 false 처리
            mSharedProperty.put(SharedProperty.KEY_FIRST_RUN, false)

            super.startActivity(TutorialActivity.newIntent(this))
            permissionCheck()
        }).start()
    }


    /**
     * 테스트/메인 넷 체크
     */
    fun checkNetType() {

        if (!TextUtils.isEmpty(mUserManager.getData().networkType)) {
            var resStringId = 0
            // 기존 발급 지갑이 해당 행사와 네트워크 타입이 다를 경우
            val userVo = mUserManager.getData()
            if (userVo.networkType.equals(DevoteApplication.event!!.networkType, true)) {

                if (userVo.networkType.equals(CODE.NET_TYPE_TEST, true)
                        && DevoteApplication.event!!.networkType.equals(CODE.NET_TYPE_MAIN, true)) {
                    // 테스트넷 > 메인
                    resStringId = R.string.alert_net_type_change_main

                } else if (userVo.networkType.equals(CODE.NET_TYPE_MAIN, true)
                        && DevoteApplication.event!!.networkType.equals(CODE.NET_TYPE_TEST, true)) {
                    // 메인 > 테스트
                    resStringId = R.string.alert_net_type_change_test

                }
            }

            if (resStringId > 0) {
                // 유저정보 초기화
                mUserManager.reset()

                // 안내 alert
                MaterialDialogUtil.confirmAlert(this@MainActivity
                        , getString(R.string.alert_title_event)
                        , getString(resStringId)
                        , getString(R.string.confirm)
                        , {

                })
            }
        }
    }

    /**
     * 필수 퍼미션 체크
     */
    fun permissionCheck() {
        // 퍼미션 체크
        mPermissionManager
                .setContext(this)
                .setIgnore(Manifest.permission.CAMERA)
                .setPermission(Manifest.permission.CAMERA)
                .check()
    }


    /**
     * 투표 결과 페이지 이동
     */
    fun onEvent(mainEvent: MainEvent) {
        startActivity(VotingActivity.newIntent(this@MainActivity))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            return intent
        }
    }


    fun isNetworkPostInsertVotingsAvail(): Boolean {
        var ret: Boolean = false
        val availVo: AvailVo? = null

        val url = StringUtil.replace(URI.URL_EVENT_VOTING_AVAIL, DevoteApplication.event!!.id!!)
        mRetroClient.postInsertVotingsAvail(url, availVo, object : RetroCallback<Any>(this@MainActivity) {
            override fun onError(t: Throwable) {
            }

            override fun onSuccess(code: Int, receivedData: Any?) {
                super.onSuccess(code, receivedData)
                ret = true
            }

            override fun onFailure(code: Int) {
                super.onFailure(code)
                ret = false
            }
        })
        return ret
    }

}
