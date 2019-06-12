package com.carson.signsystem.home.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.carson.signsystem.custom_views.MyCalendarView

class MyPagerAdapter constructor(context: Context, mCacheCalendars: List<MyCalendarView>) : PagerAdapter() {

    private var context: Context? = null
    private var mCacheCalendars: List<MyCalendarView>? = null
    var listener: MyCalendarView.OnChangeMonthListener? = null

    init {
        this.context = context
        this.mCacheCalendars = mCacheCalendars
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mCalendarView = mCacheCalendars!![position]
        mCalendarView.setMonthChangeListener(listener)
        container.addView(mCalendarView)
        return mCalendarView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return 12
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return o === view
    }
}