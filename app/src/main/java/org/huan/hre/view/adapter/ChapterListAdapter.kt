package org.huan.hre.view.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_book_detail.view.*
import java.util.ArrayList
import kotlinx.android.synthetic.main.item_chapter.view.*
import org.huan.hre.DetailActivity
import org.huan.hre.R
import org.huan.hre.source.Book
import org.huan.hre.source.BookDetailResp
import org.huan.hre.source.Menu

/**
 * 目录Adapter
 */
class ChapterListAdapter(val mContext: Activity) :RecyclerView.Adapter<ChapterListAdapter.BaseHolder>(){
    companion object {
        const val TYPE_DETAIL = 0
        const val TYPE_CHAPTER = 1
    }
    var menus = ArrayList<Menu>()
    private var booDetail : Book?=null
    open fun addItems(items: BookDetailResp){
        this.menus = items.chapterlist
        this.booDetail = items.book
        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        if(viewType == TYPE_DETAIL){
           return BookDetailViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_book_detail, parent, false))
        }
        return MenuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_chapter, parent, false))
    }

    override fun getItemCount(): Int {
        if(menus.size!=0){
            return menus.size+1
        }else{
            return 0
        }

    }

    override fun getItemViewType(position: Int): Int {
        if(position == 0 && menus.size!=0) {
            return TYPE_DETAIL
        }
        return  TYPE_CHAPTER
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        holder.onBind()
    }

     inner class MenuViewHolder(itemView : View) :BaseHolder(itemView){
        override fun onBind(){
            if(menus.size == 0) return
            var index  =  position-1
            if(index <0){
                index = 0
            }
             val menu = menus[index]
             itemView.menu_tv_num.text = menu.num
             itemView.menu_tv_title.text = menu.title
             itemView.setOnClickListener({
                 val i = Intent(this@ChapterListAdapter.mContext, DetailActivity::class.java)
                 i.putExtra("url",menu.url)
                 i.putExtra("web",menu.web)
                 this@ChapterListAdapter.mContext.startActivity(i)
             })
         }
    }

    inner class BookDetailViewHolder(itemView : View) :BaseHolder(itemView){
        override fun onBind(){
            if(booDetail==null)return
           itemView.tv_title.text = booDetail!!.title
            itemView.tv_author.text = booDetail!!.author
            itemView.tv_category.text = booDetail!!.category
            itemView.tv_des.text = booDetail!!.description
            itemView.tv_score.text = booDetail!!.score
            itemView.sdv_img.setImageURI(booDetail!!.imgUrl)
        }
    }

    abstract inner class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun onBind()
    }
}