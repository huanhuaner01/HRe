package org.huan.hre.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class LoveRO: RealmObject()  {
    @PrimaryKey
    var bookName:String = ""
    var imgUrl:String = ""
    var pathUrl:String = ""
    var time: Long = 0
    var lotestChapter:ChapterRO?=null
    var status:Int = 0
    var web:String = ""
}