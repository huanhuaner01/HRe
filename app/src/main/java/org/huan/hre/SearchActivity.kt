package org.huan.hre

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import org.huan.hre.view.adapter.BookListAdapter
import org.huan.hre.source.BookListResp
import org.huan.hre.source.SourceFactory

class SearchActivity : AppCompatActivity() {
    private var keyword = ""
    private var web = ""
    private var mAdapter: BookListAdapter?=null
    private var page = 0
    private var countPage = 0
    private var nextPageHref = ""
    companion object {
        const val SEARCH_KEY_WORD = "keyword"
        const val SEARCH_WEB = "web"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        keyword = intent.getStringExtra(SEARCH_KEY_WORD)
        web = intent.getStringExtra(SEARCH_WEB)
        mAdapter = BookListAdapter(this)
        rv_search.layoutManager = LinearLayoutManager(this)
        rv_search.adapter = mAdapter
        sr_search.setOnLoadMoreListener { requestLoadMoreData() }
        sr_search.setOnRefreshListener {   search(keyword)}
        sr_search.autoRefresh()
    }

    private fun search(keyword: String) {
         SourceFactory.Create(web).searchBook(keyword)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(object : Observer<BookListResp> { // 第三步：订阅

                     // 第二步：初始化Observer

                     private var mDisposable: Disposable? = null

                     override fun onSubscribe(@NonNull d: Disposable) {
                         mDisposable = d
                     }

                     override fun onNext(t: BookListResp) {
                         if(this@SearchActivity.isFinishing)return
                         page = t.page
                         countPage = t.countPage
                         nextPageHref = t.nextPageHref

                         mAdapter!!.addItems(t.books)
                         sr_search.finishRefresh()
                     }

                     override fun onError(@NonNull e: Throwable) {
                         sr_search.finishRefresh()
                     }

                     override fun onComplete() {
                     }
                 })
    }
    private fun requestLoadMoreData() {
        SourceFactory.Create(web).searchBookloadMore("",page,countPage,nextPageHref)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BookListResp> { // 第三步：订阅

                    // 第二步：初始化Observer
                    private var mDisposable: Disposable? = null

                    override fun onSubscribe(@NonNull d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(t: BookListResp) {
                        if(this@SearchActivity.isFinishing)return
                        page = t.page
                        countPage = t.countPage
                        nextPageHref = t.nextPageHref

                        mAdapter!!.addItems(t.books)
                        sr_search.finishLoadMore()
                        if(page == countPage){
                            sr_search.setEnableLoadMore(false)
                        }else{
                            sr_search.setEnableLoadMore(true)
                        }


                    }

                    override fun onError(@NonNull e: Throwable) {
                        if(this@SearchActivity.isFinishing)return
                        sr_search.finishLoadMore()
                    }

                    override fun onComplete() {
                    }
                })

    }
}
