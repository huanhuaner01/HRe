package org.huan.hre.view.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_book.view.*
import org.huan.hre.ChapterListActivity
import org.huan.hre.R
import org.huan.hre.source.Book
import java.util.ArrayList

/**
 * 书本列表
 */
class BookListAdapter(val mContext: Context) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>(){
    var books = ArrayList<Book>()

    open fun setItems(items: ArrayList<Book>){
        this.books = items
        this.notifyDataSetChanged()
    }
    open fun addItems(items: ArrayList<Book>){
        this.books.addAll(items)
        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView  = LayoutInflater.from(mContext).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.onBind(books[position])
    }

    inner class BookViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun onBind(book:Book){
            itemView.item_book_title.text = book.title
            itemView.item_book_author.text = book.author
            itemView.item_book_des.text = book.description
            itemView.item_book_img.setImageURI(book.imgUrl)
            itemView.setOnClickListener( {
                val i = Intent(this@BookListAdapter.mContext, ChapterListActivity::class.java)
                i.putExtra("book_url",book.url)
                i.putExtra("web",book.web)
                this@BookListAdapter.mContext.startActivity(i)
            })
        }
    }

}