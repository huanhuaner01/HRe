package org.huan.hre.realm

import io.reactivex.Observable
import io.realm.Case
import io.realm.Realm
import io.realm.RealmModel
import org.huan.hre.source.History
import java.sql.Date

object DBSearch {

    fun searchHistory(): Observable<RealmManager.DBResult<ArrayList<History>>> =
            Observable.create<RealmManager.DBResult<ArrayList<History>>> {
                val realm = Realm.getDefaultInstance()
                try {
                       val datas = arrayListOf<History>()
                       for(item in realm.where(HistoryRO::class.java).findAll()){
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

}