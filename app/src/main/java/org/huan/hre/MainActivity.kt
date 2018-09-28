package org.huan.hre

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import org.huan.hre.realm.HistoryRO
import org.huan.hre.realm.RealmManager
import org.huan.hre.source.KanshugtangSource
import org.huan.hre.view.adapter.MainFragmentPageAdapter
import org.huan.hre.view.fragment.BookListFragment
import org.huan.hre.source.Sort
import org.huan.hre.source.SourceFactory


class MainActivity : AppCompatActivity() {
    private lateinit var mSearchView: SearchView
    private var web = KanshugtangSource.BASE_URL
    private lateinit var realm: Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        getSorts()

//        realm = Realm.getDefaultInstance()

//        simP()
    }

    private fun simP() {

        val history = HistoryRO()
        history.bookName = "1111"
        history.pathUrl = "2222"
        history.time = System.currentTimeMillis()
        history.web = "www"
        RealmManager.insertOrUpdate(history)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // Remember to close Realm when done.
    }
   private fun initTab(tabs:List<Sort>){
       if(tabs.isEmpty())return
       val fragments = mutableListOf<BookListFragment>()
       for(item in tabs){
           fragments.add(BookListFragment.newInstance(item))
       }
       vp_main.adapter = MainFragmentPageAdapter(supportFragmentManager, fragments, tabs)
       tab_main.setupWithViewPager(vp_main)
   }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu?.findItem(R.id.menu_search)
        val historyItem = menu?.findItem(R.id.menu_history)

        historyItem?.setOnMenuItemClickListener {
            onClickHistory()
            return@setOnMenuItemClickListener true

        }
        mSearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        //通过MenuItem得到SearchView
//        val mSearchView =  getActionView(searchItem)
        val mSearchAutoComplete = mSearchView.findViewById(R.id.search_src_text) as SearchView.SearchAutoComplete

        //设置输入框提示文字样式
        mSearchAutoComplete.setHintTextColor(resources.getColor(android.R.color.white))//设置提示文字颜色
        mSearchAutoComplete.setTextColor(resources.getColor(android.R.color.white))//设置内容文字颜色
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            private val TAG = javaClass.simpleName

            override fun onQueryTextChange(queryText: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(queryText: String): Boolean {
                Log.d(TAG, "onQueryTextSubmit = $queryText")

                search(queryText)
                mSearchView.clearFocus()

                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

 /*   override fun onMenuOpened(featureId: Int, menu: Menu?): Boolean {
        if (menu != null) {
            if (menu::class.simpleName.equals("MenuBuilder", true)) {
                try {

                    val method = menu::class.java.getDeclaredMethod("setOptionalIconsVisible", java.lang.Boolean.TYPE)
                    method.isAccessible = true
                    method.invoke(menu, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        return super.onMenuOpened(featureId, menu)
    }*/
    private fun getSorts(){
         SourceFactory.Create(web).getSort()
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe(object : Observer<List<Sort>> { // 第三步：订阅

               // 第二步：初始化Observer
               private var i: Int = 0
               private var mDisposable: Disposable? = null

               override fun onSubscribe(@NonNull d: Disposable) {
                   mDisposable = d
               }

               override fun onNext(t: List<Sort>) {
                   initTab(t)
               }

               override fun onError(@NonNull e: Throwable) {

               }

               override fun onComplete() {
               }
           })
    }
    private fun search(keyword: String) {
      val intent = Intent(this,SearchActivity::class.java)
        intent.putExtra(SearchActivity.SEARCH_WEB,web)
        intent.putExtra(SearchActivity.SEARCH_KEY_WORD,keyword)
        startActivity(intent)
    }

    private fun onClickHistory(){
        val intent = Intent(this,HistoryActivity::class.java)
        startActivity(intent)
    }


}
