package org.huan.hre.realm

import android.provider.ContactsContract
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class History : RealmObject() {

    @PrimaryKey
    var bookName:String?=null

    var pathUrl:String?=null
    var time: Long? = 0
    var web:String?= null
}