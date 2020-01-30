package com.ad4th.devote.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import java.lang.reflect.Field


class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    //이 것이 스크롤을 막아주는 중요 변수!
    var enabled: Boolean? = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            if (this.enabled!!) {
                //				Log.i("INFO", "스크롤 중..");
                return super.onTouchEvent(event)
            }
        } catch (e: Exception) {
        }

        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.enabled!!) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    // 뷰페이저 안에 뷰페이저가 있을 경우 안쪽 뷰페이저에 스크롤을 먼저 준다.
    override fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        return if (v !== this && v is ViewPager) {
            true
        } else super.canScroll(v, checkV, dx, x, y)
    }

    //페이지 스크롤시 애니메이션 제거를 위해 별도 오버라이딩 false 처리
    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, smoothScroll)
    }

    //페이지 스크롤시 애니메이션 제거를 위해 별도 오버라이딩 false 처리
    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, false)
    }

    //페이징 애니메이션 처리
    fun setMyScroller() {
        try {
            var viewpager: Class<Any> = ViewPager::class.java as Class<Any>
            var scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true;
            scroller.set(this, MyScroller(context))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class MyScroller(context: Context) : Scroller(context, DecelerateInterpolator()) {

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/)
        }
    }
}