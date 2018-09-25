package org.huan.hre.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_book_list.view.*
import org.huan.hre.R
import org.huan.hre.view.adapter.BookListAdapter
import org.huan.hre.source.BookListResp
import org.huan.hre.source.Sort
import org.huan.hre.source.SourceFactory

/**
 * A fragment representing a list of Items.
 */
class BookListFragment : Fragment() {
    private var mAdapter: BookListAdapter?=null
    private var sort :Sort?=null
    private var page = 0
    private var countPage = 0
    private var nextPageHref = ""
    companion object {

        const val ARG_SORT = "BOOK_LIST_PATH"

        /**
         * @param path the access path of book list
         */
        @JvmStatic
        fun newInstance(sort: Sort) =
                BookListFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_SORT, sort)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            sort = it.getParcelable(ARG_SORT)
        }
        Log.i("huan","onCreate() ${sort!!.text}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i("huan","onCreateView() ${sort!!.text}")
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        // Set the adapter
        if(mAdapter==null|| mAdapter!!.books.isEmpty()) {
            mAdapter = BookListAdapter(context!!)
            view!!.rv.layoutManager = LinearLayoutManager(context)
            view.rv.adapter = mAdapter
            view.sr.setOnRefreshListener { requestData() }
            view.sr.setOnLoadMoreListener { requestLoadMoreData() }
            view.sr.autoRefresh()
        }else{
            view!!.rv.layoutManager = LinearLayoutManager(context)
            view.rv.adapter = mAdapter
            view.sr.setOnRefreshListener { requestData() }
            view.sr.setOnLoadMoreListener { requestLoadMoreData() }
        }
        return view
    }
    private fun requestData() = SourceFactory.Create(sort!!.web).getBookListRefresh(sort!!.url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BookListResp> { // 第三步：订阅

                // 第二步：初始化Observer
                private var i: Int = 0
                private var mDisposable: Disposable? = null

                override fun onSubscribe(@NonNull d: Disposable) {
                    mDisposable = d
                }

                override fun onNext(t: BookListResp) {
                    if(view==null)return
                    page = t.page
                    countPage = t.countPage
                    nextPageHref = t.nextPageHref

                    mAdapter!!.setItems(t.books)

                    view!!.sr.finishRefresh()
                    if(page == countPage){
                        view!!.sr.setEnableLoadMore(false)
                    }else{
                        view!!.sr.setEnableLoadMore(true)
                    }
                }

                override fun onError(@NonNull e: Throwable) {
                    if(view==null)return
                    view!!.sr.finishRefresh()
                }

                override fun onComplete() {
                }
            })
    private fun requestLoadMoreData() {
        SourceFactory.Create(sort!!.web).getBookListLoadMore(sort!!.url,page,countPage,nextPageHref)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BookListResp> { // 第三步：订阅

                    // 第二步：初始化Observer
                    private var i: Int = 0
                    private var mDisposable: Disposable? = null

                    override fun onSubscribe(@NonNull d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(t: BookListResp) {
                        if(view==null)return
                        page = t.page
                        countPage = t.countPage
                        nextPageHref = t.nextPageHref

                        mAdapter!!.addItems(t.books)
                        view!!.sr.finishLoadMore()
                        if(page == countPage){
                            view!!.sr.setEnableLoadMore(false)
                        }else{
                            view!!.sr.setEnableLoadMore(true)
                        }


                    }

                    override fun onError(@NonNull e: Throwable) {
                        if(view==null)return
                        view!!.sr.finishLoadMore()
                    }

                    override fun onComplete() {
                    }
                })

    }

}
