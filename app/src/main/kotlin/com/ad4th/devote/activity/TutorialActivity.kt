package com.ad4th.devote.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ad4th.devote.R
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.utils.UiUtil
import com.ad4th.devote.widget.CustomViewPager
import java.util.*
import com.ad4th.devote.R.id.viewPager




/**
 *  팝업 액티비티
 */
class TutorialActivity : BaseActivity() {
    //현재 위치 영역 네비바
    private  var layoutNext : LinearLayout ? =null
    private  var layoutDone : LinearLayout ? =null
    private var viewPager: CustomViewPager? = null
    private var mPagerAdapter: TutorialPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0,0)
        setContentView(R.layout.activity_tutorial)
        init()
    }

    /**
     * 초기화
     */
    fun init() {

        viewPager = findViewById(R.id.viewPager)
        viewPager!!.setMyScroller()

        /* 이전 버튼 */
        findViewById<LinearLayout>(R.id.layoutBack).setOnClickListener {
            handler!!.post {
                viewPager!!.setCurrentItem(viewPager!!.currentItem - 1, true)
            }
        }

        /* 다음 버튼 */
        layoutNext = findViewById<LinearLayout>(R.id.layoutNext)
        layoutNext!!.setOnClickListener {

            handler!!.post {
                viewPager!!.setCurrentItem(viewPager!!.currentItem +1, true)
            }

        }

        /* 완료 버튼 */
        layoutDone = findViewById<LinearLayout>(R.id.layoutDone)
        layoutDone!!.setOnClickListener { finish() }


        var datas = ArrayList<Int>()
        datas.add(R.drawable.step_1)
        datas.add(R.drawable.step_2)
        datas.add(R.drawable.step_3)
        datas.add(R.drawable.step_4)
        datas.add(R.drawable.step_5)

        // 튜토리얼 초기화
        initPagerAdapter(datas)

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, TutorialActivity::class.java)
        }
    }


    /**
     * 튜토리얼 개수
     */
    fun getAdPagerCount(): Int {
        return if (mPagerAdapter != null) {
            mPagerAdapter!!.count
        } else 0
    }

    /**
     * 튜토리얼 초기화
     */
    fun initPagerAdapter(data :ArrayList<Int> ) {
        mPagerAdapter = TutorialPagerAdapter(this, data)

        if (viewPager != null) {
            viewPager!!.adapter = mPagerAdapter
            viewPager!!.offscreenPageLimit = getAdPagerCount() - 1
            viewPager!!.clearOnPageChangeListeners()
            viewPager!!.addOnPageChangeListener(mOnPageChangeListener)
        }
    }

    /**
     * 튜토리얼 화면 전환 이벤트
     */
    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            var position = position

            handler!!.post({
                if(position == viewPager!!.childCount -1) {
                    /* 완료 버튼 */
                    layoutDone!!.visibility=View.VISIBLE
                    layoutNext!!.visibility=View.GONE
                } else {
                    /* 다음 버튼 */
                    layoutDone!!.visibility=View.GONE
                    layoutNext!!.visibility=View.VISIBLE
                }
            })
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    /**
     * 어뎁터 처리
     */
    class TutorialPagerAdapter(context: Context, data: ArrayList<Int>) : PagerAdapter() {
        private val mInflater: LayoutInflater = LayoutInflater.from(context)
        private var mListData: ArrayList<Int>? = null
        private var mUserActionListener: UserActionListener? = null

        init {
            this.mListData = data
        }

        interface UserActionListener {
            fun onSelectItem(selectDto: ImageView)
        }

        fun setUserActionListener(listener: UserActionListener) {
            mUserActionListener = listener
        }


        fun getItem(position: Int): Int? {
            return if (this.mListData == null) null else if (mListData!!.size > position) this.mListData!![position] else null
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imgDrawable = getItem(position)
            val imageView = ImageView(mInflater.context)
            imageView.setImageResource(imgDrawable!!)
            imageView.scaleType =ImageView.ScaleType.FIT_XY
            //        imageView.setImageResource(mInflater.getContext().getDrawable(R.drawable.ad_4_th));
            container.addView(imageView)
            return imageView
        }

        override fun getCount(): Int {
            return mListData?.size ?: 0
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

}