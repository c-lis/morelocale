package jp.co.c_lis.ccl.morelocale.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.FragmentLocaleListBinding
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import jp.co.c_lis.ccl.morelocale.repository.LocaleRepository
import jp.co.c_lis.morelocale.MoreLocale
import timber.log.Timber
import java.lang.reflect.InvocationTargetException

class LocaleListFragment : Fragment() {

    private var binding: FragmentLocaleListBinding? = null

    private val viewModel by viewModels<LocaleListViewModel> {
        LocaleListViewModelProvider(LocaleRepository(requireContext().applicationContext))
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

        setHasOptionsMenu(true)

        adapter = LocaleListAdapter(
                LayoutInflater.from(context),
                lifecycleScope
        ) { localeItem ->
            Timber.d("Change locale ${localeItem.displayName}")
            setLocale(localeItem)
        }
    }

    private fun setLocale(localeItem: LocaleItem) {
        try {
            MoreLocale.setLocale(localeItem.locale)
        } catch (e: InvocationTargetException) {
            Timber.e(e, "InvocationTargetException")
        }
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

        viewModel.currentLocale.observe(viewLifecycleOwner, { currentLocale ->
            binding?.also {
                it.currentLocale = currentLocale

            }
        })

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

        viewModel.loadCurrentLocale(requireContext())
        viewModel.loadLocaleList(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.fragment_locale_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_locale -> {

            }
            R.id.menu_about -> {

            }
        }
        return super.onOptionsItemSelected(item)
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
}
