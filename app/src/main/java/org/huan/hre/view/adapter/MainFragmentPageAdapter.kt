package org.huan.hre.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import org.huan.hre.view.fragment.BookListFragment
import org.huan.hre.source.Sort

/**
 * 首页分类ViewPager的适配器
 */
class MainFragmentPageAdapter(fm: FragmentManager, fragments:List<Fragment>, sorts:List<String>) : FragmentStatePagerAdapter(fm) {
    private var mFragments:List<Fragment>?=null
    private var mSorts:List<String>?=null
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
        return mSorts!![position]
    }
}