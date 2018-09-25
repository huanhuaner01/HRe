package org.huan.hre.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.huan.hre.view.fragment.BookListFragment
import org.huan.hre.source.Sort

/**
 * 首页分类ViewPager的适配器
 */
class MainFragmentPageAdapter(fm: FragmentManager, fragments:List<BookListFragment>, sorts:List<Sort>) : FragmentPagerAdapter(fm) {
    private var mFragments:List<BookListFragment>?=null
    private var mSorts:List<Sort>?=null
    init {
        mFragments = fragments
        mSorts = sorts
    }
    override fun getItem(position: Int): Fragment {
        return mFragments!![position]
    }

    override fun getCount(): Int {
       return mFragments!!.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mSorts!![position].text
    }
}