package jp.co.c_lis.ccl.morelocale.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.ListItemLocaleBinding
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocaleListAdapter(
        private val inflater: LayoutInflater,
        private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var localeItemList: List<LocaleItem> = ArrayList()
        set(value) {
            coroutineScope.launch(Dispatchers.Main) {
                val diffResult = calculateDiff(field, value)
                diffResult.dispatchUpdatesTo(this@LocaleListAdapter)
                field = value
            }
        }

    private suspend fun calculateDiff(oldList: List<LocaleItem>, newList: List<LocaleItem>) = withContext(Dispatchers.Default) {
        return@withContext DiffUtil.calculateDiff(LocaleListDiffCallback(oldList, newList))
    }

    override fun getItemCount() = localeItemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LocaleItemViewHolder(
                inflater.inflate(R.layout.list_item_locale, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LocaleItemViewHolder) {
            holder.bind(localeItemList[position])
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is LocaleItemViewHolder) {
            holder.unbind()
        }
    }

    class LocaleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ListItemLocaleBinding.bind(itemView)

        fun bind(localeItem: LocaleItem) {
            binding.locale = localeItem
        }

        fun unbind() {
            binding.unbind()
        }
    }

    class LocaleListDiffCallback(
            private val oldList: List<LocaleItem>,
            private val newList: List<LocaleItem>) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] === newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
