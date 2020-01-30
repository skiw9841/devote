package com.ad4th.devote.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.ad4th.devote.R
import com.ad4th.devote.activity.VotingActivity
import com.ad4th.devote.activity.VotingDecideActivity
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseFragment
import com.ad4th.devote.base.BaseRecyclerViewAdapter
import com.ad4th.devote.config.CODE
import com.ad4th.devote.control.GlideManager
import com.ad4th.devote.interfaces.RecyclerViewListener
import com.ad4th.devote.model.TeamVo
import com.ad4th.devote.utils.MaterialDialogUtil
import com.ad4th.devote.utils.UiUtil

class VotingTargetFragment : BaseFragment() {


    internal lateinit var view: View

    var adapter: CustomAdapter? = null

    var textViewSubTitle: TextView? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_voting_target, null)
        init()
        return view
    }

    /**
     * 초기화
     */
    fun init() {

        textViewSubTitle = view.findViewById(R.id.textViewSubTitle)

        // 행사명
        textViewSubTitle!!.text = DevoteApplication.event!!.subTitle

        // 타이틀
        (getmActivity() as VotingActivity).customToolbar!!.textViewCenter.text = getString(R.string.voting_ing)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = GridLayoutManager(getmActivity(), 3, GridLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(GridSpacesItemDecoration(UiUtil.convertDpToPx(getmActivity(), 20f), false))

        for(team in DevoteApplication.event!!.teams!!) {
            team.choice = false
        }

        //creating our adapter
        adapter = CustomAdapter(getmActivity() as Context, DevoteApplication.event!!.teams!!)
        adapter!!.setRecyclerView(recyclerView)
        adapter!!.setChoiceMode(true)
        adapter!!.setHasStableIds(true)
        adapter!!.setOnItemClickListener(object : RecyclerViewListener {
            override fun onItemClick(view: View, position: Int) {

                if (adapter!!.selectedPosition >= 0) {
                    adapter!!.teams[adapter!!.selectedPosition].choice = false
                    adapter!!.notifyItemChanged(adapter!!.selectedPosition)
                }
                adapter!!.teams[position].choice = true
                adapter!!.notifyItemChanged(position)
            }
        })

        recyclerView.adapter = adapter

        /* 투표시작하기 버튼 */
        view.findViewById<Button>(R.id.buttonVotingStart).setOnClickListener {

            val intent: Intent = VotingDecideActivity.newIntent(getmActivity() as Context)
            val teamVo = adapter!!.teams[adapter!!.selectedPosition]
            if(teamVo.choice) {
                intent.putExtra("teamVo", teamVo)
                getmActivity()!!.startActivityForResult(intent, CODE.ACTIVITY_CODE_VOTING)
            } else {
                MaterialDialogUtil.alert(getmActivity() as Context,getString(R.string.alert_voting_team_choice)).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }


    /**
     * Voting RecyclerView Custom Adapter
     */
    class CustomAdapter(val context: Context, val teams: List<TeamVo>) : BaseRecyclerViewAdapter<CustomAdapter.ViewHolder>() {

        private var glideManager: GlideManager = GlideManager.instance!!


        //this method is returning the view for each item in the list
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_voting_target, parent, false)
            v.setOnClickListener(this)
            return ViewHolder(v)
        }

        //this method is binding the data on the list
        override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)

            val teamVo = teams[position]
            // 팀 번호
            holder.textViewNum.text = teamVo.number.toString() + "팀"
            // 팀 명
            holder.textViewName.text = teamVo.name

            // 이미지
            glideManager.uriLoad(context, teamVo.imageUrl!!, holder.imageViewTeam)

            val isChoice: Boolean = teams[position].choice
            if (isChoice) {
                holder.imageViewTeam.setBackgroundResource(R.drawable.border_bora)
                holder.imageViewChoice.setImageResource(R.drawable.ic_check_on2)
                holder.imageViewChoice.setColorFilter(0)
            } else {
                holder.imageViewTeam.setBackgroundResource(R.drawable.border_grey_2px)
                holder.imageViewChoice.setImageResource(R.drawable.ic_check_off)
                holder.imageViewChoice.setColorFilter(context.resources.getColor(R.color.grey4))
            }

            holder.itemView.requestLayout()
        }


        override fun getItemId(position: Int): Long {
            return teams.get(position).id.toLong()
        }
        //this method is giving the size of the list
        override fun getItemCount(): Int {
            return teams.size
        }

        //the class is hodling the list view
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewName: TextView
            var textViewNum: TextView
            var imageViewChoice: ImageView
            var imageViewTeam: ImageView

            init {
                textViewName = itemView.findViewById(R.id.textViewName)
                textViewNum = itemView.findViewById(R.id.textViewNum)
                imageViewChoice = itemView.findViewById(R.id.imageViewChoice)
                imageViewTeam = itemView.findViewById(R.id.imageViewTeam)
            }
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
}
