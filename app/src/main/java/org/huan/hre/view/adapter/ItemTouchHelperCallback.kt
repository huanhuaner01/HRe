package org.huan.hre.view.adapter

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

open class ItemTouchHelperCallback(adapter: ItemTouchHelperAdapter): ItemTouchHelper.Callback() {

    private var mAdapter: ItemTouchHelperAdapter? = null
    init {
        mAdapter = adapter
    }
    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN      //允许上下的拖动
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT  //只允许从右向左侧滑
        return makeMovementFlags(dragFlags,swipeFlags)

    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        //onItemMove是接口方法
        if(viewHolder !=null && target!=null) {
            mAdapter?.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        }
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        if(viewHolder!=null){
            mAdapter?.onItemDissmiss(viewHolder.adapterPosition)
        }
    }


}
 interface ItemTouchHelperAdapter {
    //数据交换
    fun  onItemMove(fromPosition:Int,toPosition:Int)
    //数据删除
    fun onItemDissmiss(position: Int)
 }