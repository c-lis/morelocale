package jp.co.c_lis.ccl.morelocale.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.FragmentLocaleListBinding
import jp.co.c_lis.ccl.morelocale.databinding.ListItemLocaleBinding
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import jp.co.c_lis.ccl.morelocale.repository.LocaleRepository
import timber.log.Timber

class LocaleListFragment : Fragment() {

    private var binding: FragmentLocaleListBinding? = null

    private val viewModel by viewModels<LocaleListViewModel> {
        LocaleListViewModelProvider(LocaleRepository())
    }

    companion object {
        val TAG = LocaleListFragment::class.java.simpleName

        fun getInstance(): LocaleListFragment {
            return LocaleListFragment()
        }
    }

    private var adapter: LocaleListAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        adapter = LocaleListAdapter(LayoutInflater.from(context))
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_locale_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.localeList.observe(viewLifecycleOwner, { localeItemList ->
            adapter?.also {
                Timber.d("localeItemList size ${localeItemList.size}")
                it.localeItemList = localeItemList
                it.notifyDataSetChanged()
            }
        })

        binding = FragmentLocaleListBinding.bind(view).also {
            it.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            it.recyclerView.adapter = adapter
        }

        viewModel.showLocaleList(requireContext().assets)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    inner class LocaleListViewModelProvider(
            private val localeRepository: LocaleRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LocaleListViewModel(localeRepository, lifecycleScope) as T
        }
    }

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
                binding.label.text = localeItem.country
            }

            fun unbind() {
                binding.unbind()
            }
        }
    }
}
