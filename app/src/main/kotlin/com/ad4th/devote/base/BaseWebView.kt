package com.ad4th.devote.base

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import java.lang.reflect.Field

class BaseWebView : WebView, View.OnKeyListener {

    private var mActivity: Activity? = null
    var onPageStartListener: OnPageStartListener? = null
    var onPageFinishListener: OnPageFinishListener? = null
    var mOnShouldOverrideUrlListener: OnShouldOverrideUrlListener? = null


    interface OnShouldOverrideUrlListener {
        fun onShouldOverrideUrlLoading(url: String): Boolean
    }

    interface OnPageStartListener {
        fun onPageStart(view: WebView, url: String)
    }

    interface OnPageFinishListener {
        fun onPageFinish(view: WebView, url: String)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    fun init(activity: Activity) {

        this.mActivity = activity
        /* 권한정보 */
        val webSettings = settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.allowContentAccess = true
        /* zoom control false */
        webSettings.setSupportZoom(false)
        webSettings.builtInZoomControls = false
        webSettings.displayZoomControls = false
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        //webview Client
        webViewClient = butlerWebClient()
        //크롬클라이언트
        webChromeClient = butlerWebChromeClient()
    }


    override fun onKey(view: View, i: Int, keyEvent: KeyEvent?): Boolean {
        if (i == KeyEvent.KEYCODE_BACK) {
            if (this.canGoBack()) {
                this.goBack()
                return true
            }
        }
        return false
    }

    override fun destroy() {
        super.destroy()
        try {
            if (sConfigCallback != null) {
                sConfigCallback!!.set(null, null)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    /**
     * Extends WebViewClient
     */
    inner class butlerWebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith("sms:")) {
                //loadAction(Intent.ACTION_SENDTO, Uri.parse(url));
                return true
            } else if (url.startsWith("tel:")) {
                loadAction(Intent.ACTION_DIAL, Uri.parse(url))
                return true
            } else if (url.startsWith("mailto:")) {
                //loadAction(Intent.ACTION_SENDTO, Uri.parse(url));
                return true
            } else if (url.contains("target=blank")) {
                loadAction(Intent.ACTION_VIEW, Uri.parse(url))
                return true
            } else {
                if (mOnShouldOverrideUrlListener != null && mOnShouldOverrideUrlListener!!.onShouldOverrideUrlLoading(url)) {
                    return true
                }
                if (url.startsWith("native")) {
                    loadActionViewIntent(url)
                    return true
                } else {
                    view.loadUrl(url)
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }


        fun loadAction(action: String, uri: Uri) {
            try {
                val intent = Intent(action, uri)
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                mActivity!!.startActivity(intent)
            } catch (e: Exception) {
            }

        }

        fun loadActionViewIntent(url: String) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                mActivity!!.startActivity(intent)
            } catch (e: Exception) {
            }

        }

        /**
         * post history back 에 사용
         */
        override fun onFormResubmission(view: WebView, dontResend: Message, resend: Message) {
            super.onFormResubmission(view, dontResend, resend)
            resend.sendToTarget()
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            if (onPageStartListener != null) {
                onPageStartListener!!.onPageStart(view, url)
            }
            //            mActivity.showWebProgress();
            view.requestFocus()
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (onPageFinishListener != null) {
                onPageFinishListener!!.onPageFinish(view, url)
            }
            //            mActivity.dismissProgress();
            view.requestFocus()
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {}

        override fun onLoadResource(view: WebView, url: String) {
            super.onLoadResource(view, url)
        }
    }

    inner class butlerWebChromeClient : WebChromeClient() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onPermissionRequest(request: PermissionRequest) {
            mActivity!!.runOnUiThread { request.grant(request.resources) }
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            val finalRes = result
            //확인버튼만
            return true
        }

        override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
            val finalRes = result
            //확인 취소버튼
            return true
        }

        /**
         * GeoLocationApi 사용을 시도할때 허용할지 물어보는 알림창을 노출한다.
         */
        override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {}
    }

    companion object {
        private val TAG = BaseWebView::class.java.name
        private var sConfigCallback: Field? = null

        init {
            try {
                sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback")
                sConfigCallback!!.isAccessible = true
            } catch (e: Exception) {
                // ignored
            }

        }
    }
}