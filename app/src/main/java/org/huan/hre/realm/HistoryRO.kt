package org.huan.hre.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class HistoryRO : RealmObject() {

    @PrimaryKey
    var bookName:String = ""

    var pathUrl:String = ""
    var time: Long = 0
    var web:String = ""
}