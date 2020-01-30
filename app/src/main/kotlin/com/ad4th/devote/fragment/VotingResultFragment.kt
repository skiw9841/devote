package com.ad4th.devote.fragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.ad4th.devote.R
import com.ad4th.devote.activity.VotingActivity
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseFragment
import com.ad4th.devote.base.BaseRecyclerViewAdapter
import com.ad4th.devote.config.URI
import com.ad4th.devote.control.GlideManager
import com.ad4th.devote.model.VotingResultVo
import com.ad4th.devote.network.RetroCallback
import com.ad4th.devote.utils.StringUtil
import com.ad4th.devote.utils.UiUtil
import java.util.*
import kotlin.comparisons.compareByDescending


class VotingResultFragment : BaseFragment() {


    internal lateinit var view: View

    var textViewSubTitle: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_voting_result, null)
        init()
        return view
    }

    /**
     * 초기화
     */
    fun init() {
        textViewSubTitle = view.findViewById(R.id.textViewSubTitle)
//        // 행사명
        textViewSubTitle!!.text = DevoteApplication.event!!.subTitle

        // 타이틀
        (getmActivity() as VotingActivity).customToolbar!!.textViewCenter.text = getString(R.string.voting_result)

        // 투표결과 조회
        val url = StringUtil.replace(URI.URL_EVENT_RESULT, DevoteApplication.event!!.id!!)
        networkPostSelectListEventResult(url)

    }

    /**
     * 결과 팀 초기화
     */
    fun initResultTeams(teams: List<VotingResultVo>) {

        /* 우승팀 */
        val winnerTeam = teams.maxBy { it.score }
        /* 외 팀 */
        var listTeam = teams.sortedWith(compareByDescending<VotingResultVo> { it.score })

        var listData = ArrayList<VotingResultVo>()
        var winData  = ArrayList<VotingResultVo>()

        var tempScore = -1
        var score  = -1
        for(i in 0 until listTeam.size) {

            // 우승팀
            var team = listTeam[i]
            if (winnerTeam!!.score == team.score) {
                team.team!!.rank = 1
                winData.add(team)
                continue
            }
            if (tempScore == team.score) {
                if(score > 0) {
                    team.team!!.rank = score
                } else {
                    team.team!!.rank = i
                    score = i
                }
            } else {
                team.team!!.rank = i + 1
                score = -1
            }

            tempScore = team.score
            listData.add(team)
        }

        /* 우승팀 */
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = GridLayoutManager(getmActivity(), winData.size, GridLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(GridSpacesItemDecoration(UiUtil.convertDpToPx(getmActivity(), 5f), false))
        //creating our adapter
        val adapter = CustomWinAdapter(getmActivity() as Context, winData)
        adapter.setRecyclerView(recyclerView)
        recyclerView.adapter = adapter

//        /* 배경 처리 */
        var imageViewBg = view.findViewById<ImageView>(R.id.imageViewBg)
        recyclerView.post {
            imageViewBg.layoutParams.height = recyclerView.height
            GlideManager.instance!!.resouceLoad(getmActivity()!!, R.drawable.intro_bg, imageViewBg)
        }

        /* 일반팀 */
        val recyclerViewSub = view.findViewById<RecyclerView>(R.id.baseRecyclerView)
        recyclerViewSub.layoutManager =LinearLayoutManager(getmActivity() as Context)
        //creating our adapter
        val adapterSub = CustomAdapter(getmActivity() as Context, listData)
        adapterSub.setRecyclerView(recyclerViewSub)
        recyclerViewSub.adapter = adapterSub
    }



    /**
     * Voting RecyclerView Custom Adapter
     */
    class CustomWinAdapter(val context: Context, private val teamList: List<VotingResultVo>) : BaseRecyclerViewAdapter<CustomWinAdapter.ViewHolder>() {

        private var glideManager: GlideManager = GlideManager.instance!!

        //this method is returning the view for each item in the list
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomWinAdapter.ViewHolder {
                var v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_voting_result, parent, false)
                return ViewHolder(v)
        }

        //this method is binding the data on the list
        override fun onBindViewHolder(holder: CustomWinAdapter.ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)

            val votingResultVo = teamList[position]

            if (votingResultVo.team != null) {
                //스코어
                holder.textViewScore.text = (votingResultVo.score /*/ DevoteApplication.event!!.icxQuantity!!*/).toInt().toString() + "표" // 2018.10.16 심사의원 점수 반영에 의한 '/5'삭제
                // 2018.10.16 팀넘버 삭제
                // 팀 명
                holder.textViewName.text = StringUtil.fromHtml(/*"<b>" + votingResultVo.team!!.number.toString() + "팀</b> " +*/ "" + votingResultVo.team!!.name )
                //팀 이미지
                glideManager.uriLoad(context, votingResultVo.team!!.imageUrl!!, holder.imageViewTeam)
            }

            // 우승팀이 한팀일 경우 여백을 줘서 크기를 조절한다.
            if(itemCount == 1) {
                holder.itemView.setPadding(UiUtil.convertPxToDp(context, 70f), 0, UiUtil.convertPxToDp(context, 70f), 0)
            }

            holder.itemView.requestLayout()
        }

        override fun getItemCount(): Int {
            return teamList.size
        }

        /** Item */
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewName: TextView = itemView.findViewById(R.id.textViewName)
            var textViewScore: TextView = itemView.findViewById(R.id.textViewScore)
            var imageViewTeam: ImageView = itemView.findViewById(R.id.imageViewTeam)
        }
    }

    /**
     * Voting RecyclerView Custom Adapter
     */
    class CustomAdapter(val context: Context, private val teamList: List<VotingResultVo>) : BaseRecyclerViewAdapter<CustomAdapter.ViewHolder >() {

        //this method is returning the view for each item in the list
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
                var v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_voting_result_base, parent, false)
                return ViewHolder(v)
        }

        //this method is binding the data on the list
        override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)

            val votingResultVo = teamList[position]

            if (votingResultVo.team != null) {
                //스코어
                holder.textViewScore.text = (votingResultVo.score /*/ DevoteApplication.event!!.icxQuantity!!*/).toString() + "표"
                // 2018.10.16 팀넘버 삭제
                //팀 명
                holder.textViewName.text = StringUtil.fromHtml(/*"<b>" + votingResultVo.team!!.number.toString() + "팀</b> " +*/ "" + votingResultVo.team!!.name )
                //등수
                holder.textViewRank.text = votingResultVo.team!!.rank.toString() + "등"
            }

            holder.itemView.requestLayout()
        }

        override fun getItemCount(): Int {
            return teamList.size
        }

        /** Item */
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewName: TextView = itemView.findViewById(R.id.textViewName)
            var textViewScore: TextView = itemView.findViewById(R.id.textViewScore)
            var textViewRank: TextView = itemView.findViewById(R.id.textViewRank)
        }
    }

    /**
     * recyclerView 여백 처리
     */
    inner class GridSpacesItemDecoration(private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if (parent.layoutManager is GridLayoutManager) {
                val layoutManager = parent.layoutManager as GridLayoutManager
                val spanCount = layoutManager.spanCount
                val position = parent.getChildAdapterPosition(view) // item position
                val column = position % spanCount // item column

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing
                    }
                    outRect.bottom = spacing // item bottom
                } else {
                    outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.top = spacing // item top
                    }
                }

            }
        }
    }

    /**
     * 투표결과 조회
     */
    private fun networkPostSelectListEventResult(url: String) {
        getmActivity()!!.mRetroClient.getSelectListEventResult(url, object : RetroCallback<Any>(getmActivity()) {
            override fun onError(t: Throwable) {
                super.onError(t)
            }

            override fun onSuccess(code: Int, receivedData: Any?) {
                super.onSuccess(code, receivedData)

                receivedData?.let {
                    val results = receivedData as List<VotingResultVo>
                    initResultTeams(results)
                }
            }

            override fun onFailure(code: Int) {
                super.onFailure(code)
            }
        })
    }

    /**
     * 프로그레스바 애니메이션
     */
    inner class ProgressBarAnimation(private val progressBar: ProgressBar, private val from: Float, private val to: Float) : Animation() {

        protected override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            val value = from + (to - from) * interpolatedTime
            progressBar.progress = value.toInt()
        }

    }
}
