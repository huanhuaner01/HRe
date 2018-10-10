package org.huan.hre.realm

import io.reactivex.Observable
import io.realm.Realm
import io.realm.Sort
import org.huan.hre.source.Chapter
import org.huan.hre.source.History
import org.huan.hre.source.Love

object DBSearch {

    fun searchHistory(): Observable<RealmManager.DBResult<ArrayList<History>>> =
            Observable.create<RealmManager.DBResult<ArrayList<History>>> {
                val realm = Realm.getDefaultInstance()
                try {
                       val datas = arrayListOf<History>()
                       for(item in realm.where(HistoryRO::class.java).sort("time",Sort.DESCENDING).findAll()){
                           datas.add(History(item.bookName,item.pathUrl,item.time,item.web))
                       }
                    it.onNext(RealmManager.DBResult(200, "", datas))
                    it.onComplete()
                } catch (e: Exception) {
                    it.onError(e)
                } finally {
                    realm.close()
                }
            }

    fun searchLoves(): Observable<RealmManager.DBResult<ArrayList<Love>>> =
            Observable.create<RealmManager.DBResult<ArrayList<Love>>> {
                val realm = Realm.getDefaultInstance()
                try {
                    val datas = arrayListOf<Love>()
                    for(item in realm.where(LoveRO::class.java).sort("time",Sort.DESCENDING).findAll()){
                        datas.add(Love(item.bookName,item.pathUrl,item.imgUrl,item.lotestChapter?.name,item.web))
                    }
                    it.onNext(RealmManager.DBResult(200, "", datas))
                    it.onComplete()
                } catch (e: Exception) {
                    it.onError(e)
                } finally {
                    realm.close()
                }
            }
    fun searchLatestChapter(web:String,bookName:String): Observable<RealmManager.DBResult<Chapter>> =
            Observable.create<RealmManager.DBResult<Chapter>> {
                val realm = Realm.getDefaultInstance()
                try {
                    val data =  realm.where(ChapterRO::class.java).equalTo("web",web).equalTo("bookName",bookName).sort("time",Sort.DESCENDING).findFirst()
                    if(data != null) {
                        it.onNext(RealmManager.DBResult(200, "",
                                Chapter(data.name,data.url,data.status,data.bookName,data.web,data.time)))
                    }else{
                        it.onNext(RealmManager.DBResult(200, "", null))
                    }
                    it.onComplete()
                } catch (e: Exception) {
                    it.onError(e)
                } finally {
                    realm.close()
                }
            }
}