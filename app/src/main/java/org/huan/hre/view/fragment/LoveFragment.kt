package org.huan.hre.view.fragment

import android.app.Activity
import android.app.SearchManager
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
import org.huan.hre.realm.DBSearch
import org.huan.hre.realm.RealmManager
import org.huan.hre.source.BookListResp
import org.huan.hre.source.Love
import org.huan.hre.source.Sort
import org.huan.hre.source.SourceFactory
import org.huan.hre.view.adapter.BookListAdapter
import org.huan.hre.view.adapter.LoveAdapter

/**
 * 收藏
 */
class LoveFragment : Fragment() {
    private var mAdapter: LoveAdapter? = null
    companion object {


        /**
         * @param path the access path of love list
         */
        @JvmStatic
        fun newInstance() =
                LoveFragment()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        // Set the adapter
        if (mAdapter == null || mAdapter?.listData!!.isEmpty()) {
            mAdapter = LoveAdapter(context!!)
            view!!.rv.layoutManager = LinearLayoutManager(context)
            view.rv.adapter = mAdapter
            view.sr.setOnRefreshListener { requestData() }
            view.sr.setEnableLoadMore(false)
            view.sr.autoRefresh()
        } else {
            view!!.rv.layoutManager = LinearLayoutManager(context)
            view.rv.adapter = mAdapter
            view.sr.setOnRefreshListener { requestData() }
            view.sr.setEnableLoadMore(false)
        }
        return view
    }

    private fun requestData() = DBSearch.searchLoves()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<RealmManager.DBResult<ArrayList<Love>>> { // 第三步：订阅

                // 第二步：初始化Observer
                private var i: Int = 0
                private var mDisposable: Disposable? = null

                override fun onSubscribe(@NonNull d: Disposable) {
                    mDisposable = d
                }

                override fun onNext(t: RealmManager.DBResult<ArrayList<Love>>) {
                    if (view == null) return

                    mAdapter?.setData(t.data!!)

                    view!!.sr.finishRefresh()
                }

                override fun onError(@NonNull e: Throwable) {
                    if (view == null) return
                    view!!.sr.finishRefresh()
                }

                override fun onComplete() {
                }
            })
}