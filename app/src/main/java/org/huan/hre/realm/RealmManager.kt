package org.huan.hre.realm

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.realm.rx.RealmObservableFactory
import io.realm.*
import io.realm.kotlin.deleteFromRealm
import java.sql.Date


/**
 * Realm数据库管理工具类
 * 功能：数据库初始化，数据增加，删除，修改，查询
 */
object RealmManager {


    fun init(context: Context) {
        Realm.init(context)
        val configuration = RealmConfiguration.Builder()
                .schemaVersion(2) //设置数据库的版本号
                .rxFactory(RealmObservableFactory()) //支持RxJava
                .name("HRe") //数据库名字
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(configuration)
        Log.i("huan", Realm.getDefaultConfiguration().toString())
    }

    /**
     * 添加数据
     */
    fun insertOrUpdate(data: RealmObject): Observable<DBResult<Any>> = Observable.create<DBResult<Any>> {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction({
                realm.insertOrUpdate(data)
            })
            it.onNext(DBResult(200, "", null))
        } catch (e: Exception) {
            it.onError(e)
        } finally {
            realm.close()
        }
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
    fun <E : RealmModel> delete(clazz: Class<E>, key: String, value: Any, caseSensitive: Boolean = true): Observable<DBResult<Any>> =
            Observable.create<DBResult<Any>> {
                val realm = Realm.getDefaultInstance()
                try {
                    realm.executeTransaction { realmDb ->
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
                    }
                    it.onNext(DBResult(200, "", null))
                } catch (e: Exception) {
                    it.onError(e)
                } finally {
                    realm.close()
                }
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
    fun <E : RealmModel> search(clazz: Class<E>, key: String? = null, value: Any? = null, caseSensitive: Boolean = true): Observable<DBResult<List<E>>> =
            Observable.create<DBResult<List<E>>> {
                val realm = Realm.getDefaultInstance()
                try {
                    val item = if (key != null && value != null) {
                        realm.where(clazz).run {
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
                        }.findAll()
                    } else
                        realm.where(clazz).findAll()

                    it.onNext(DBResult(200, "", item.toList()))
                    it.onComplete()
                } catch (e: Exception) {
                    it.onError(e)
                } finally {
                    realm.close()
                }
            }

    data class DBResult<out T : Any>(val code: Int, val message: String, val data: T? = null)
}