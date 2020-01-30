package com.ad4th.devote.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.ad4th.devote.R
import com.ad4th.devote.activity.IntroActivity
import com.ad4th.devote.activity.VotingActivity
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseFragment
import com.ad4th.devote.config.CODE
import com.ad4th.devote.config.URI
import com.ad4th.devote.model.AvailVo
import com.ad4th.devote.model.EventVo
import com.ad4th.devote.network.RetroCallback
import com.ad4th.devote.utils.DateUtil
import com.ad4th.devote.utils.MaterialDialogUtil
import com.ad4th.devote.utils.StringUtil

class VotingDisplayFragment : BaseFragment() {


    internal lateinit var view: View
    var textViewEventTerm: TextView? = null
    var textViewSubTitle: TextView? = null
    var textViewVotingInfo: TextView? = null
    var imageViewEvent: ImageView? = null
    var imageViewEventBg: ImageView? = null

    var buttonVotingStart: Button? = null
    var buttonVotingResult: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_voting_display, null)
        init()
        return view;
    }

    /**
     * 초기화
     */
    fun init() {
        if(DevoteApplication.event == null) {
            // 백그라운드에 앱 장시간 있다가 복귀 시 NullPoint Error 처리
            startActivity(Intent(getmActivity(), IntroActivity::class.java))
            return
        }

        textViewEventTerm = view.findViewById(R.id.textViewEventTerm)
        textViewSubTitle = view.findViewById(R.id.textViewSubTitle)
        imageViewEvent = view.findViewById(R.id.imageViewEvent)
        buttonVotingStart = view.findViewById(R.id.buttonVotingStart)
        buttonVotingResult = view.findViewById(R.id.buttonVotingResult)
        textViewVotingInfo = view.findViewById(R.id.textViewVotingInfo)
        imageViewEventBg = view.findViewById(R.id.imageViewEventBg)

        // 타이틀
        (getmActivity() as VotingActivity).customToolbar!!.textViewCenter.text = getString(R.string.app_name)

        // 행사명
        textViewSubTitle!!.text = DevoteApplication.event!!.subTitle
        // 행사기간
        textViewEventTerm!!.text = DateUtil.dateDayTermFormet(DevoteApplication.event!!.eventStartDateTime!!, DevoteApplication.event!!.eventEndDateTime!!)
        // 행사 이미지
        getmActivity()!!.mGlideManager.uriBackgroundLoad(getmActivity()!!, DevoteApplication.event!!.titleImageUrl!!, imageViewEventBg!!)
        getmActivity()!!.mGlideManager.uriLoad(getmActivity()!!, DevoteApplication.event!!.titleImageUrl!!, imageViewEvent!!)

        if(DevoteApplication.event!!.votingOver!! || getmActivity()!!.mUserManager.getData().isVoting) {
            buttonVotingResult!!.visibility = View.VISIBLE
            buttonVotingStart!!.visibility = View.GONE
            textViewVotingInfo!!.text= getString(R.string.voting_result_commnet)

            if(DevoteApplication.event!!.votingOver!!) {
                /* 투표종료 결과 버튼 */
                buttonVotingResult!!.setOnClickListener {
                    (getmActivity() as VotingActivity).replaceFragment(VotingActivity.CODE_VOTING_RESULT)
                }

            } else {
                /* 투표종료 결과 버튼 */
                buttonVotingResult!!.setOnClickListener {
                    networkGetSelectEvent(DevoteApplication.event!!.id.toString()!!)
                }
            }

        } else {
            buttonVotingStart!!.visibility = View.VISIBLE
            buttonVotingResult!!.visibility = View.GONE
            textViewVotingInfo!!.text= getString(R.string.voting_info_commnet)

            /* 투표시작하기 버튼 */
            buttonVotingStart!!.setOnClickListener {
                val availVo = AvailVo(userId = getmActivity()!!.mUserManager.getData()!!.id!!, eventId = DevoteApplication.event!!.id!!)
                //투표가능여부 체크
                networkPostInsertVotingsAvail(availVo)
            }
        }
    }

    /**
     * 투표가능 상태
     */
    fun networkPostInsertVotingsAvail(availVo: AvailVo) {

        val url = StringUtil.replace(URI.URL_EVENT_VOTING_AVAIL, DevoteApplication.event!!.id!!)
        getmActivity()!!.mRetroClient.postInsertVotingsAvail( url, availVo, object : RetroCallback<Any>(getmActivity()!!) {
            override fun onError(t: Throwable) {
                super.onError(t)
            }

            override fun onSuccess(code: Int, receivedData: Any?) {
                super.onSuccess(code, receivedData)

                val availVo = receivedData as AvailVo

                if(availVo.eventVotingStatus.equals(CODE.VOTING_POSSIBLE, true)) {
                    /* 투표가능 상태 */
                    if(availVo.userVotingStatus.equals(CODE.VOTING_POSSIBLE ,true)) {
                        /* 사용자 투표 가능 */
                        (getmActivity() as VotingActivity).replaceFragment(VotingActivity.CODE_VOTING_TARGET)
                    } else {
                        /* 사용자 투표 불가능(중복투표) */
                        MaterialDialogUtil.alert(getmActivity()!!, getString(R.string.alert_overlap_voting)).show()
                    }
                } else {
                    /* 투표기간이 아닐 경우 */
                    MaterialDialogUtil.alert(getmActivity()!!, getString(R.string.alert_not_term_voting)).show()
                }
            }
            override fun onFailure(code: Int) {
                super.onFailure(code)
            }
        })
    }


    /**
     * 이벤트 조회
     */
    fun networkGetSelectEvent(eventId: String) {
        val url = StringUtil.replace(URI.URL_EVENT, eventId)

        getmActivity()!!.mRetroClient.getSelectEvent(url, object : RetroCallback<Any>(getmActivity()!!) {
            override fun onError(t: Throwable) {
                super.onError(t)
            }

            override fun onSuccess(code: Int, receivedData: Any?) {
                super.onSuccess(code, receivedData)
                val eventVo = receivedData as EventVo

                if (eventVo.votingOver!!) {
                    (getmActivity() as VotingActivity).replaceFragment(VotingActivity.CODE_VOTING_RESULT)
                } else {
                    MaterialDialogUtil.customDialog(getmActivity()!!, "",getString(R.string.alert_voting_continue_wait), "", null).show()
                    //MaterialDialogUtil.alert(getmActivity()!!, getString(R.string.alert_voting_continue_wait)).show()
                }
            }

            override fun onFailure(code: Int) {
                super.onFailure(code)
            }
        })
    }

}
