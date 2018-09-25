package org.huan.hre.source

import io.reactivex.Observable
import java.util.ArrayList

interface Source {
    open fun getBookListRefresh(path:String):Observable<BookListResp>
    open fun getBookListLoadMore(path:String,page:Int,countPage:Int,nextPageHref:String):Observable<BookListResp>
    open fun getChapterList(bookUrl:String): Observable<BookDetailResp>
    open fun getDetail(url:String):Observable<String>
    open fun searchBook(keyword:String):Observable<BookListResp>
    open fun searchBookloadMore(path:String,page:Int,countPage:Int,nextPageHref:String):Observable<BookListResp>
    open fun getSort():Observable<List<Sort>>
}