package com.ad4th.devote.base

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.View
import com.ad4th.devote.interfaces.INoDataChanger
import com.ad4th.devote.interfaces.RecyclerViewListener
import java.util.*


abstract class BaseRecyclerViewAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>(), View.OnClickListener, INoDataChanger {

    var mSelectedItems: SparseBooleanArray? = null      // 셀렉트 모드
    var mListener: RecyclerViewListener? = null         // 이벤트 전달할 리스너
    var mRecyclerView: RecyclerView? = null             // recyclerView
    var mLatelyPosition = -1                            // 최근 포지션 값

    private var mIsChoiceMode = false                   // 초이스 모드
    private var mIsSingleChoiceMode = false             // 싱글 초이스 모드
    private var mNoDataView: View? = null               // 데이터 없을시 보여질 뷰

    /**
     * 최근에 선택된 포지션 값
     *
     * 선택된 포지션이 없을경우 -1 리턴
     */
    val selectedPosition: Int
        get() = if (mLatelyPosition != -1) mLatelyPosition else 0

    /**
     * 선택된 포지션 갯수 (멀티 초이스일경우 사용)
     */
    val selectedItemCount: Int
        get() = mSelectedItems!!.size()

    /**
     * 선택된 포지션 값  (멀티 초이스일경우 사용)
     */
    val selectedItemsPositions: List<Int>
        get() {
            val items = ArrayList<Int>(mSelectedItems!!.size())
            for (i in 0 until mSelectedItems!!.size()) {
                items.add(mSelectedItems!!.keyAt(i))
            }
            return items
        }

    /**
     * 싱글 모드 여부 default : false
     *
     * @param isChoiceMode true 일경우 싱글초이스 모드 false 일경우 일반
     */
    fun setChoiceMode(isChoiceMode: Boolean) {
        this.mIsChoiceMode = isChoiceMode
        if (isChoiceMode) mSelectedItems = SparseBooleanArray()
    }

    /**
     * 싱글 모드 여부 default : false
     *
     * @param isChoiceMode true 일경우 싱글초이스 모드 false 일경우 일반
     */
    fun setSingleChoiceMode(isChoiceMode: Boolean) {
        this.mIsSingleChoiceMode = isChoiceMode
        this.mIsChoiceMode = isChoiceMode
        if (isChoiceMode) mSelectedItems = SparseBooleanArray()
    }

    /**
     * 포지션 값을 셀렉트 한다.
     */
    @Synchronized
    fun setSelected(pos: Int) {
        if (-1 < pos && pos != mLatelyPosition) clearSelection(mLatelyPosition)
        mSelectedItems!!.put(pos, true)
        if (!mIsSingleChoiceMode) {
            mLatelyPosition = pos
        }
        runNotify(pos)
    }

    fun runNotify(position: Int) {
        Handler(Looper.myLooper()).post { notifyItemChanged(position) }
    }

    /**
     * 선택된 포지션값을 삭제한다.
     */
    fun clearSelection(pos: Int) {
        if (mSelectedItems!!.get(pos, false)) {
            mSelectedItems!!.delete(pos)
        }
        //notifyItemChanged(pos);
        runNotify(pos)
    }

    /**
     * 모든 선택값을 삭제한다.
     */
    fun clearSelections() {
        if (mSelectedItems!!.size() > 0) {
            mSelectedItems!!.clear()
            notifyDataSetChanged()
        }
    }

    /**
     * 클릭 리스너를 셋팅한다.
     */
    fun setOnItemClickListener(listener: RecyclerViewListener) {
        mListener = listener
    }

    /**
     * 어댑터에 사용되는 뷰를 셋팅한다.
     */
    override fun setRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (mIsChoiceMode) holder.itemView.isActivated = mSelectedItems!!.get(position, false)
    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun onClick(v: View?) {
        if (v != null) {
            val position = mRecyclerView!!.getChildAdapterPosition(v)
            //초이스 모드일경우
            if (mListener != null) mListener!!.onItemClick(v, position)
            if (mIsChoiceMode) setSelected(position)
        }
    }

    /**
     * 데이터가 없을경우 보여질 뷰를 셋팅한다.
     */
    override fun setNoDataView(view: View) {
        mNoDataView = view
    }

    /**
     * true 인 경우 데이터 없는 뷰를 보여주고 리사이클러뷰를 감춘다.
     */
    override fun showNoDataView(isNoData: Boolean) {
        if (mNoDataView == null || mRecyclerView == null) {
            return
        }
        if (isNoData) {
            if (mRecyclerView!!.isShown) {
                mNoDataView!!.visibility = View.VISIBLE
                mRecyclerView!!.visibility = View.GONE
            }
        } else {
            if (mNoDataView!!.isShown) {
                mNoDataView!!.visibility = View.GONE
                mRecyclerView!!.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        private val TAG = BaseRecyclerViewAdapter::class.java.name
    }
}