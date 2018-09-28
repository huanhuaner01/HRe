package org.huan.hre.source

import io.reactivex.Observable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.ArrayList

/**
 * 资源网站：http://www.kanshutang.net
 */
class KanshugtangSource:Source {
    companion object {
        const val BASE_URL = "https://m.kanshutang.net"

        const val BOOK_PATH = "/xclass/0/1.html"

        const val CHAPTER_PATH = "/all.html"
    }
    override fun getBookListRefresh(path: String): Observable<BookListResp> {
        //创建一个上游 Observable：
        return io.reactivex.Observable.create<BookListResp> { emitter ->

            val books = ArrayList<Book>()
            try {

                val doc = Jsoup.connect("$BASE_URL$path").get()

                val list = doc.select("div.hot_sale")

                for (i: Element in list) {
                    val book = Book(BASE_URL,
                            "",
                            i.select("img.lazy").attr("data-original"),
                            i.select("p.title").text(),
                            i.select("p.author").text(),
                            i.select("p.review").text(),
                            i.select("div.score").text(),
                            "$BASE_URL${i.select("a").attr("href")}",
                            ""
                    )
                    books.add(book)

                }
                val en = doc.select("p.page").first()

                val str =en.select("input").attr("value").toString()
                val separate1 = str.split("/".toRegex())
                val next = en.select("a").first().attr("href")
//
                emitter.onNext(BookListResp(separate1[0].toInt(),separate1[1].toInt(),books,next))
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)

            }

        }
    }

    override fun getBookListLoadMore(path: String, page: Int, countPage: Int, nextPageHref: String): Observable<BookListResp> {
        //创建一个上游 Observable：
        return io.reactivex.Observable.create<BookListResp> { emitter ->

            val books = ArrayList<Book>()
            try {
                var url = nextPageHref
                if(!url.startsWith("/")) url = "/$nextPageHref"

                //还是一样先从一个URL加载一个Document对象。
                val doc = Jsoup.connect("$BASE_URL$url").get()

                //“椒麻鸡”和它对应的图片都在<div class="pic">中
                val list = doc.select("div.hot_sale")
                //使用Element.select(String selector)查找元素，使用Node.attr(String key)方法取得一个属性的值
                /*    Log.i("huan","the first p : "+list.get(0).)*/

                for (i: Element in list) {
                    val book = Book(BASE_URL,
                            "",
                            i.select("img.lazy").attr("data-original"),
                            i.select("p.title").text(),
                            i.select("p.author").text(),
                            i.select("p.review").text(),
                            i.select("div.score").text(),
                            "$BASE_URL${i.select("a").attr("href")}",
                            "")

                    books.add(book)

                }
                val en = doc.select("p.page").first()

                val str =en.select("input").attr("value").toString()
                val separate1 = str.split("/".toRegex())
                val next = en.select("a").first().attr("href")
                emitter.onNext(BookListResp(separate1[0].toInt(),separate1[1].toInt(),books,next))
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }

        }
    }

    override fun getChapterList(bookUrl: String): Observable<BookDetailResp> {
        return io.reactivex.Observable.create<BookDetailResp> { emitter ->
            val menus = ArrayList<Menu>()
            try {

                //还是一样先从一个URL加载一个Document对象。
                val doc = Jsoup.connect(bookUrl).get()

                //“椒麻鸡”和它对应的图片都在<div class="pic">中
                val em = doc.select("div#chapterlist")
                val list = em.select("a[href^=/]")
                //使用Element.select(String selector)查找元素，使用Node.attr(String key)方法取得一个属性的值
                /*    Log.i("huan","the first p : "+list.get(0).)*/

                for(i: Element in list ){
                    val menu =Menu(BASE_URL," ",i.text(),i.attr("href"))
                    menus.add(menu)

                }

                /*  //所需链接在<div class="detail">中的<a>标签里面
                  val url = doc.select("div.detail").select("a");
      //            Log.i("mytag", "url:" + url.get(i).attr("href"));

                  //原料在<p class="subcontent">中
                  val burden = doc.select("p.subcontent");
                  //对于一个元素中的文本，可以使用Element.text()方法
                  Log.i("mytag", "burden:" + burden.get(1).text());*/
                emitter.onNext(BookDetailResp(null,menus))
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)

            }

        }
    }

    override fun getDetail(url: String): Observable<String> {
        //创建一个上游 Observable：
        return io.reactivex.Observable.create<String> { emitter ->
            val content = StringBuilder()
            try {

                if(!url.isEmpty()) {
                    //还是一样先从一个URL加载一个Document对象。
                    val doc = Jsoup.connect("$BASE_URL$url").get()

                    val contentEs = doc.getElementById("chaptercontent").textNodes()
                    for (i in contentEs.indices) {
                        var temp = contentEs[i].text().trim({ it <= ' ' })
                        temp = temp.replace(" ".toRegex(), "").replace(" ".toRegex(), "")
                        if (temp.isNotEmpty()) {
                        content.append("\u3000\u3000" + temp)
                            content.append(temp)
                            if (i < contentEs.size - 1) {
                                content.append("\r\n\r")
                            }
                        }
                    }
                }


                emitter.onNext(content.toString())
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)

            }

        }
    }

    override fun searchBook(keyword: String): Observable<BookListResp> {
        return io.reactivex.Observable.create<BookListResp> { emitter ->
            val books = ArrayList<Book>()
            try {


                //还是一样先从一个URL加载一个Document对象。
                val doc = Jsoup.connect("$BASE_URL/s.php").data("keyword",keyword).data("t","1").post()

                val list = doc.select("div.hot_sale")

                for(i: Element in list ){
                    val book = Book(BASE_URL,
                            " ",
                            "",
                            i.select("p.title").text(),
                            i.select("p.author").first().text().split("|")[1],
                            i.select("p.author")[1].text(),
                            "",
                            "$BASE_URL${i.select("a").attr("href")}",
                            i.select("p.author").first().text().split("|")[0]
                    )
                    books.add(book)

                }
//                val bookListResp =  BookListResp()
                emitter.onNext(BookListResp(1,1,books,""))
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)

            }

        }
    }

    override fun searchBookloadMore(path: String, page: Int, countPage: Int, nextPageHref: String): Observable<BookListResp> {
        //创建一个上游 Observable：
        return io.reactivex.Observable.create<BookListResp> { emitter ->

            val books = ArrayList<Book>()
            try {
                var url = nextPageHref
                if (!url.startsWith("/")) url = "/$nextPageHref"

                //还是一样先从一个URL加载一个Document对象。
                val doc = Jsoup.connect("$BASE_URL$url").get()

                val list = doc.select("div.hot_sale")

                for (i: Element in list) {
                    val book = Book(BASE_URL,
                            " ",
                            "",
                            i.select("p.title").text(),
                            i.select("p.author").first().text().split("|")[1],
                            i.select("p.author")[1].text(),
                            "",
                            "$BASE_URL${i.select("a").attr("href")}",
                            i.select("p.author").first().text().split("|")[0]
                    )

                    books.add(book)

                }
//                val bookListResp =  BookListResp()
                emitter.onNext(BookListResp(1, 1, books, ""))
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)

            }
        }
    }

    override fun getSort(): Observable<List<Sort>> {
        //创建一个上游 Observable：
        return io.reactivex.Observable.create<List<Sort>> { emitter ->

            val sorts = mutableListOf<Sort>()
            try {


                val doc = Jsoup.connect("$BASE_URL$BOOK_PATH").get()

                val list = doc.select("nav.sortChannel_nav").select("a")


                for (i: Element in list) {
                    val sort = Sort(BASE_URL,i.text(),i.attr("href"))

                    sorts.add(sort)

                }
                emitter.onNext(sorts)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)

            }

        }
    }
}