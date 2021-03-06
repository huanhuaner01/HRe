package org.huan.hre.view.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_history.view.*
import org.huan.hre.ChapterListActivity
import org.huan.hre.R
import org.huan.hre.realm.HistoryRO
import org.huan.hre.realm.RealmManager
import org.huan.hre.source.History
import java.util.*

class HistoryAdapter (val mContext: Activity) : RecyclerView.Adapter<BaseViewHolder>(),ItemTouchHelperAdapter{


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
                i.putExtra("title",data.bookName)
                this@HistoryAdapter.mContext.startActivity(i)
            }
        }

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        //交换位置
        Collections.swap(listData,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }

    override fun onItemDissmiss(position: Int) {
        //移除数据
        RealmManager.delete(HistoryRO::class.java,"bookName",listData[position].bookName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RealmManager.DBResult<Any>> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: RealmManager.DBResult<Any>) {
                    }

                    override fun onError(e: Throwable) {
                    }
                })
        listData.removeAt(position)
        notifyItemRemoved(position)
    }

}