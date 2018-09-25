package org.huan.hre

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chapter_list.*
import org.huan.hre.view.adapter.ChapterListAdapter
import org.huan.hre.source.BookDetailResp
import org.huan.hre.source.SourceFactory

class ChapterListActivity : AppCompatActivity() {

    private lateinit var mAdapter: ChapterListAdapter
    private lateinit var bookUrl:String
    private lateinit var web:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_list)
        bookUrl = intent.getStringExtra("book_url")
        web = intent.getStringExtra("web")
        mAdapter = ChapterListAdapter(this)
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv.adapter = mAdapter

        sr_chapter.setOnRefreshListener { getChapterList()}
        sr_chapter.setEnableLoadMore(false)
        sr_chapter.autoRefresh()

    }
    private fun getChapterList(){
        SourceFactory.Create(web).getChapterList(bookUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BookDetailResp> { // 第三步：订阅

                    // 第二步：初始化Observer
                    private var i: Int = 0
                    private var mDisposable: Disposable? = null

                    override fun onSubscribe(@NonNull d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(t: BookDetailResp) {

                        mAdapter.addItems(t)
                        sr_chapter.finishRefresh()
                    }

                    override fun onError(@NonNull e: Throwable) {
                        Log.e("huan", "onError : value : " + e.message + "\n")
                    }

                    override fun onComplete() {
                        Log.e("huan", "onComplete" + "\n")
                    }
                })
    }

}
