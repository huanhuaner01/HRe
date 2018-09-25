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


    /**
     * @param[value] 接受以下类型的值，其他类型将抛出异常：
     * * [Boolean]
     * * [Short]
     * * [Int]
     * * [Long]
     * * [Float]
     * * [Double]
     * * [ByteArray]
     * * [Date]
     * * [String]
     * @param[caseSensitive] 仅当 [value] 为 `String` 时有效，指示比较时是否大小写敏感，默认为 `true`。
     */
    fun <E : RealmModel> delete(clazz: Class<E>, key: String, value: Any, caseSensitive: Boolean = true, listener: RealmListener? = null) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync({ realmDb ->
            val item = realmDb.where(clazz).run {
                when (value) {
                    is Boolean -> equalTo(key, value)
                    is Short -> equalTo(key, value)
                    is Int -> equalTo(key, value)
                    is Long -> equalTo(key, value)
                    is Float -> equalTo(key, value)
                    is Double -> equalTo(key, value)
                    is ByteArray -> equalTo(key, value)
                    is Date -> equalTo(key, value)
                    is String -> equalTo(key, value, if (caseSensitive) {
                        Case.SENSITIVE
                    } else {
                        Case.INSENSITIVE
                    })
                    else -> throw IllegalArgumentException("Unknown type ${value::class.simpleName}")
                }
            }.findFirst()

            item?.deleteFromRealm()

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