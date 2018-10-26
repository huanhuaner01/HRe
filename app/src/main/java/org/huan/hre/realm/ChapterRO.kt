package org.huan.hre.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChapterRO: RealmObject() {
    @PrimaryKey
    var id:String = ""
    var name:String = ""
    var url:String = ""
    var status: Long = 0
    var bookName:String = ""
    var web:String=""
    var time: Long = 0
}