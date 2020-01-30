package com.ad4th.devote.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.ad4th.devote.R
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.config.CODE
import com.ad4th.devote.config.URI
import com.ad4th.devote.eventbus.MainEvent
import com.ad4th.devote.model.AvailVo
import com.ad4th.devote.model.TeamVo
import com.ad4th.devote.model.UserVo
import com.ad4th.devote.model.VotingVo
import com.ad4th.devote.network.RetroCallback
import com.ad4th.devote.utils.*
import de.greenrobot.event.EventBus

/**
 * 투표 최종 Activity
 */
class VotingDecideActivity : BaseActivity() {


    var teamVo: TeamVo? = null
    var userVo: UserVo?= null
    var editTextPrivateKey:EditText?= null
    var textViewAddress:TextView?= null
    var textViewSubTitle: TextView? = null
    var textViewVotingInfo: TextView? = null
    var imageViewTeam: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_decide)
        init()
    }

    /**
     * 초기화
     */
    fun init() {

        // 상태바 색상 변경(WHITE)
        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // 투표 대상 팀
        teamVo = intent.getSerializableExtra("teamVo") as TeamVo
        // 투표 대상 유저
        userVo = mUserManager.getData()

        textViewSubTitle = findViewById(R.id.textViewSubTitle)
        textViewVotingInfo = findViewById(R.id.textViewVotingInfo)
        imageViewTeam = findViewById(R.id.imageViewTeam)


        // 행사명
        textViewSubTitle!!.text = DevoteApplication.event!!.subTitle
        // 행사기간
        textViewVotingInfo!!.text = StringUtil.fromHtml("ICX를 사용하여 <b><font color='#462c9d'>" + teamVo!!.name + "</font></b>에 투표하시려면 다음 정보를 입력해주세요")
        // 이미지
        mGlideManager.uriLoad(this@VotingDecideActivity, teamVo!!.imageUrl!!,imageViewTeam!!)

        // 이미지
        mGlideManager.uriBackgroundLoad(this@VotingDecideActivity, teamVo!!.imageUrl!!,imageViewTeam!!)

        textViewAddress = findViewById(R.id.textViewAddress)
        editTextPrivateKey = findViewById(R.id.editTextPrivateKey)

        /* 테스트넷 여부에 따른 안내 */
        if(DevoteApplication.event!!.networkType.equals(CODE.NET_TYPE_TEST, true)){
            findViewById<TextView>(R.id.textViewTestNet).visibility= View.VISIBLE
        } else {
            findViewById<TextView>(R.id.textViewTestNet).visibility= View.INVISIBLE
        }

        // 툴바 닫기 버튼
        findViewById<ImageButton>(R.id.imageButtonRight).setOnClickListener { finish() }

        textViewAddress!!.text = userVo!!.walletAddress

        // 투표하기 버튼
        findViewById<Button>(R.id.buttonVotingStart).setOnClickListener {
            /**  1. 투표가능상태 > 2. 잔액조회 > 3. 송금 > 4. 최종 결과 send    */
            val strPrivateKEy = editTextPrivateKey!!.text.toString()
            val errorMsgCode = ValidationUtil.walletPrivateKey(strPrivateKEy)
            if (errorMsgCode != -1) {
                handler?.post({
                    MaterialDialogUtil.alert(this, getString(errorMsgCode)).show()
                })
                return@setOnClickListener
            }

            val availVo = AvailVo(userId = mUserManager.getData()!!.id!!
                    , eventId = DevoteApplication.event!!.id!!)
            networkPostInsertVotingsAvail(availVo)
        }
    }

    /**
     * 잔액조회
     */
    fun walletBalance(strAddress: String) {
        IconUtil.WalletBalance(strAddress, object : IconUtil.WalletCallback(this) {
            override fun successed(result: String) {
                super.successed(result)
                // 투표가능 잔고 확인
                if (result.toDouble() >= DevoteApplication.event!!.icxQuantity!!.toDouble()) {
                    val teamWallet = teamVo!!.wallet!!
                    // 송금
                    walletTransaction(
                            userVo!!.walletAddress!!
                            , teamWallet.address!!
                            , (DevoteApplication.event!!.icxQuantity!! - 0.01).toString() //수수료를 뺴준다.
                            , editTextPrivateKey!!.text.toString())
                } else {
                    MaterialDialogUtil.alert(this@VotingDecideActivity, getString(R.string.alert_voting_not_icx)).show()
                }
            }
        }).start()
    }


