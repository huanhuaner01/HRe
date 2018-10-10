package org.huan.hre.view.adapter

import android.app.Activity
import android.content.Context
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
import kotlinx.android.synthetic.main.item_book.view.*
import kotlinx.android.synthetic.main.item_love.view.*
import org.huan.hre.ChapterListActivity
import org.huan.hre.R
import org.huan.hre.realm.HistoryRO
import org.huan.hre.realm.RealmManager
import org.huan.hre.source.Love
import java.util.*

class LoveAdapter(val mContext: Context) : RecyclerView.Adapter<BaseViewHolder>(),ItemTouchHelperAdapter {
    var listData = mutableListOf<Love>()

    fun setData(data:List<Love>){
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return LoveViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_love, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind()
    }

    inner class LoveViewHolder(itemView: View):BaseViewHolder(itemView){
        override fun onBind() {
            val data = listData[position]
            itemView.item_love_title.text = data.bookName
            itemView.item_love_chapter.text = data.latestChapterName
            if(data.imgUrl!=null) {
                itemView.item_love_img.setImageURI(data.imgUrl)
            }
            itemView.setOnClickListener {
                val i = Intent(this@LoveAdapter.mContext, ChapterListActivity::class.java)
                i.putExtra("book_url",data.pathUrl)
                i.putExtra("web",data.web)
                i.putExtra("title",data.bookName)
                this@LoveAdapter.mContext.startActivity(i)
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