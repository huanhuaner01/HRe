package org.huan.hre

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*
import org.huan.hre.realm.ChapterRO
import org.huan.hre.realm.RealmManager
import org.huan.hre.source.ChapterContentResp
import org.huan.hre.source.SourceFactory
import org.huan.hre.util.getPage

class DetailActivity : AppCompatActivity() {
    private lateinit var url :String
    private lateinit var web :String
    private lateinit var bookName :String
    private lateinit var chapterName :String
    private lateinit var content :String
    private var currentPage:Int = 0
    private lateinit var pages :IntArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        url = intent.getStringExtra("url")
        web = intent.getStringExtra("web")
        bookName = intent.getStringExtra("bookName")

        SourceFactory.Create(web).getDetail(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ChapterContentResp> { // 第三步：订阅

                    // 第二步：初始化Observer
                    private var i: Int = 0
                    private var mDisposable: Disposable? = null

                    override fun onSubscribe(@NonNull d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(t: ChapterContentResp) {
//                        Log.i("huan","content : "+t)
                        chapterName = t.chapterName
                        initPage(t.chapterContent)
                        saveChapter()
//                        detail_tv.text = t
                    }

                    override fun onError(@NonNull e: Throwable) {
                        Log.e("huan", "onError : value : " + e.message + "\n")
                    }

                    override fun onComplete() {
                        Log.e("huan", "onComplete" + "\n")
                    }
                })

    }
    private fun saveChapter(){
        val chapterRO = ChapterRO()
        chapterRO.web = web
        chapterRO.url = url
        chapterRO.name = chapterName
        chapterRO.bookName = bookName
        chapterRO.time = System.currentTimeMillis()
        RealmManager.insertOrUpdate(chapterRO)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RealmManager.DBResult<Any>> { // 第三步：订阅

                    // 第二步：初始化Observer
                    private var i: Int = 0
                    private var mDisposable: Disposable? = null

                    override fun onSubscribe(@NonNull d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(t: RealmManager.DBResult<Any>) {
//                        Log.i("huan","content : "+t)

//                        initPage(t.chapterContent)
//                        detail_tv.text = t
                    }

                    override fun onError(@NonNull e: Throwable) {
                        Log.e("huan", "onError : value : " + e.message + "\n")
                    }

                    override fun onComplete() {
                        Log.e("huan", "onComplete" + "\n")
                    }
                })
    }
    private fun initPage(t:String){
        content = t
        pages = getPage(detail_tv, content)
        Log.i("huan","content ：$content")
        for (i in 0 until pages.size) {
          if(i == 0){
              Log.i("huan","page:$i pageString : ${content.subSequence(0,pages[0])} ")
          }else{
              Log.i("huan","page:$i pageString : ${content.subSequence(pages[i-1],pages[i])} ")
          }
        }
        next.setOnClickListener(View.OnClickListener {
            showDetail()
        })
        showDetail()
    }
    private fun showDetail(){

        if(currentPage == 0){
            detail_tv.text = content.subSequence(0,pages[0])
        }else{
            detail_tv.text = content.subSequence(pages[currentPage-1],pages[currentPage])
        }
        currentPage++
        if(currentPage == pages.size){
         next.visibility = View.GONE
        }
    }

}
