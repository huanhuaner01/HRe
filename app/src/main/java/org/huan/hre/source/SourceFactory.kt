package org.huan.hre.source

object SourceFactory {

    @JvmStatic
    open fun Create(url:String):Source{
        when{
            url.startsWith( MiaoShuFangSource.BASE_URL,false) -> return MiaoShuFangSource()
            else ->return MiaoShuFangSource()
        }
   }

}