package org.huan.hre.view.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_book_detail.view.*
import kotlinx.android.synthetic.main.item_chapter.view.*
import org.huan.hre.DetailActivity
import org.huan.hre.R
import org.huan.hre.source.Book
import org.huan.hre.source.BookDetailResp
import org.huan.hre.source.Menu

/**
 * 目录Adapter
 */
class ChapterListAdapter(val mContext: Activity) :RecyclerView.Adapter<BaseViewHolder>(){
    companion object {
        const val TYPE_DETAIL = 0
        const val TYPE_CHAPTER = 1
    }
    var menus = listOf<Menu>()
    private var booDetail : Book?=null
     fun addItems(items: BookDetailResp){
        this.menus = items.chapterlist
        this.booDetail = items.book
        this.notifyDataSetChanged()
    }
    fun revered(){
        menus = menus.reversed()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if(viewType == TYPE_DETAIL){
           return BookDetailViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_book_detail, parent, false))
        }
        return MenuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_chapter, parent, false))
    }

    override fun getItemCount(): Int {
        return if(booDetail!=null){
            menus.size+1
        }else{
            menus.size
        }

    }

    override fun getItemViewType(position: Int): Int {
        if(position == 0 && booDetail!=null) {
            return TYPE_DETAIL
        }
        return  TYPE_CHAPTER
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind()
    }

     inner class MenuViewHolder(itemView : View) :BaseViewHolder(itemView){
        override fun onBind(){
            if(menus.isEmpty()) return
            var index  =  if(booDetail!=null)position-1 else position
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

    inner class BookDetailViewHolder(itemView : View) :BaseViewHolder(itemView){
        override fun onBind(){
            if(booDetail==null)return
            itemView.tv_title.text = booDetail!!.title
            itemView.tv_author.text = booDetail!!.author
            itemView.tv_category.text = booDetail!!.category
            itemView.tv_des.text = booDetail!!.description
            itemView.tv_score.text = booDetail?.score
            itemView.sdv_img.setImageURI(booDetail!!.imgUrl)
            itemView.tv_revered.setOnClickListener({
                revered()
                if(itemView.tv_revered.text == "正序")
                    itemView.tv_revered.text = "倒序"
                else
                    itemView.tv_revered.text = "正序"
            })
        }
    }

}