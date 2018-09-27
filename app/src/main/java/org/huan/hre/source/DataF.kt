package org.huan.hre.source

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class Book(val web:String,val num: String, val imgUrl: String, val title: String, val author:String,val description: String,val score:String,val url:String,val category:String): Parcelable

data class Menu(val web:String,val num:String,val title: String, val url: String)

data class BookListResp(val page:Int ,val countPage:Int,val books:ArrayList<Book> ,val nextPageHref:String)

@Parcelize
data class Sort(val web:String,val text:String,val url:String) : Parcelable

data class BookDetailResp(val book:Book,val chapterlist:ArrayList<Menu>)

data class History(val bookName:String,val pathUrl:String,val time: Long,val web:String)