package org.huan.hre.view.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_history.view.*
import org.huan.hre.ChapterListActivity
import org.huan.hre.R
import org.huan.hre.realm.HistoryRO
import org.huan.hre.source.History

class HistoryAdapter (val mContext: Activity) : RecyclerView.Adapter<BaseViewHolder>(){
     var listData = mutableListOf<History>()

    fun setData(data:List<History>){
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return HistoryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false))
    }

    override fun getItemCount(): Int {
       return listData.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind()
    }

    inner class HistoryViewHolder(itemView: View):BaseViewHolder(itemView){
        override fun onBind() {
            val data = listData[position]
            itemView.tv_title_his.text = data.bookName
            itemView.tv_time_his.text = DateFormat.format("yyyy-MM-dd hh:mm", data.time)
            itemView.setOnClickListener {
                val i = Intent(this@HistoryAdapter.mContext, ChapterListActivity::class.java)
                i.putExtra("book_url",data.pathUrl)
                i.putExtra("web",data.web)
                this@HistoryAdapter.mContext.startActivity(i)
            }
        }

    }

}