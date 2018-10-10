package org.huan.hre

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chapter_list.*
import org.huan.hre.realm.*
import org.huan.hre.source.Book
import org.huan.hre.view.adapter.ChapterListAdapter
import org.huan.hre.source.BookDetailResp
import org.huan.hre.source.Chapter
import org.huan.hre.source.SourceFactory

class ChapterListActivity : AppCompatActivity() {

    private lateinit var mAdapter: ChapterListAdapter
    private lateinit var bookUrl:String
    private lateinit var web:String
    private var title:String = ""
    private var book:Book? = null
    private var latestChapter:Chapter? = null
    private var fristChapter:Chapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_list)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        bookUrl = intent.getStringExtra("book_url")
        web = intent.getStringExtra("web")
        title = intent.getStringExtra("title")
        book = intent.getParcelableExtra("book")
        mAdapter = ChapterListAdapter(this)
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv.adapter = mAdapter

        sr_chapter.setOnRefreshListener { getChapterList()}
        sr_chapter.setEnableLoadMore(false)
        sr_chapter.autoRefresh()

    }
    private fun getLatestChapter(){
        DBSearch.searchLatestChapter(web,title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RealmManager.DBResult<Chapter>> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: RealmManager.DBResult<Chapter>) {
                        latestChapter = t.data
                    }

                    override fun onError(e: Throwable) {

                    }
                })
    }
    private fun addHistory(book: Book?=null){
        val history = HistoryRO()
        history.bookName = title
        history.pathUrl = bookUrl
        history.time = System.currentTimeMillis()
        history.web = web
        RealmManager
                .insertOrUpdate(history)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RealmManager.DBResult<Any>> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: RealmManager.DBResult<Any>) {
                      Log.i("huan","insert success！")
                    }

                    override fun onError(e: Throwable) {

                    }
                })
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
                        if(t.book == null && book!=null)
                            t.book = book
                        if(book == null) book = t.book
                        if(t.chapterlist.isNotEmpty()){
                            fristChapter = Chapter(t.chapterlist[0].title,
                                    t.chapterlist[0].url,
                                    0,
                                    title,
                                    web,
                                    System.currentTimeMillis()
                                                  )
                        }
                        mAdapter.addItems(t)
                        addHistory(t.book)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_love, menu)
        val loveItem = menu?.findItem(R.id.menu_love)

        loveItem?.setOnMenuItemClickListener {
            onClickLove()
            return@setOnMenuItemClickListener true

        }

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onClickLove(){
        if(book == null) return
        if(fristChapter==null)return
        val love = LoveRO()
        love.bookName = title
        love.pathUrl = bookUrl
        love.time = System.currentTimeMillis()
        love.web = web
        love.imgUrl = book!!.imgUrl
        val chapter = ChapterRO()
        if(latestChapter !=null){
            chapter.bookName = latestChapter!!.bookName
            chapter.name = latestChapter!!.name
            chapter.status = latestChapter!!.status
            chapter.time = latestChapter!!.time
            chapter.url = latestChapter!!.url
            chapter.web = web
        }else{
            chapter.bookName = fristChapter!!.bookName
            chapter.name = fristChapter!!.name
            chapter.status = fristChapter!!.status
            chapter.time = fristChapter!!.time
            chapter.url = fristChapter!!.url
            chapter.web = web
        }

        love.lotestChapter = chapter
        RealmManager
                .insertOrUpdate(love)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RealmManager.DBResult<Any>> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: RealmManager.DBResult<Any>) {
                        Log.i("huan","insert success！")
                    }

                    override fun onError(e: Throwable) {
                    }

                })
    }
}
