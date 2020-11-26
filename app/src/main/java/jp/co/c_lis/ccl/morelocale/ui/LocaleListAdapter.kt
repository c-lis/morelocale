package jp.co.c_lis.ccl.morelocale.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
        private val coroutineScope: CoroutineScope,
        private val menuCallback: MenuCallback? = null,
        private val onLocaleSelected: (localeItem: LocaleItem) -> Unit,
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
                inflater.inflate(R.layout.list_item_locale, parent, false),
                menuCallback,
                onLocaleSelected
        )
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

    class LocaleItemViewHolder(
            itemView: View,
            private val menuCallback: MenuCallback?,
            private val onLocaleSelected: (localeItem: LocaleItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ListItemLocaleBinding.bind(itemView)

        fun bind(localeItem: LocaleItem) {
            binding.locale = localeItem
            itemView.setOnClickListener {
                onLocaleSelected(localeItem)
            }
            binding.more.setOnClickListener {
                binding.more.setOnClickListener {
                    PopupMenu(itemView.context, it).also { popupMenu ->
                        popupMenu.menuInflater.inflate(
                                R.menu.list_item_locale,
                                popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.menu_edit -> menuCallback?.onEdit(localeItem)
                                R.id.menu_delete -> menuCallback?.onDelete(localeItem)
                            }
                            return@setOnMenuItemClickListener true
                        }
                    }.show()
                }
            }
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

    interface MenuCallback {
        fun onEdit(localeItem: LocaleItem)
        fun onDelete(localeItem: LocaleItem)
    }
}
