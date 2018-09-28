package org.huan.hre.source

object SourceFactory {


    fun Create(url:String):Source{
        when{
            url.startsWith( MiaoShuFangSource.BASE_URL,false) -> return MiaoShuFangSource()

            url.startsWith(KanshugtangSource.BASE_URL,false) -> return KanshugtangSource()

            else ->return MiaoShuFangSource()
        }
   }

}