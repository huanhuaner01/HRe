package org.huan.hre

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history.*
import org.huan.hre.realm.DBSearch
import org.huan.hre.realm.RealmManager
import org.huan.hre.source.History
import org.huan.hre.view.adapter.HistoryAdapter
import org.huan.hre.view.adapter.ItemTouchHelperCallback

class HistoryActivity : AppCompatActivity() {
    private lateinit var mAdapter: HistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        rv_history.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rv_history.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mAdapter = HistoryAdapter(this)
        rv_history.adapter = mAdapter

        //先实例化Callback
        val callback = ItemTouchHelperCallback(mAdapter)
        //用Callback构造ItemtouchHelper
        val touchHelper = ItemTouchHelper(callback)
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(rv_history)

        sr_history.setOnRefreshListener { getHistory()}
        sr_history.setEnableLoadMore(false)
        sr_history.autoRefresh()
    }

    private fun getHistory() {
        DBSearch.searchHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RealmManager.DBResult<ArrayList<History>>> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: RealmManager.DBResult<ArrayList<History>>) {
                        sr_history.finishRefresh()
                        if(t.data!=null)
                        mAdapter.setData(t.data)


                    }

                    override fun onError(e: Throwable) {
                        sr_history.finishRefresh()
                    }

                })
    }
}
