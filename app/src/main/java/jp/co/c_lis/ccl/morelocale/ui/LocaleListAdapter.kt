package jp.co.c_lis.ccl.morelocale.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.ListItemLocaleBinding
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem

class LocaleListAdapter(
        private val inflater: LayoutInflater
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var localeItemList: List<LocaleItem> = ArrayList()

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
}
