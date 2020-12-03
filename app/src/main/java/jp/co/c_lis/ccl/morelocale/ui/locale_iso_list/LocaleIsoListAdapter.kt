package jp.co.c_lis.ccl.morelocale.ui.locale_iso_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.ListItemLocaleIsoBinding
import jp.co.c_lis.ccl.morelocale.entity.LocaleIsoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocaleIsoListAdapter(
        private val inflater: LayoutInflater,
        private val coroutineScope: CoroutineScope,
        private val onLocaleIsoSelected: (localeIsoItem: LocaleIsoItem) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var localeIsoItemList: List<LocaleIsoItem> = ArrayList()
        set(value) {
            coroutineScope.launch(Dispatchers.Main) {
                val diffResult = calculateDiff(field, value)
                field = value
                diffResult.dispatchUpdatesTo(this@LocaleIsoListAdapter)
            }
        }

    private suspend fun calculateDiff(oldList: List<LocaleIsoItem>, newList: List<LocaleIsoItem>) = withContext(Dispatchers.Default) {
        return@withContext DiffUtil.calculateDiff(LocaleListDiffCallback(oldList, newList))
    }

    override fun getItemCount() = localeIsoItemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LocaleItemViewHolder(
                inflater.inflate(R.layout.list_item_locale_candidate, parent, false),
                onLocaleIsoSelected
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LocaleItemViewHolder) {
            holder.bind(localeIsoItemList[position])
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
            private val onLocaleSelected: (localeIsoItem: LocaleIsoItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ListItemLocaleIsoBinding.bind(itemView)

        fun bind(localeIsoItem: LocaleIsoItem) {
            binding.title = localeIsoItem.label
            binding.root.setOnClickListener {
                onLocaleSelected(localeIsoItem)
            }
        }

        fun unbind() {
            binding.unbind()
        }
    }

    class LocaleListDiffCallback(
            private val oldList: List<LocaleIsoItem>,
            private val newList: List<LocaleIsoItem>) : DiffUtil.Callback() {

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