// 테스트 코드
//    val from = "hxa9a783effea6e17d78e100c2883beefb6c8f8840"
//    val privateKey = "11220ad65a8d7fa193ca1300c6482c064a3cf39cf486bc9eeb4a84652f2a5674"
//    //        val to = “hxd3498e32a314c8038ecfc1ffd30e85228a04ec22”
//    val to = "123124124124"

    /**
     * 송금
     */
    fun walletTransaction(from: String, to: String, balance: String, privateKey: String) {

        IconUtil.RequestTransaction(from, to, balance, privateKey, object : IconUtil.WalletCallback(this) {
            override fun errored(t: Throwable) {
                super.errored(t)
                MaterialDialogUtil.alert(this@VotingDecideActivity, getString(R.string.alert_network_error)).show()
            }

            override fun failed() {
                super.failed()
            }

            override fun successed(result: String) {
                // 9월행사대응
                //IconUtil.TEMP_ICX -= DevoteApplication.event!!.icxQuantity!!
                //mSharedProperty.put(SharedProperty.KEY_TEMP_ICX, IconUtil.TEMP_ICX)
                //

                // Private Key가 틀릴경우
                if(!TextUtils.isEmpty(result) && result.startsWith("tx validate fail")) {
                    super.successed(result)
                    MaterialDialogUtil.alert(this@VotingDecideActivity, getString(R.string.alert_privateKey_not_valid)).show()
                } else {
                    // 투표 데이터
                    val votingVo = VotingVo(
                            balance = DevoteApplication.event!!.icxQuantity!!.toInt()
                            , txId = result
                            , eventId = DevoteApplication.event!!.id!!.toInt()
                            , userId = userVo!!.id!!.toInt()
                            , teamId = teamVo!!.id)
                    //투표 등록
                    networkPostInsertVotings(votingVo)
                }
            }
        }).start()
    }


    /**
     * 투표가능 상태
     */
    fun networkPostInsertVotingsAvail(availVo: AvailVo) {

        val url = StringUtil.replace(URI.URL_EVENT_VOTING_AVAIL, DevoteApplication.event!!.id!!)
        mRetroClient.postInsertVotingsAvail(url, availVo, object : RetroCallback<Any>(this@VotingDecideActivity) {
            override fun onError(t: Throwable) {
            }

            override fun onSuccess(code: Int, receivedData: Any?) {

                val availVo = receivedData as AvailVo

                if(availVo.eventVotingStatus.equals(CODE.VOTING_POSSIBLE, true)) {
                    /* 투표가능 상태 */
                    if(availVo.userVotingStatus.equals(CODE.VOTING_POSSIBLE ,true)) {
                        /* 투표가능 잔액 확인 */
                        walletBalance(userVo!!.walletAddress!!)

                    } else {
                        super.onSuccess(code, receivedData)
                        /* 사용자 투표 불가능(중복투표) */
                        MaterialDialogUtil.errorAlert(this@VotingDecideActivity,getString(R.string.alert_overlap_voting),{
                            setResult(CODE.RESULT_CODE_VOTING_DONE)
                            finish()
                        }).show()

                    }
                } else {
                    super.onSuccess(code, receivedData)
                    /* 투표기간이 아닐 경우 */
                    MaterialDialogUtil.errorAlert(this@VotingDecideActivity,getString(R.string.alert_not_term_voting),{
                        setResult(CODE.RESULT_CODE_VOTING_DONE)
                        finish()
                    }).show()
                }
            }

            override fun onFailure(code: Int) {
                super.onFailure(code)
            }
        })
    }

    /**
     * 투표등록
     */
    fun networkPostInsertVotings(votingVo: VotingVo) {
        val url = StringUtil.replace(URI.URL_EVENT_VOTING, DevoteApplication.event!!.id!!)
        mRetroClient.postInsertVotings(url, votingVo, object : RetroCallback<Any>(this@VotingDecideActivity) {
            override fun onError(t: Throwable) {
                super.onError(t)
            }

            override fun onSuccess(code: Int, receivedData: Any?) {
                super.onSuccess(code, receivedData)

                val userVo = mUserManager.getData()
                userVo.isVoting = true
                mUserManager.setData(userVo)
                MaterialDialogUtil.customDialog(this@VotingDecideActivity, "",getString(R.string.alert_voting_success_wait), "", {
                    //startActivity(MainActivity.newIntent(this@VotingDecideActivity))
                    setResult(CODE.RESULT_CODE_VOTING_DONE)
                    finish()
                }).show()
            }

            override fun onFailure(code: Int) {
                super.onFailure(code)
            }
        })
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, VotingDecideActivity::class.java)
            return intent
        }
    }
}

