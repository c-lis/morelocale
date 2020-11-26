package jp.co.c_lis.ccl.morelocale.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

/**
 * https://stackoverflow.com/a/33822747
 */
class WrapContentLinearLayoutManager(
        context: Context,
        orientation: Int,
        reversed: Boolean
) : LinearLayoutManager(context, orientation, reversed) {

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            // Do nothing
        }
    }
}
