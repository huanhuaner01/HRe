package org.huan.hre.util

import android.graphics.Rect
import android.widget.TextView


     fun getPage(textView: TextView, content:String): IntArray {
         textView.text = content
        val count = textView.lineCount
        val pCount = getPageLineCount(textView)
        val pageNum = Math.ceil(count / pCount.toDouble()).toInt()
        val page = IntArray(pageNum)
        for (i in 0 until pageNum) {
            if(i == pageNum-1){
                page[i] = content.length
            }else {
                page[i] = textView.layout.getLineEnd((i + 1) * pCount - 1)
            }
        }
        return page
    }

    private fun getPageLineCount(view: TextView): Int {    /*
     * The first row's height is different from other row.
     */
        val h = view.bottom - view.top - view.paddingTop
        val firstH = getLineHeight(0, view)
        val otherH = getLineHeight(1, view)
        return (h - firstH) / otherH + 1
    }
    private fun getLineHeight(line: Int, view: TextView): Int {
        val rect = Rect()
        view.getLineBounds(line, rect)
        return rect.bottom - rect.top
    }