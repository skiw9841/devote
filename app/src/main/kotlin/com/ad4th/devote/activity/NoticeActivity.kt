package com.ad4th.devote.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.ad4th.devote.R
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.base.BaseWebView
import com.ad4th.devote.utils.BlockerActivityUtil
import com.ad4th.devote.utils.SharedProperty
import com.ad4th.devote.widget.CustomToolbar

/**
 *  팝업 액티비티
 */
class NoticeActivity : BaseActivity() {

    private var mBlockerParam: BlockerActivityUtil.BlockerActivityResult? = null
    var baseWebView: BaseWebView? = null
    var customToolbar: CustomToolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0,0)
        setContentView(R.layout.activity_notice)

        // 상태바 색상 변경(WHITE)
        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        init()
    }

    /**
     * 초기화
     */
    fun init() {
        mBlockerParam = BlockerActivityUtil.getParam(this, intent)

        baseWebView=findViewById(R.id.baseWebView)
        customToolbar = findViewById(R.id.customToolbar)

        baseWebView!!.init(this)
        baseWebView!!.loadUrl(noticeUrl)
        customToolbar!!.textViewCenter.text=noticeTitle

        /* 툴바 닫기 버튼 */
        customToolbar!!.imageButtonRight.setOnClickListener { finish() }

        /* 다시보지않기 버튼 */
        findViewById<Button>(R.id.buttonNotView).setOnClickListener {
            mSharedProperty.put(SharedProperty.KEY_NOTICE_NOT_ID, noticeId)
            finish()
        }

        /* 확인 버튼 */
        findViewById<Button>(R.id.buttonConfirm).setOnClickListener { finish() }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
        BlockerActivityUtil.finishBlockerActivity(mBlockerParam)
    }

    companion object {
        private var noticeId: Int = -1
        private var noticeUrl: String = ""
        private var noticeTitle: String = ""

        fun newIntent(context: Context, noticeId: Int, noticeUrl: String, noticeTitle: String): Intent {
            this.noticeId = noticeId
            this.noticeUrl = noticeUrl
            this.noticeTitle = noticeTitle
            return Intent(context, NoticeActivity::class.java)
        }
    }

}

