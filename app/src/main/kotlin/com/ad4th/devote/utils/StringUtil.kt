package com.ad4th.devote.utils

import android.content.Context
import android.text.Html
import android.os.Build
import android.text.Spanned
import java.util.regex.Matcher
import java.util.regex.Pattern


object StringUtil {

    /**
     * 공백 여부 체크
     */
    fun isEmpty(str: String?): Boolean {
        return if (str != null && !str.isEmpty()) {
            false
        } else true
    }

    /**
     * 문자 비교
     */
    fun isStringSame(a: String, b: String): Boolean {
        val aIs = isEmpty(a)
        val bIs = isEmpty(b)
        if (aIs != bIs) return false
        return if (aIs == bIs == true) true else a == b

    }

    /**
     * 문자 치환
     */
    fun replace(s: String, vararg arrStr:Any): String {
        val regex = "\\{.+?\\}".toRegex()
        var str = s
        for(a in arrStr) {
            str = str.replaceFirst(regex, a.toString())

        }
        return str
    }


    /**
     * 클릭보드 복사
     */
    fun setClipboard(context: Context, str: String) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = str
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Copied Text", str)
            clipboard.primaryClip = clip
        }
    }

    /**
     * 클릭보드 내용 가져오기
     */
    fun getClipboard(context: Context) : String {

        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            return clipboard.primaryClip.getItemAt(0).text.toString()
        }catch (e:Exception) {
            return ""
        }
    }

    /**
     * Html
     */
    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

}

