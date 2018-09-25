package org.huan.hre.realm

import android.content.Context
import android.util.Log
import io.realm.rx.RealmObservableFactory
import io.realm.RealmObject.deleteFromRealm
import android.content.ClipData.Item
import io.realm.*
import io.realm.kotlin.deleteFromRealm
import java.sql.Date


/**
 * Realm数据库管理工具类
 * 功能：数据库初始化，数据增加，删除，修改，查询
 */
object RealmManager {


    open fun init(context: Context){
        Realm.init(context)
        val  configuration = RealmConfiguration.Builder()
                .schemaVersion(2) //设置数据库的版本号
                .rxFactory(RealmObservableFactory()) //支持RxJava
                .name("HRe") //数据库名字
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(configuration)
        Log.i("huan", Realm.getDefaultConfiguration().toString())
    }


    open fun add(data:RealmObject,listener: RealmListener?=null){
          val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync({
            realm -> realm.insertOrUpdate(data)
        }, {
            realm.close()
            listener?.onSuccess()
        }, {
            realm.close()
            listener?.onError(it)
        })
    }



    open fun<E : RealmModel> delete( clazz:Class<E>, key:String,value:Any,listener: RealmListener?=null){
//       if()
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync({
            realm ->
          /*  val item = realm.where(clazz).equalTo(key, value)
                    .findFirst()
            if (item != null) {
                item!!.deleteFromRealm()
            }
*/
        }, {
            realm.close()
            listener?.onSuccess()
        }, {
            realm.close()
            listener?.onError(it)
        })
    }

    open interface RealmListener{
        fun onSuccess()
        fun onError(t:Throwable)
    }
}